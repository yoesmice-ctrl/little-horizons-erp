package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.AcademicDAO;
import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Attendance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AcademicService {

    private final AcademicDAO academicDAO;

    public AcademicService() {
        this.academicDAO = new AcademicDAO();
    }

    public boolean registerGrade(AcademicRecord record) {
        if (record.getGradeValue().compareTo(BigDecimal.ZERO) < 0 ||
            record.getGradeValue().compareTo(new BigDecimal("5.0")) > 0) {
            return false;
        }
        return academicDAO.saveGrade(record);
    }

    /**
     * Calculates student average for a specific academic period using Java Streams.
     */
    public BigDecimal calculateStudentPeriodAverage(int studentId, int year, String period) {
        List<AcademicRecord> records = academicDAO.getStudentGrades(studentId);
        double avg = records.stream()
                .filter(r -> r.getAcademicYear() == year && r.getAcademicPeriod().equalsIgnoreCase(period))
                .mapToDouble(r -> r.getGradeValue().doubleValue())
                .average()
                .orElse(0.0);

        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Identifies students with more than 3 absences in the last 7 days using Stream groupingBy.
     */
    public Map<Integer, Long> getStudentsWithHighAbsences() {
        List<Attendance> recent = academicDAO.getRecentAttendances(7);
        return recent.stream()
                .filter(a -> "Ausente".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.groupingBy(Attendance::getStudentId, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean registerAttendance(Attendance attendance) {
        return academicDAO.saveAttendance(attendance);
    }

    public List<AcademicRecord> getStudentHistory(int studentId) {
        return academicDAO.getStudentGrades(studentId);
    }
}
