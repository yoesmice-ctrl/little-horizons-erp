package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.StudentDAO;
import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.service.*;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.MainMenuView;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MainController {

    private final MainMenuView mainView;
    private final StudentDAO studentDAO;
    private final AcademicService academicService;
    private final ExtracurricularService extracurricularService;
    private final FinanceService financeService;
    private final ResourceService resourceService;

    public MainController() {
        this.mainView = new MainMenuView();
        this.studentDAO = new StudentDAO();
        this.academicService = new AcademicService();
        this.extracurricularService = new ExtracurricularService();
        this.financeService = new FinanceService();
        this.resourceService = new ResourceService();
    }

    public void start() {
        boolean running = true;
        while (running) {
            int option = mainView.showMainMenu();
            switch (option) {
                case 1 -> handleStudentModule();
                case 2 -> handleAcademicModule();
                case 3 -> handleExtracurricularModule();
                case 4 -> handleFinanceModule();
                case 5 -> handleResourceModule();
                case 6 -> handleReportsModule();
                case 7 -> {
                    System.out.println("\nShutting down Little Horizons ERP. Goodbye!");
                    running = false;
                }
                case 8 -> {
                    var r = new AttendanceImportService().importAttendances();
                    switch (r.status()) {
                        case FILE_NOT_FOUND -> mainView.showAttendanceImportFileNotFound();
                        case INVALID_HEADER -> mainView.showAttendanceImportInvalidHeader();
                        case READ_ERROR -> mainView.showAttendanceImportReadError();
                        case COMPLETED -> mainView.showAttendanceImportReport(r.total(), r.success(), r.failed());
                    }
                }
            }
        }
    }

    private void handleStudentModule() {
        System.out.println("\n--- STUDENT MANAGEMENT ---");
        System.out.println("1. List All Students");
        System.out.println("2. Register New Student");
        System.out.println("3. Enroll Student in Grade");
        System.out.println("0. Back to Main Menu");
        int opt = InputValidator.readIntInRange("Choose an option: ", 0, 3);

        switch (opt) {
            case 1 -> {
                List<Student> students = studentDAO.findAllStudents();
                System.out.printf("%-5s | %-15s | %-30s | %-10s%n", "ID", "Document", "Full Name", "Status");
                System.out.println("---------------------------------------------------------------------");
                students.forEach(s -> System.out.printf("%-5d | %-15s | %-30s | %-10s%n",
                        s.getStudentId(), s.getDocumentNumber(), s.getFullName(), s.getStatus()));
            }
            case 2 -> {
                Student s = new Student();
                s.setDocumentType(InputValidator.readNonEmptyString("Document Type (TI/CC/CE): "));
                s.setDocumentNumber(InputValidator.readNonEmptyString("Document Number: "));
                s.setFirstName(InputValidator.readNonEmptyString("First Name: "));
                s.setMiddleName(InputValidator.readOptionalString("Middle Name (optional): "));
                s.setLastName(InputValidator.readNonEmptyString("Last Name: "));
                s.setSecondLastName(InputValidator.readOptionalString("Second Last Name (optional): "));
                s.setBirthDate(InputValidator.readDate("Birth Date"));
                s.setGender(InputValidator.readNonEmptyString("Gender (M/F/Otro): "));
                s.setAddress(InputValidator.readOptionalString("Address: "));
                s.setPhone(InputValidator.readOptionalString("Phone: "));
                s.setEmail(InputValidator.readOptionalString("Email: "));
                s.setEnrollmentDate(LocalDate.now());
                s.setStatus("Activo");

                if (studentDAO.insertStudent(s)) {
                    System.out.println("[OK] Student registered successfully with ID: " + s.getStudentId());
                } else {
                    System.out.println("[ERROR] Could not register student. Check error_log.txt.");
                }
            }
            case 3 -> {
                int studentId = InputValidator.readIntInRange("Student ID: ", 1, 99999);
                int gradeId = InputValidator.readIntInRange("Grade ID (1=Preescolar .. 12=Once): ", 1, 12);
                int year = InputValidator.readIntInRange("Academic Year (e.g., 2026): ", 2020, 2035);
                if (studentDAO.enrollStudentInGrade(studentId, gradeId, year)) {
                    System.out.println("[OK] Student enrolled successfully.");
                } else {
                    System.out.println("[ERROR] Enrollment failed. Check error_log.txt.");
                }
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleAcademicModule() {
        System.out.println("\n--- ACADEMIC MANAGEMENT ---");
        System.out.println("1. Register Grade (0.0 - 5.0)");
        System.out.println("2. View Student Grades & Period Average");
        System.out.println("3. Record Daily Attendance");
        System.out.println("0. Back to Main Menu");
        int opt = InputValidator.readIntInRange("Choose an option: ", 0, 3);

        switch (opt) {
            case 1 -> {
                AcademicRecord r = new AcademicRecord();
                r.setStudentId(InputValidator.readIntInRange("Student ID: ", 1, 99999));
                r.setSubjectId(InputValidator.readIntInRange("Subject ID: ", 1, 99999));
                r.setTeacherId(InputValidator.readIntInRange("Teacher ID: ", 1, 99999));
                r.setAcademicYear(InputValidator.readIntInRange("Academic Year: ", 2020, 2035));
                r.setAcademicPeriod(InputValidator.readNonEmptyString("Period (I, II, III, IV, Final): "));
                r.setGradeValue(InputValidator.readBigDecimalInRange("Grade (0.0 - 5.0): ", 0.0, 5.0));
                r.setObservations(InputValidator.readOptionalString("Observations: "));
                r.setRecordDate(LocalDate.now());

                if (academicService.registerGrade(r)) {
                    System.out.println("[OK] Grade saved successfully.");
                } else {
                    System.out.println("[ERROR] Failed to save grade.");
                }
            }
            case 2 -> {
                int studentId = InputValidator.readIntInRange("Student ID: ", 1, 99999);
                int year = InputValidator.readIntInRange("Academic Year: ", 2020, 2035);
                String period = InputValidator.readNonEmptyString("Period (I, II, III, IV, Final): ");

                List<AcademicRecord> history = academicService.getStudentHistory(studentId);
                history.forEach(rec -> System.out.printf("Year: %d | Period: %-5s | Subject ID: %-4d | Grade: %.1f%n",
                        rec.getAcademicYear(), rec.getAcademicPeriod(), rec.getSubjectId(), rec.getGradeValue()));

                BigDecimal avg = academicService.calculateStudentPeriodAverage(studentId, year, period);
                System.out.printf(">> Calculated Average (%d - %s): %.2f%n", year, period, avg);
            }
            case 3 -> {
                Attendance a = new Attendance();
                a.setStudentId(InputValidator.readIntInRange("Student ID: ", 1, 99999));
                a.setSubjectId(InputValidator.readIntInRange("Subject ID: ", 1, 99999));
                a.setAttendanceDate(LocalDate.now());
                a.setStatus(InputValidator.readNonEmptyString("Status (Presente, Ausente, Tardanza, Excusa Médica): "));
                a.setObservations(InputValidator.readOptionalString("Observations: "));
                a.setRecordedByTeacherId(InputValidator.readIntInRange("Teacher ID: ", 1, 99999));

                if (academicService.registerAttendance(a)) {
                    System.out.println("[OK] Attendance recorded.");
                } else {
                    System.out.println("[ERROR] Could not save attendance.");
                }
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleExtracurricularModule() {
        System.out.println("\n--- EXTRACURRICULAR ACTIVITIES ---");
        System.out.println("1. List Active Clubs");
        System.out.println("2. Enroll Student in Club");
        System.out.println("3. List Events & Register Participant");
        System.out.println("0. Back to Main Menu");
        int opt = InputValidator.readIntInRange("Choose an option: ", 0, 3);

        switch (opt) {
            case 1 -> extracurricularService.getActiveClubs().forEach(c ->
                    System.out.printf("ID: %-3d | %-25s | Max Cap: %-3d | Schedule: %s%n",
                            c.getClubId(), c.getClubName(), c.getMaxCapacity(), c.getMeetingSchedule()));
            case 2 -> {
                int studentId = InputValidator.readIntInRange("Student ID: ", 1, 99999);
                int clubId = InputValidator.readIntInRange("Club ID: ", 1, 99999);
                String result = extracurricularService.enrollStudentInClub(studentId, clubId);
                if ("SUCCESS".equals(result)) {
                    System.out.println("[OK] Student enrolled in club.");
                } else {
                    System.out.println("[!] " + result);
                }
            }
            case 3 -> {
                extracurricularService.getActiveEvents().forEach(ev ->
                        System.out.printf("ID: %-3d | %-25s | Date: %s | Type: %s%n",
                                ev.getEventId(), ev.getEventName(), ev.getEventDate(), ev.getEventType()));
                int eventId = InputValidator.readIntInRange("Event ID to register student: ", 1, 99999);
                int studentId = InputValidator.readIntInRange("Student ID: ", 1, 99999);
                String role = InputValidator.readNonEmptyString("Role (Participante/Competidor): ");
                String pos = InputValidator.readOptionalString("Position (1er Lugar/2do Lugar/3er Lugar/Participante): ");
                if (extracurricularService.registerEventParticipation(eventId, studentId, role, pos)) {
                    System.out.println("[OK] Participation registered.");
                }
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleFinanceModule() {
        System.out.println("\n--- FINANCIAL MANAGEMENT & DIAN INVOICING ---");
        System.out.println("1. Register Payment & Issue Electronic Invoice (CUFE)");
        System.out.println("2. View Student Payment History");
        System.out.println("0. Back to Main Menu");
        int opt = InputValidator.readIntInRange("Choose an option: ", 0, 2);

        switch (opt) {
            case 1 -> {
                Payment p = new Payment();
                p.setStudentId(InputValidator.readIntInRange("Student ID: ", 1, 99999));
                p.setGuardianId(InputValidator.readIntInRange("Guardian ID: ", 1, 99999));
                String guardianDoc = InputValidator.readNonEmptyString("Guardian Document Number (for DIAN Invoice): ");
                p.setConceptId(InputValidator.readIntInRange("Payment Concept ID (1=Matricula, 2=Pension...): ", 1, 50));
                p.setAmountPaid(InputValidator.readBigDecimalInRange("Amount Paid (COP): ", 1000.0, 50000000.0));
                p.setPaymentMethod(InputValidator.readNonEmptyString("Method (Efectivo, Transferencia, Tarjeta Débito, Tarjeta Crédito): "));
                p.setAcademicYear(2026);
                p.setAcademicPeriod("Anual");
                p.setNotes(InputValidator.readOptionalString("Notes: "));

                Invoice invoice = financeService.processPaymentAndGenerateInvoice(p, guardianDoc);
                if (invoice != null) {
                    System.out.println("[OK] Payment registered and DIAN Electronic Invoice generated!");
                    mainView.printDianElectronicInvoice(invoice, p, guardianDoc);
                } else {
                    System.out.println("[ERROR] Payment processing failed. Check error_log.txt.");
                }
            }
            case 2 -> {
                int studentId = InputValidator.readIntInRange("Student ID: ", 1, 99999);
                financeService.getStudentPaymentHistory(studentId).forEach(pay ->
                        System.out.printf("Date: %s | Receipt: %-14s | Method: %-15s | Amount: $%,.2f%n",
                                pay.getPaymentDate(), pay.getReceiptNumber(), pay.getPaymentMethod(), pay.getAmountPaid()));
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleResourceModule() {
        System.out.println("\n--- RESOURCE MANAGEMENT (LIBRARY & LABS) ---");
        System.out.println("1. List Available Resources");
        System.out.println("2. Borrow Resource (Student or Teacher)");
        System.out.println("3. Return Borrowed Resource");
        System.out.println("0. Back to Main Menu");
        int opt = InputValidator.readIntInRange("Choose an option: ", 0, 3);

        switch (opt) {
            case 1 -> resourceService.getAvailableResources().forEach(r ->
                    System.out.printf("ID: %-3d | Code: %-10s | %-28s | Location: %s%n",
                            r.getResourceId(), r.getResourceCode(), r.getResourceName(), r.getPhysicalLocation()));
            case 2 -> {
                ResourceLoan loan = new ResourceLoan();
                loan.setResourceId(InputValidator.readIntInRange("Resource ID: ", 1, 99999));
                String type = InputValidator.readNonEmptyString("Borrower Type (STUDENT / TEACHER): ").toUpperCase();
                loan.setBorrowerType(type);
                loan.setBorrowerId(InputValidator.readIntInRange(type + " ID: ", 1, 99999));
                loan.setLoanDate(LocalDate.now());
                loan.setExpectedReturnDate(InputValidator.readDate("Expected Return Date"));
                loan.setObservations(InputValidator.readOptionalString("Observations: "));

                if (resourceService.borrowResource(loan)) {
                    System.out.println("[OK] Resource loan registered successfully.");
                } else {
                    System.out.println("[ERROR] Resource unavailable or invalid ID.");
                }
            }
            case 3 -> {
                String type = InputValidator.readNonEmptyString("Borrower Type (STUDENT / TEACHER): ").toUpperCase();
                int loanId = InputValidator.readIntInRange("Loan ID: ", 1, 99999);
                int resourceId = InputValidator.readIntInRange("Resource ID: ", 1, 99999);
                if (resourceService.returnResource(loanId, resourceId, type)) {
                    System.out.println("[OK] Resource returned and marked as Disponible.");
                } else {
                    System.out.println("[ERROR] Could not process return.");
                }
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleReportsModule() {
        System.out.println("\n--- EXECUTIVE REPORTS (JAVA STREAM API) ---");
        System.out.printf("1. Total Revenue Collected: $%,.2f COP%n", financeService.calculateTotalRevenue());

        Map<Integer, Long> highAbsences = academicService.getStudentsWithHighAbsences();
        System.out.println("2. Students with >3 Absences in the Last 7 Days: " + highAbsences.size());
        highAbsences.forEach((studentId, count) ->
                System.out.printf("   -> Student ID: %d | Absences: %d%n", studentId, count));

        List<ResourceLoan> overdue = resourceService.getOverdueLoans();
        System.out.println("3. Overdue Resource Loans: " + overdue.size());
        overdue.forEach(loan ->
                System.out.printf("   -> Loan ID: %d | Type: %s | Borrower ID: %d | Due Date: %s%n",
                        loan.getLoanId(), loan.getBorrowerType(), loan.getBorrowerId(), loan.getExpectedReturnDate()));

        new ClubReportService().generateReport().ifPresentOrElse(f -> System.out.println("4. TXT generado: " + f.toAbsolutePath()), () -> System.out.println("4. Error TXT"));
        InputValidator.pressEnterToContinue();
    }
}