package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AcademicRecord {
    private int recordId;
    private int studentId;
    private int subjectId;
    private int teacherId;
    private int academicYear;
    private String academicPeriod;
    private BigDecimal gradeValue;
    private String observations;
    private LocalDate recordDate;

    public AcademicRecord() {}

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }
    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }
    public String getAcademicPeriod() { return academicPeriod; }
    public void setAcademicPeriod(String academicPeriod) { this.academicPeriod = academicPeriod; }
    public BigDecimal getGradeValue() { return gradeValue; }
    public void setGradeValue(BigDecimal gradeValue) { this.gradeValue = gradeValue; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
}
