package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.Event;
import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.util.ErrorLogger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExtracurricularDAO {

    public List<Club> getAllActiveClubs() {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubes WHERE esta_activo = TRUE ORDER BY nombre_club";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Club c = new Club();
                c.setClubId(rs.getInt("id_club"));
                c.setClubName(rs.getString("nombre_club"));
                c.setDescription(rs.getString("descripcion"));
                c.setSupervisorTeacherId((Integer) rs.getObject("id_profesor_supervisor"));
                c.setMaxCapacity(rs.getInt("capacidad_maxima"));
                c.setMeetingSchedule(rs.getString("horario_reunion"));
                c.setActive(rs.getBoolean("esta_activo"));
                clubs.add(c);
            }
        } catch (SQLException e) {
            ErrorLogger.logError("ExtracurricularDAO.getAllActiveClubs", "Error fetching active clubs", e);
        }
        return clubs;
    }

    public int countActiveClubMembers(int clubId) {
        String sql = "SELECT COUNT(*) FROM membresias_club WHERE id_club = ? AND estado_membresia = 'Activa'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            ErrorLogger.logError("ExtracurricularDAO.countActiveClubMembers", "Error counting club members", e);
        }
        return 0;
    }

    public boolean enrollStudentInClub(int studentId, int clubId) {
        String sql = "INSERT INTO membresias_club (id_estudiante, id_club, fecha_ingreso, estado_membresia) VALUES (?, ?, ?, 'Activa')";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, clubId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ErrorLogger.logError("ExtracurricularDAO.enrollStudentInClub", "Error enrolling student in club", e);
            return false;
        }
    }

    public boolean registerEventParticipant(int eventId, int studentId, String role, String position) {
        String sql = "INSERT INTO participantes_evento (id_evento, id_estudiante, rol_participacion, posicion_obtenida) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE rol_participacion = VALUES(rol_participacion), posicion_obtenida = VALUES(posicion_obtenida)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, studentId);
            stmt.setString(3, role);
            stmt.setString(4, position);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ErrorLogger.logError("ExtracurricularDAO.registerEventParticipant", "Error registering event participant", e);
            return false;
        }
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE esta_activo = TRUE ORDER BY fecha_evento DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Event ev = new Event();
                ev.setEventId(rs.getInt("id_evento"));
                ev.setEventName(rs.getString("nombre_evento"));
                ev.setDescription(rs.getString("descripcion"));
                ev.setEventType(rs.getString("tipo_evento"));
                ev.setEventDate(rs.getDate("fecha_evento").toLocalDate());
                ev.setLocation(rs.getString("lugar"));
                ev.setHasCompetition(rs.getBoolean("tiene_competencia"));
                events.add(ev);
            }
        } catch (SQLException e) {
            ErrorLogger.logError("ExtracurricularDAO.getAllEvents", "Error fetching events", e);
        }
        return events;
    }
}
