package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.util.ErrorLogger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {

    public boolean insertStudent(Student student) {
        String sql = "INSERT INTO estudiantes (tipo_documento, numero_documento, primer_nombre, segundo_nombre, " +
                     "primer_apellido, segundo_apellido, fecha_nacimiento, genero, direccion, telefono, " +
                     "correo_electronico, tipo_sangre, alergias, condiciones_medicas, fecha_ingreso, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getDocumentType());
            stmt.setString(2, student.getDocumentNumber());
            stmt.setString(3, student.getFirstName());
            stmt.setString(4, student.getMiddleName());
            stmt.setString(5, student.getLastName());
            stmt.setString(6, student.getSecondLastName());
            stmt.setDate(7, Date.valueOf(student.getBirthDate()));
            stmt.setString(8, student.getGender());
            stmt.setString(9, student.getAddress());
            stmt.setString(10, student.getPhone());
            stmt.setString(11, student.getEmail());
            stmt.setString(12, student.getBloodType());
            stmt.setString(13, student.getAllergies());
            stmt.setString(14, student.getMedicalConditions());
            stmt.setDate(15, Date.valueOf(student.getEnrollmentDate()));
            stmt.setString(16, student.getStatus() != null ? student.getStatus() : "Activo");

            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) student.setStudentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            ErrorLogger.logError("StudentDAO.insertStudent", "Failed to insert student: " + student.getDocumentNumber(), e);
        }
        return false;
    }

    public List<Student> findAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes ORDER BY primer_apellido, primer_nombre";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            ErrorLogger.logError("StudentDAO.findAllStudents", "Error retrieving students list", e);
        }
        return students;
    }

    public Optional<Student> findByDocument(String documentNumber) {
        String sql = "SELECT * FROM estudiantes WHERE numero_documento = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documentNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            ErrorLogger.logError("StudentDAO.findByDocument", "Error searching student: " + documentNumber, e);
        }
        return Optional.empty();
    }

    public boolean enrollStudentInGrade(int studentId, int gradeId, int academicYear) {
        String sql = "INSERT INTO matriculas (id_estudiante, id_grado, año_academico, estado_matricula, fecha_matricula) " +
                     "VALUES (?, ?, ?, 'Activa', ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, gradeId);
            stmt.setInt(3, academicYear);
            stmt.setDate(4, Date.valueOf(LocalDate.now()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ErrorLogger.logError("StudentDAO.enrollStudentInGrade", "Error enrolling student ID: " + studentId, e);
            return false;
        }
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("id_estudiante"));
        s.setDocumentType(rs.getString("tipo_documento"));
        s.setDocumentNumber(rs.getString("numero_documento"));
        s.setFirstName(rs.getString("primer_nombre"));
        s.setMiddleName(rs.getString("segundo_nombre"));
        s.setLastName(rs.getString("primer_apellido"));
        s.setSecondLastName(rs.getString("segundo_apellido"));
        s.setBirthDate(rs.getDate("fecha_nacimiento").toLocalDate());
        s.setGender(rs.getString("genero"));
        s.setAddress(rs.getString("direccion"));
        s.setPhone(rs.getString("telefono"));
        s.setEmail(rs.getString("correo_electronico"));
        s.setBloodType(rs.getString("tipo_sangre"));
        s.setAllergies(rs.getString("alergias"));
        s.setMedicalConditions(rs.getString("condiciones_medicas"));
        s.setEnrollmentDate(rs.getDate("fecha_ingreso").toLocalDate());
        s.setStatus(rs.getString("estado"));
        return s;
    }
}
