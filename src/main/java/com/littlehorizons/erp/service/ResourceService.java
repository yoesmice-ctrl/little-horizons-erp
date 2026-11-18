package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.ResourceDAO;
import com.littlehorizons.erp.model.Resource;
import com.littlehorizons.erp.model.ResourceLoan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceService {

    private final ResourceDAO resourceDAO;

    public ResourceService() {
        this.resourceDAO = new ResourceDAO();
    }

    public List<Resource> getAvailableResources() {
        return resourceDAO.getAllResources().stream()
                .filter(r -> "Disponible".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Resource> getAllResources() {
        return resourceDAO.getAllResources();
    }

    public boolean borrowResource(ResourceLoan loan) {
        return resourceDAO.createLoan(loan);
    }

    public boolean returnResource(int loanId, int resourceId, String borrowerType) {
        return resourceDAO.returnResource(loanId, resourceId, borrowerType);
    }

    /**
     * Uses Java Streams and Lambdas to filter overdue loans across both students and teachers.
     */
    public List<ResourceLoan> getOverdueLoans() {
        List<ResourceLoan> allActive = new ArrayList<>();
        allActive.addAll(resourceDAO.getActiveLoans("STUDENT"));
        allActive.addAll(resourceDAO.getActiveLoans("TEACHER"));

        LocalDate today = LocalDate.now();
        return allActive.stream()
                .filter(loan -> loan.getExpectedReturnDate().isBefore(today))
                .collect(Collectors.toList());
    }
}
