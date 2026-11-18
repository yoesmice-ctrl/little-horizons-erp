package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.util.ErrorLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcademicDAO {

    public boolean saveGrade(AcademicRecord record) {
        String sql = "INSERT INTO registros_academicos (id_estudiante, id_materia, id_profesor, año_academico, " +
                     "periodo_academico, valor_nota, observaciones, fecha_registro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE valor_nota = VALUES(valor_nota), observaciones = VALUES(observaciones)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, record.getStudentId());
            stmt.setInt(2, record.getSubjectId());
            stmt.setInt(3, record.getTeacherId());
            stmt.setInt(4, record.getAcademicYear());
            stmt.setString(5, record.getAcademicPeriod());
            stmt.setBigDecimal(6, record.getGradeValue());
            stmt.setString(7, record.getObservations());
            stmt.setDate(8, Date.valueOf(record.getRecordDate()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ErrorLogger.logError("AcademicDAO.saveGrade", "Failed to save grade for student ID: " + record.getStudentId(), e);
            return false;
        }
    }

    public List<AcademicRecord> getStudentGrades(int studentId) {
        List<AcademicRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM registros_academicos WHERE id_estudiante = ? ORDER BY año_academico DESC, periodo_academico";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AcademicRecord r = new AcademicRecord();
                    r.setRecordId(rs.getInt("id_registro"));
                    r.setStudentId(rs.getInt("id_estudiante"));
                    r.setSubjectId(rs.getInt("id_materia"));
                    r.setTeacherId(rs.getInt("id_profesor"));
                    r.setAcademicYear(rs.getInt("año_academico"));
                    r.setAcademicPeriod(rs.getString("periodo_academico"));
                    r.setGradeValue(rs.getBigDecimal("valor_nota"));
                    r.setObservations(rs.getString("observaciones"));
                    r.setRecordDate(rs.getDate("fecha_registro").toLocalDate());
                    records.add(r);
                }
            }
        } catch (SQLException e) {
            ErrorLogger.logError("AcademicDAO.getStudentGrades", "Failed to fetch grades for student ID: " + studentId, e);
        }
        return records;
    }

    public boolean saveAttendance(Attendance attendance) {
        String sql = "INSERT INTO asistencias (id_estudiante, id_materia, fecha_asistencia, estado_asistencia, observaciones, registrado_por) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE estado_asistencia = VALUES(estado_asistencia), observaciones = VALUES(observaciones)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attendance.getStudentId());
            stmt.setInt(2, attendance.getSubjectId());
            stmt.setDate(3, Date.valueOf(attendance.getAttendanceDate()));
            stmt.setString(4, attendance.getStatus());
            stmt.setString(5, attendance.getObservations());
            if (attendance.getRecordedByTeacherId() != null) stmt.setInt(6, attendance.getRecordedByTeacherId());
            else stmt.setNull(6, Types.INTEGER);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ErrorLogger.logError("AcademicDAO.saveAttendance", "Failed to save attendance for student ID: " + attendance.getStudentId(), e);
            return false;
        }
    }

    public List<Attendance> getRecentAttendances(int daysBack) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE fecha_asistencia >= DATE_SUB(CURDATE(), INTERVAL ? DAY)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, daysBack);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance a = new Attendance();
                    a.setAttendanceId(rs.getInt("id_asistencia"));
                    a.setStudentId(rs.getInt("id_estudiante"));
                    a.setSubjectId(rs.getInt("id_materia"));
                    a.setAttendanceDate(rs.getDate("fecha_asistencia").toLocalDate());
                    a.setStatus(rs.getString("estado_asistencia"));
                    a.setObservations(rs.getString("observaciones"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            ErrorLogger.logError("AcademicDAO.getRecentAttendances", "Error querying recent attendances", e);
        }
        return list;
    }
}
