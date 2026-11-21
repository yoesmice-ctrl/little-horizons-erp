package com.littlehorizons.erp.service;

import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.util.ErrorLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ClubReportService {

    public static final String REPORT_FILE = "reporte_ocupacion_clubes.txt";

    private static final String SQL =
            "SELECT c.id_club, c.nombre_club, c.capacidad_maxima, COUNT(m.id_club) AS inscritos " +
                    "FROM clubes c " +
                    "LEFT JOIN membresias_club m " +
                    "       ON m.id_club = c.id_club AND m.estado_membresia = 'Activa' " +
                    "GROUP BY c.id_club, c.nombre_club, c.capacidad_maxima";

    private static final String JUSTIFICATION =
            "JUSTIFICACIÓN DE NEGOCIO\n" +
                    "Este reporte permite a la Coordinación Académica ver, en un solo documento, qué clubes\n" +
                    "extracurriculares están llenos o en sobrecupo y cuáles tienen cupos sin usar. Con eso puede:\n" +
                    "(1) reasignar estudiantes o abrir nuevos grupos antes de que se pierda la calidad de la\n" +
                    "atención o se supere la capacidad segura; (2) asignar tutores y espacios según la demanda\n" +
                    "real; y (3) decidir qué actividades reforzar, promocionar o replantear, apoyándose en datos\n" +
                    "y no en percepciones.\n";

    public static class ClubOccupancy {
        private final int id;
        private final String name;
        private final int capacity;
        private final int enrolled;

        public ClubOccupancy(int id, String name, int capacity, int enrolled) {
            this.id = id;
            this.name = name;
            this.capacity = capacity;
            this.enrolled = enrolled;
        }

        public int id() { return id; }
        public String name() { return name; }
        public int capacity() { return capacity; }
        public int enrolled() { return enrolled; }

        public int available() {
            return Math.max(0, capacity - enrolled);
        }

        public double occupancyPct() {
            return capacity > 0 ? enrolled * 100.0 / capacity : 0.0;
        }

        public boolean isFull() {
            return capacity > 0 && enrolled == capacity;
        }

        public boolean isOverbooked() {
            return enrolled > capacity;
        }

        public String status() {
            if (isOverbooked()) return "SOBRECUPO";
            if (isFull()) return "LLENO";
            if (enrolled == 0) return "SIN INSCRITOS";
            return "DISPONIBLE";
        }
    } public Optional<Path> generateReport() {
        try {
            List<ClubOccupancy> clubs = loadClubs().stream()
                    .sorted(Comparator.comparingDouble(ClubOccupancy::occupancyPct).reversed()
                            .thenComparing(ClubOccupancy::name))
                    .toList();
            Path file = Path.of(REPORT_FILE);
            writeReport(file, clubs);
            return Optional.of(file);
        } catch (SQLException | IOException e) {
            ErrorLogger.logError("ClubReportService.generateReport", "Error generando " + REPORT_FILE, e);
            return Optional.empty();
        }
    }

    private List<ClubOccupancy> loadClubs() throws SQLException {
        List<ClubOccupancy> clubs = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clubs.add(new ClubOccupancy(
                        rs.getInt("id_club"),
                        rs.getString("nombre_club"),
                        rs.getInt("capacidad_maxima"),
                        rs.getInt("inscritos")));
            }
        }
        return clubs;
    }

    private void writeReport(Path file, List<ClubOccupancy> clubs) throws IOException {
        int totalCapacity = clubs.stream().mapToInt(ClubOccupancy::capacity).sum();
        int totalEnrolled = clubs.stream().mapToInt(ClubOccupancy::enrolled).sum();
        int totalAvailable = clubs.stream().mapToInt(ClubOccupancy::available).sum();
        double globalPct = totalCapacity > 0 ? totalEnrolled * 100.0 / totalCapacity : 0.0;
        long full = clubs.stream().filter(ClubOccupancy::isFull).count();
        long overbooked = clubs.stream().filter(ClubOccupancy::isOverbooked).count();
        long empty = clubs.stream().filter(c -> c.enrolled() == 0).count();
        Optional<ClubOccupancy> top = clubs.stream().findFirst();String line = "=".repeat(86);
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write(line); w.newLine();
            w.write("REPORTE DE OCUPACIÓN DE CLUBES EXTRACURRICULARES - COLEGIO LITTLE HORIZONS"); w.newLine();
            w.write(line); w.newLine();
            w.write(JUSTIFICATION); w.newLine();
            w.write(line); w.newLine();

            w.write(String.format(Locale.US, "%-4s %-30s %8s %10s %10s %10s  %s",
                    "ID", "Club", "Cupo", "Inscritos", "Disponib.", "Ocupación", "Estado"));
            w.newLine();
            w.write("-".repeat(86)); w.newLine();
            for (ClubOccupancy c : clubs) {
                String name = c.name().length() > 30 ? c.name().substring(0, 27) + "..." : c.name();
                w.write(String.format(Locale.US, "%-4d %-30s %8d %10d %10d %9.1f%%  %s",
                        c.id(), name, c.capacity(), c.enrolled(), c.available(),
                        c.occupancyPct(), c.status()));
                w.newLine();
            }

            w.write(line); w.newLine();
            w.write("MÉTRICAS GLOBALES"); w.newLine();
            w.write("Total de clubes: " + clubs.size()); w.newLine();
            w.write("Cupo total: " + totalCapacity); w.newLine();
            w.write("Estudiantes inscritos (membresías activas): " + totalEnrolled); w.newLine();
            w.write("Cupos disponibles: " + totalAvailable); w.newLine();
            w.write(String.format(Locale.US, "Ocupación global: %.1f%%", globalPct)); w.newLine();
            w.write("Clubes llenos: " + full); w.newLine();
            w.write("Clubes en sobrecupo: " + overbooked); w.newLine();
            w.write("Clubes sin inscritos: " + empty); w.newLine();
            top.ifPresent(c -> {
                try {
                    w.write(String.format(Locale.US, "Club con mayor ocupación: %s (%.1f%%)",
                            c.name(), c.occupancyPct()));
                    w.newLine();
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            });
            w.write(line); w.newLine();
        } catch (java.io.UncheckedIOException e) {
            throw e.getCause();
        }
    }
}