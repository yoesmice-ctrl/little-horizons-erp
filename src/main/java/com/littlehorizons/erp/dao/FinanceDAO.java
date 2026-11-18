package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.util.ErrorLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FinanceDAO {

    public boolean registerPaymentAndInvoice(Payment payment, Invoice invoice) {
        String sqlPayment = "INSERT INTO pagos (id_estudiante, id_acudiente, id_concepto, monto_pagado, fecha_pago, " +
                            "metodo_pago, numero_recibo, año_academico, periodo_academico, notas) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlInvoice = "INSERT INTO facturas (numero_factura, id_pago, fecha_emision, cufe) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtPay = conn.prepareStatement(sqlPayment, Statement.RETURN_GENERATED_KEYS)) {
                stmtPay.setInt(1, payment.getStudentId());
                if (payment.getGuardianId() != null) stmtPay.setInt(2, payment.getGuardianId());
                else stmtPay.setNull(2, Types.INTEGER);
                stmtPay.setInt(3, payment.getConceptId());
                stmtPay.setBigDecimal(4, payment.getAmountPaid());
                stmtPay.setDate(5, Date.valueOf(payment.getPaymentDate()));
                stmtPay.setString(6, payment.getPaymentMethod());
                stmtPay.setString(7, payment.getReceiptNumber());
                if (payment.getAcademicYear() != null) stmtPay.setInt(8, payment.getAcademicYear());
                else stmtPay.setNull(8, Types.INTEGER);
                stmtPay.setString(9, payment.getAcademicPeriod());
                stmtPay.setString(10, payment.getNotes());

                stmtPay.executeUpdate();
                try (ResultSet rs = stmtPay.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setPaymentId(rs.getInt(1));
                        invoice.setPaymentId(payment.getPaymentId());
                    }
                }
            }

            try (PreparedStatement stmtInv = conn.prepareStatement(sqlInvoice, Statement.RETURN_GENERATED_KEYS)) {
                stmtInv.setString(1, invoice.getInvoiceNumber());
                stmtInv.setInt(2, invoice.getPaymentId());
                stmtInv.setDate(3, Date.valueOf(invoice.getIssueDate()));
                stmtInv.setString(4, invoice.getCufe());
                stmtInv.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            ErrorLogger.logError("FinanceDAO.registerPaymentAndInvoice", "Transaction failed while registering payment", e);
            return false;
        }
    }

    public List<Payment> getPaymentsByStudent(int studentId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE id_estudiante = ? ORDER BY fecha_pago DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setPaymentId(rs.getInt("id_pago"));
                    p.setStudentId(rs.getInt("id_estudiante"));
                    p.setGuardianId((Integer) rs.getObject("id_acudiente"));
                    p.setConceptId(rs.getInt("id_concepto"));
                    p.setAmountPaid(rs.getBigDecimal("monto_pagado"));
                    p.setPaymentDate(rs.getDate("fecha_pago").toLocalDate());
                    p.setPaymentMethod(rs.getString("metodo_pago"));
                    p.setReceiptNumber(rs.getString("numero_recibo"));
                    p.setNotes(rs.getString("notas"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            ErrorLogger.logError("FinanceDAO.getPaymentsByStudent", "Error querying student payments", e);
        }
        return list;
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM pagos ORDER BY fecha_pago DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Payment p = new Payment();
                p.setPaymentId(rs.getInt("id_pago"));
                p.setStudentId(rs.getInt("id_estudiante"));
                p.setConceptId(rs.getInt("id_concepto"));
                p.setAmountPaid(rs.getBigDecimal("monto_pagado"));
                p.setPaymentDate(rs.getDate("fecha_pago").toLocalDate());
                p.setPaymentMethod(rs.getString("metodo_pago"));
                p.setReceiptNumber(rs.getString("numero_recibo"));
                list.add(p);
            }
        } catch (SQLException e) {
            ErrorLogger.logError("FinanceDAO.getAllPayments", "Error querying all payments", e);
        }
        return list;
    }
}
