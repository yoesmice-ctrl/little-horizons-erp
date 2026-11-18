package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.ExtracurricularDAO;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.Event;

import java.util.List;

public class ExtracurricularService {

    private final ExtracurricularDAO extracurricularDAO;

    public ExtracurricularService() {
        this.extracurricularDAO = new ExtracurricularDAO();
    }

    public List<Club> getActiveClubs() {
        return extracurricularDAO.getAllActiveClubs();
    }

    public String enrollStudentInClub(int studentId, int clubId) {
        Club targetClub = extracurricularDAO.getAllActiveClubs().stream()
                .filter(c -> c.getClubId() == clubId)
                .findFirst()
                .orElse(null);

        if (targetClub == null) {
            return "Club not found or inactive.";
        }

        int currentMembers = extracurricularDAO.countActiveClubMembers(clubId);
        if (currentMembers >= targetClub.getMaxCapacity()) {
            return "Club has reached its maximum capacity (" + targetClub.getMaxCapacity() + " students).";
        }

        boolean enrolled = extracurricularDAO.enrollStudentInClub(studentId, clubId);
        return enrolled ? "SUCCESS" : "Database error enrolling student in club.";
    }

    public List<Event> getActiveEvents() {
        return extracurricularDAO.getAllEvents();
    }

    public boolean registerEventParticipation(int eventId, int studentId, String role, String position) {
        return extracurricularDAO.registerEventParticipant(eventId, studentId, role, position);
    }
}
