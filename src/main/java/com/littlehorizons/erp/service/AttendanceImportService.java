package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.AcademicDAO;
import com.littlehorizons.erp.util.ErrorLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Set;
import java.util.regex.Pattern;

public class AttendanceImportService {

    public static final String FILE_NAME = "asistencias_importar.csv";
    public static final String EXPECTED_HEADER = "id_estudiante;id_materia;fecha;estado";

    private static final Set<String> VALID_STATES =
            Set.of("Presente", "Ausente", "Tardanza", "Excusa Médica");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    public enum Status { FILE_NOT_FOUND, INVALID_HEADER, READ_ERROR, COMPLETED }

    public record ImportResult(Status status, int total, int success, int failed) {
        static ImportResult of(Status status) {
            return new ImportResult(status, 0, 0, 0);
        }
    }

    private final AcademicDAO academicDAO;

    public AttendanceImportService() {
        this(new AcademicDAO());
    }

    public AttendanceImportService(AcademicDAO academicDAO) {
        this.academicDAO = academicDAO;
    }

    public ImportResult importAttendances() {
        Path file = Path.of(FILE_NAME);
        if (!Files.isRegularFile(file)) {
            return ImportResult.of(Status.FILE_NOT_FOUND);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8))) {

            String header = reader.readLine();
            if (header == null || !EXPECTED_HEADER.equals(stripBom(header))) {
                return ImportResult.of(Status.INVALID_HEADER);
            }

            int success = 0;
            int failed = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                if (processLine(line)) {
                    success++;
                } else {
                    failed++;
                }
            }
            return new ImportResult(Status.COMPLETED, success + failed, success, failed);

        } catch (IOException e) {
            ErrorLogger.logError("AttendanceImportService.importAttendances", "Error leyendo " + FILE_NAME, e);
            return ImportResult.of(Status.READ_ERROR);
        }
    }

    private boolean processLine(String line) {
        try {
            String[] fields = line.split(";", -1);
            if (fields.length != 4) {
                return false;
            }

            int studentId = Integer.parseInt(fields[0]);
            int subjectId = Integer.parseInt(fields[1]);

            String dateText = fields[2];
            if (!DATE_PATTERN.matcher(dateText).matches()) {
                return false;
            }
            LocalDate date = LocalDate.parse(dateText, DATE_FORMAT);

            String state = fields[3];
            if (!VALID_STATES.contains(state)) {
                return false;
            }

            if (!academicDAO.existsStudent(studentId) || !academicDAO.existsSubject(subjectId)) {
                return false;
            }

            return academicDAO.insertAttendance(studentId, subjectId, date, state);

        } catch (NumberFormatException | DateTimeParseException e) {
            return false;
        } catch (SQLException e) {
            ErrorLogger.logError("AttendanceImportService.processLine", "Error importando asistencia: " + line, e);
            return false;
        }
    }

    private static String stripBom(String text) {
        return text.startsWith("\uFEFF") ? text.substring(1) : text;
    }
}
