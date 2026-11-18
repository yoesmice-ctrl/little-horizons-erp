package com.littlehorizons.erp.model;

import java.time.LocalDate;

public class Attendance {
    private int attendanceId;
    private int studentId;
    private int subjectId;
    private LocalDate attendanceDate;
    private String status;
    private String observations;
    private Integer recordedByTeacherId;

    public Attendance() {}

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public Integer getRecordedByTeacherId() { return recordedByTeacherId; }
    public void setRecordedByTeacherId(Integer recordedByTeacherId) { this.recordedByTeacherId = recordedByTeacherId; }
}
