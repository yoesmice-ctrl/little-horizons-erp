package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Resource;
import com.littlehorizons.erp.model.ResourceLoan;
import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.util.ErrorLogger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public List<Resource> getAllResources() {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT * FROM recursos ORDER BY nombre_recurso";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Resource r = new Resource();
                r.setResourceId(rs.getInt("id_recurso"));
                r.setResourceCode(rs.getString("codigo_recurso"));
                r.setResourceName(rs.getString("nombre_recurso"));
                r.setCategoryId(rs.getInt("id_categoria"));
                r.setDescription(rs.getString("descripcion"));
                r.setPhysicalLocation(rs.getString("ubicacion_fisica"));
                r.setStatus(rs.getString("estado_recurso"));
                resources.add(r);
            }
        } catch (SQLException e) {
            ErrorLogger.logError("ResourceDAO.getAllResources", "Error fetching resources", e);
        }
        return resources;
    }

    public boolean createLoan(ResourceLoan loan) {
        String table = "STUDENT".equalsIgnoreCase(loan.getBorrowerType()) ? "prestamos_estudiante" : "prestamos_profesor";
        String idCol = "STUDENT".equalsIgnoreCase(loan.getBorrowerType()) ? "id_estudiante" : "id_profesor";

        String sqlInsert = "INSERT INTO " + table + " (id_recurso, " + idCol + ", fecha_prestamo, fecha_devolucion_esperada, estado_prestamo, observaciones) " +
                           "VALUES (?, ?, ?, ?, 'Activo', ?)";
        String sqlUpdateResource = "UPDATE recursos SET estado_recurso = 'Prestado' WHERE id_recurso = ? AND estado_recurso = 'Disponible'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateResource)) {
                stmtUpdate.setInt(1, loan.getResourceId());
                if (stmtUpdate.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                stmtInsert.setInt(1, loan.getResourceId());
                stmtInsert.setInt(2, loan.getBorrowerId());
                stmtInsert.setDate(3, Date.valueOf(loan.getLoanDate()));
                stmtInsert.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
                stmtInsert.setString(5, loan.getObservations());
                stmtInsert.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            ErrorLogger.logError("ResourceDAO.createLoan", "Transaction failed creating resource loan", e);
            return false;
        }
    }

    public boolean returnResource(int loanId, int resourceId, String borrowerType) {
        String table = "STUDENT".equalsIgnoreCase(borrowerType) ? "prestamos_estudiante" : "prestamos_profesor";
        String sqlLoan = "UPDATE " + table + " SET estado_prestamo = 'Devuelto', fecha_devolucion_real = ? WHERE id_prestamo = ?";
        String sqlRes = "UPDATE recursos SET estado_recurso = 'Disponible' WHERE id_recurso = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt1 = conn.prepareStatement(sqlLoan);
                 PreparedStatement stmt2 = conn.prepareStatement(sqlRes)) {
                stmt1.setDate(1, Date.valueOf(LocalDate.now()));
                stmt1.setInt(2, loanId);
                stmt1.executeUpdate();

                stmt2.setInt(1, resourceId);
                stmt2.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            ErrorLogger.logError("ResourceDAO.returnResource", "Failed returning resource ID: " + resourceId, e);
            return false;
        }
    }

    public List<ResourceLoan> getActiveLoans(String borrowerType) {
        List<ResourceLoan> loans = new ArrayList<>();
        String table = "STUDENT".equalsIgnoreCase(borrowerType) ? "prestamos_estudiante" : "prestamos_profesor";
        String idCol = "STUDENT".equalsIgnoreCase(borrowerType) ? "id_estudiante" : "id_profesor";
        String sql = "SELECT * FROM " + table + " WHERE estado_prestamo IN ('Activo', 'Vencido')";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ResourceLoan loan = new ResourceLoan();
                loan.setLoanId(rs.getInt("id_prestamo"));
                loan.setResourceId(rs.getInt("id_recurso"));
                loan.setBorrowerId(rs.getInt(idCol));
                loan.setBorrowerType(borrowerType);
                loan.setLoanDate(rs.getDate("fecha_prestamo").toLocalDate());
                loan.setExpectedReturnDate(rs.getDate("fecha_devolucion_esperada").toLocalDate());
                loan.setStatus(rs.getString("estado_prestamo"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            ErrorLogger.logError("ResourceDAO.getActiveLoans", "Error fetching active loans", e);
        }
        return loans;
    }
}
