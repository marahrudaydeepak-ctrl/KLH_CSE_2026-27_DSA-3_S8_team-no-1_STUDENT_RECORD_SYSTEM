package ui;

import model.*;
import services.PerformanceService;
import services.StudentService;
import java.util.*;

/**
 * Console submenu for Student Record CRUD operations, filtering, and profile viewing.
 */
public class StudentMenu {

    private final StudentService studentService;
    private final Scanner scanner;

    public StudentMenu(StudentService studentService, Scanner scanner) {
        this.studentService = studentService;
        this.scanner = scanner;
    }

    public void display() {
        while (true) {
            System.out.println("\n====================================================");
            System.out.println("              STUDENT MANAGEMENT MENU               ");
            System.out.println("====================================================");
            System.out.println("1. Add Student");
            System.out.println("2. View Student by ID / Roll Number");
            System.out.println("3. View All Students (Table View)");
            System.out.println("4. Update Student Record");
            System.out.println("5. Delete Student (with Undo Support)");
            System.out.println("6. Search Student");
            System.out.println("7. Filter Students (Dept, Year, CGPA, Attd)");
            System.out.println("8. Sort Students (Go to Sorting Menu)");
            System.out.println("9. Undo Last Operation");
            System.out.println("10. Recently Viewed Students (Stack MRU)");
            System.out.println("11. Student Service Requests (Queue Operations)");
            System.out.println("12. Generate Grade Card / Marksheet (SGPA & Subject Grades)");
            System.out.println("13. Generate & Print Official Certificates (Bonafide, Merit, Degree, etc.)");
            System.out.println("14. Student Fee Ledger & Payment Receipts");
            System.out.println("15. Student Identity Card (ID Card) Generator");
            System.out.println("16. Attendance Shortage Warning Notice");
            System.out.println("17. Class-Wide Grade Tabulation Register");
            System.out.println("18. Show CGPA of Students Separately (Scorecard & Dept Roster)");
            System.out.println("19. Show Attendance of Students Separately (Register & Eligibility Roster)");
            System.out.println("20. Back to Main Menu");
            System.out.print("Enter your choice (1-20): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": handleAddStudent(); break;
                case "2": handleViewStudent(); break;
                case "3": handleViewAll(); break;
                case "4": handleUpdateStudent(); break;
                case "5": handleDeleteStudent(); break;
                case "6": System.out.println("Please select option 2 from Main Menu for Advanced Algorithm Search."); break;
                case "7": handleFilterStudents(); break;
                case "8": System.out.println("Please select option 3 from Main Menu for Advanced Sorting Engine."); break;
                case "9": handleUndo(); break;
                case "10": handleRecentlyViewed(); break;
                case "11": handleServiceQueue(); break;
                case "12": handleGradeCard(); break;
                case "13": handleCertificates(); break;
                case "14": handleFeeLedger(); break;
                case "15": handleIdCard(); break;
                case "16": handleAttendanceNotice(); break;
                case "17": handleTabulationRegister(); break;
                case "18": handleSeparateCgpa(); break;
                case "19": handleSeparateAttendance(); break;
                case "20": return;
                default: System.out.println("Invalid selection. Please enter a valid number (1-20).");
            }
        }
    }

    private void handleAddStudent() {
        System.out.println("\n--- ADD NEW STUDENT ---");
        try {
            System.out.print("Enter Student ID (numeric, e.g. 2520080035): ");
            long id = Long.parseLong(scanner.nextLine().trim());

            if (studentService.getStudentById(id) != null) {
                System.out.println("ERROR: Student with ID " + id + " already exists in the system!");
                return;
            }

            System.out.print("Enter Roll Number (e.g. 2520080035): ");
            String roll = scanner.nextLine().trim();
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine().trim();
            System.out.print("Enter Gender (Male/Female/Other): ");
            String gender = scanner.nextLine().trim();
            System.out.print("Enter Department (CSE/ECE/IT/AIML/MECH/CIVIL/DS): ");
            String dept = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter Course (B.Tech/M.Tech/B.Sc/MCA): ");
            String course = scanner.nextLine().trim();
            System.out.print("Enter Year (1-4): ");
            int yr = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Semester (1-8): ");
            int sem = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Section (A/B/C/D): ");
            String sec = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Phone Number: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter Address: ");
            String addr = scanner.nextLine().trim();
            System.out.print("Enter Attendance Percentage (0-100): ");
            double att = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter Marks Percentage (0-100): ");
            double marks = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter GPA / CGPA (0.0-10.0): ");
            double cgpa = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Skills (comma-separated, e.g. Java, Python, SQL): ");
            String skillsInput = scanner.nextLine().trim();
            List<String> skills = new ArrayList<>();
            for (String s : skillsInput.split(",")) {
                if (!s.trim().isEmpty()) skills.add(s.trim());
            }

            System.out.print("Enter Projects (comma-separated): ");
            String projInput = scanner.nextLine().trim();
            List<String> projs = new ArrayList<>();
            for (String p : projInput.split(",")) {
                if (!p.trim().isEmpty()) projs.add(p.trim());
            }

            System.out.print("Enter Remarks: ");
            String remarks = scanner.nextLine().trim();

            Student s = new Student(id, roll, name, dob, gender, dept, course, yr, sem, sec,
                    email, phone, addr, att, marks, cgpa, cgpa, null, skills, projs,
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), remarks);

            boolean success = studentService.addStudent(s);
            if (success) {
                System.out.println("SUCCESS: Student " + name + " (ID: " + id + ") registered successfully!");
                System.out.println("Synchronized to: Singly/Doubly/Circular Linked Lists, BST, AVL Tree, CustomHashTable, Trie, and Academic Graph.");
            } else {
                System.out.println("FAILED: Could not register student.");
            }
        } catch (Exception e) {
            System.out.println("Input validation error: " + e.getMessage());
        }
    }

    private void handleViewStudent() {
        System.out.print("\nEnter Student ID or Roll Number to inspect: ");
        String term = scanner.nextLine().trim();
        Student s = null;
        try {
            long id = Long.parseLong(term);
            s = studentService.getStudentById(id);
        } catch (NumberFormatException e) {
            s = studentService.getStudentByRoll(term);
        }

        if (s != null) {
            System.out.println("\n" + s.toDetailString());
        } else {
            System.out.println("No student record found matching identifier: " + term);
        }
    }

    private void handleViewAll() {
        List<Student> list = studentService.getAllStudents();
        System.out.println("\n==========================================================================================================");
        System.out.printf(" TOTAL REGISTERED STUDENTS: %d\n", list.size());
        System.out.println("==========================================================================================================");
        System.out.printf("%-11s | %-12s | %-20s | %-4s | %-6s | %-4s | %-6s | %-6s | %-5s\n",
                "Student ID", "Roll No", "Full Name", "Dept", "Course", "Year", "Marks%", "Attd%", "CGPA");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        for (Student s : list) {
            System.out.printf("%-11d | %-12s | %-20s | %-4s | %-6s | %-4d | %5.1f%% | %5.1f%% | %4.2f\n",
                    s.getStudentId(), s.getRollNumber(), s.getName(), s.getDepartment(),
                    s.getCourse(), s.getYear(), s.getMarks(), s.getAttendance(), s.getCgpa());
        }
        System.out.println("==========================================================================================================");
    }

    private void handleUpdateStudent() {
        System.out.print("\nEnter Student ID to update: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            Student existing = studentService.getStudentById(id);
            if (existing == null) {
                System.out.println("Student ID not found: " + id);
                return;
            }

            Student updated = new Student(existing);
            System.out.printf("Current Name [%s]: Enter new or press Enter to keep: ", existing.getName());
            String n = scanner.nextLine().trim();
            if (!n.isEmpty()) updated.setName(n);

            System.out.printf("Current Attendance [%.1f%%]: Enter new or press Enter: ", existing.getAttendance());
            String att = scanner.nextLine().trim();
            if (!att.isEmpty()) updated.setAttendance(Double.parseDouble(att));

            System.out.printf("Current Marks [%.1f%%]: Enter new or press Enter: ", existing.getMarks());
            String m = scanner.nextLine().trim();
            if (!m.isEmpty()) updated.setMarks(Double.parseDouble(m));

            System.out.printf("Current CGPA [%.2f]: Enter new or press Enter: ", existing.getCgpa());
            String c = scanner.nextLine().trim();
            if (!c.isEmpty()) updated.setCgpa(Double.parseDouble(c));

            boolean success = studentService.updateStudent(updated);
            if (success) {
                System.out.println("SUCCESS: Updated student record. Previous state backed up on Undo Stack.");
            }
        } catch (Exception e) {
            System.out.println("Update error: " + e.getMessage());
        }
    }

    private void handleDeleteStudent() {
        System.out.print("\nEnter Student ID to delete: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            Student existing = studentService.getStudentById(id);
            if (existing == null) {
                System.out.println("Student not found.");
                return;
            }

            System.out.printf("Confirm deletion of student '%s' (ID %d)? (yes/no): ", existing.getName(), id);
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
                boolean del = studentService.deleteStudent(id);
                if (del) {
                    System.out.println("SUCCESS: Student deleted. State pushed to CustomStack (can be restored using UNDO).");
                }
            } else {
                System.out.println("Deletion cancelled by user.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleFilterStudents() {
        System.out.println("\n--- FILTER STUDENT RECORDS ---");
        System.out.print("Department (CSE/ECE/IT/AIML/MECH/CIVIL/DS or ALL): ");
        String dept = scanner.nextLine().trim();
        System.out.print("Minimum CGPA (or 0 for no threshold): ");
        double minCgpa = 0;
        try { minCgpa = Double.parseDouble(scanner.nextLine().trim()); } catch (Exception ignored) {}
        System.out.print("Minimum Attendance % (or 0 for no threshold): ");
        double minAtt = 0;
        try { minAtt = Double.parseDouble(scanner.nextLine().trim()); } catch (Exception ignored) {}

        List<Student> filtered = studentService.filterStudents(dept, null, null, minCgpa, minAtt, null);
        System.out.printf("\nFound %d matching student records:\n", filtered.size());
        for (Student s : filtered) {
            System.out.println(s);
        }
    }

    private void handleUndo() {
        System.out.println("\nExecuting UNDO from CustomStack...");
        boolean success = studentService.undoLastOperation();
        if (success) {
            System.out.println("SUCCESS: Last modification reverted successfully via Undo Stack!");
        } else {
            System.out.println("Undo Stack is currently empty. No operations to undo.");
        }
    }

    private void handleRecentlyViewed() {
        System.out.println("\n--- RECENTLY VIEWED STUDENTS (CustomStack MRU) ---");
        List<Student> recent = studentService.getRecentlyViewedStack().toList();
        if (recent.isEmpty()) {
            System.out.println("No recently viewed students yet.");
            return;
        }
        for (int i = 0; i < recent.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, recent.get(i));
        }
    }

    private void handleServiceQueue() {
        System.out.println("\n--- STUDENT SERVICE REQUEST QUEUE (FIFO CustomQueue) ---");
        System.out.println("1. Submit Service Request");
        System.out.println("2. Process Next Service Request (Dequeue)");
        System.out.println("3. Inspect Queue Status (Front, Rear, Size)");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            System.out.print("Enter Student ID: ");
            try {
                long id = Long.parseLong(scanner.nextLine().trim());
                System.out.print("Request Type (Transcript / Bonafide / ID Card / Recommendation): ");
                String type = scanner.nextLine().trim();
                studentService.submitServiceRequest(id, type);
                System.out.println("Service request enqueued successfully!");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else if (ch.equals("2")) {
            StudentService.ServiceRequest req = studentService.processNextServiceRequest();
            if (req != null) {
                System.out.println("PROCESSED REQUEST: " + req);
            } else {
                System.out.println("Queue is empty. No pending requests.");
            }
        } else if (ch.equals("3")) {
            System.out.println(studentService.getServiceQueue().getStatusString());
            List<StudentService.ServiceRequest> list = studentService.getServiceQueue().toList();
            for (StudentService.ServiceRequest r : list) {
                System.out.println("  " + r);
            }
        }
    }

    private Student promptStudent() {
        System.out.print("\nEnter Student ID or Roll Number: ");
        String term = scanner.nextLine().trim();
        Student s = null;
        try {
            long id = Long.parseLong(term);
            s = studentService.getStudentById(id);
        } catch (NumberFormatException e) {
            s = studentService.getStudentByRoll(term);
        }
        if (s == null) {
            System.out.println("Student record not found for query: " + term);
        }
        return s;
    }

    public void handleGradeCard() {
        System.out.println("\n--- ACADEMIC GRADE CARD & MARKSHEET GENERATOR ---");
        Student s = promptStudent();
        if (s == null) return;

        GradeCard gc = studentService.getRecordService().generateGradeCard(s);
        System.out.println("\n" + gc.toFormattedMarksheet());

        System.out.print("Do you want to save this marksheet to file? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            String path = "data/grade_cards/marksheet_" + s.getRollNumber() + ".txt";
            boolean ok = studentService.getRecordService().saveDocumentToFile(gc.toFormattedMarksheet(), path);
            System.out.println(ok ? "Saved successfully to " + path : "Failed to save file.");
        }
    }

    public void handleCertificates() {
        System.out.println("\n--- OFFICIAL INSTITUTIONAL CERTIFICATE GENERATION & PRINTING ---");
        Student s = promptStudent();
        if (s == null) return;

        System.out.println("\nAvailable Certificate Types:");
        CertificateType[] types = CertificateType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("%d. %-36s (%s)\n", i + 1, types[i].getDisplayName(), types[i].getDescription());
        }
        System.out.print("Choose Certificate (1-" + types.length + "): ");
        try {
            int ch = Integer.parseInt(scanner.nextLine().trim());
            if (ch < 1 || ch > types.length) {
                System.out.println("Invalid selection.");
                return;
            }
            CertificateType selectedType = types[ch - 1];

            System.out.print("Enter Certificate Purpose / Reason (or press Enter for default): ");
            String purpose = scanner.nextLine().trim();

            Certificate cert = studentService.getRecordService().generateCertificate(s, selectedType, purpose);
            System.out.println("\n" + cert.getFormattedDocument());

            System.out.println("Options:");
            System.out.println("1. Save Certificate to Disk (data/certificates/)");
            System.out.println("2. Enqueue in Student Service Request Queue (FIFO)");
            System.out.println("3. Done / Return");
            System.out.print("Choice: ");
            String op = scanner.nextLine().trim();

            if (op.equals("1")) {
                String path = "data/certificates/cert_" + s.getRollNumber() + "_" + selectedType.name().toLowerCase() + ".txt";
                boolean ok = studentService.getRecordService().saveDocumentToFile(cert.getFormattedDocument(), path);
                System.out.println(ok ? "Certificate successfully saved to " + path : "Failed to save.");
            } else if (op.equals("2")) {
                studentService.submitServiceRequest(s.getStudentId(), selectedType.getDisplayName());
                System.out.println("Submitted to FIFO Service Queue! Queue size: " + studentService.getServiceQueue().getSize());
            }
        } catch (Exception e) {
            System.out.println("Error generating certificate: " + e.getMessage());
        }
    }

    public void handleFeeLedger() {
        System.out.println("\n--- STUDENT ACADEMIC FEE & DUES LEDGER ---");
        Student s = promptStudent();
        if (s == null) return;

        FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
        System.out.println("\n" + fr);
        System.out.println("\n1. View & Print Official Fee Payment Receipt");
        System.out.println("2. Record Fee Payment (Cash / Online / DD)");
        System.out.println("3. Generate No-Dues Financial Clearance Certificate");
        System.out.println("4. Return");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            String receipt = fr.toFormattedReceipt();
            System.out.println("\n" + receipt);
            System.out.print("Save Receipt to file? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                String path = "data/fee_receipts/receipt_" + s.getRollNumber() + ".txt";
                boolean ok = studentService.getRecordService().saveDocumentToFile(receipt, path);
                System.out.println(ok ? "Saved receipt to " + path : "Failed to save.");
            }
        } else if (ch.equals("2")) {
            System.out.printf("Current Balance Due: INR %,.2f\n", fr.getBalanceDue());
            System.out.print("Enter Amount to Pay: ");
            try {
                double amt = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Payment Method (UPI / NetBanking / Demand Draft / Cash): ");
                String method = scanner.nextLine().trim();
                fr.recordPayment(amt, method);
                System.out.println("Payment recorded successfully! New Balance: INR " + String.format("%,.2f", fr.getBalanceDue()));
            } catch (Exception e) {
                System.out.println("Invalid payment input: " + e.getMessage());
            }
        } else if (ch.equals("3")) {
            String nd = fr.toFormattedNoDuesCertificate();
            System.out.println("\n" + nd);
            System.out.print("Save No-Dues Certificate? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                String path = "data/certificates/nodues_" + s.getRollNumber() + ".txt";
                boolean ok = studentService.getRecordService().saveDocumentToFile(nd, path);
                System.out.println(ok ? "Saved to " + path : "Failed to save.");
            }
        }
    }

    public void handleIdCard() {
        System.out.println("\n--- OFFICIAL STUDENT IDENTITY CARD GENERATOR ---");
        Student s = promptStudent();
        if (s == null) return;

        StudentIdCard idCard = studentService.getRecordService().generateStudentIdCard(s);
        System.out.println("\n" + idCard.getFormattedCard());

        System.out.print("Save ID Card to file? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            String path = "data/id_cards/idcard_" + s.getRollNumber() + ".txt";
            boolean ok = studentService.getRecordService().saveDocumentToFile(idCard.getFormattedCard(), path);
            System.out.println(ok ? "ID Card saved to " + path : "Failed to save.");
        }
    }

    public void handleAttendanceNotice() {
        System.out.println("\n--- ATTENDANCE SHORTAGE WARNING MEMO ---");
        Student s = promptStudent();
        if (s == null) return;

        String memo = studentService.getRecordService().generateAttendanceShortageNotice(s);
        System.out.println("\n" + memo);
        System.out.print("Save Warning Memo to file? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            String path = "data/attendance_notices/warning_" + s.getRollNumber() + ".txt";
            boolean ok = studentService.getRecordService().saveDocumentToFile(memo, path);
            System.out.println(ok ? "Saved to " + path : "Failed to save.");
        }
    }

    public void handleTabulationRegister() {
        System.out.println("\n--- CLASS-WIDE GRADE TABULATION REGISTER ---");
        System.out.print("Enter Department (CSE / ECE / IT / AIML / MECH / CIVIL / DS or ALL): ");
        String dept = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter Semester (1-8 or 0 for All): ");
        int sem = 0;
        try {
            sem = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception ignored) {}

        String register = studentService.getRecordService().generateTabulationRegister(dept, sem);
        System.out.println("\n" + register);
        System.out.print("Export Tabulation Register to file? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            String path = "data/tabulation_register_" + dept.toLowerCase() + ".txt";
            boolean ok = studentService.getRecordService().saveDocumentToFile(register, path);
            System.out.println(ok ? "Tabulation register exported to " + path : "Failed to export.");
        }
    }

    public void handleSeparateCgpa() {
        System.out.println("\n--- SEPARATE STUDENT CGPA SCORECARD & ROSTER ---");
        System.out.println("1. View Individual Student CGPA Scorecard");
        System.out.println("2. View Department-Wide CGPA Performance Ledger");
        System.out.println("3. View College-Wide CGPA Honor Roll (CGPA >= 8.5)");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            Student s = promptStudent();
            if (s == null) return;
            CgpaRecord rec = studentService.getRecordService().generateCgpaRecord(s);
            System.out.println("\n" + rec.toFormattedCgpaCard());
            System.out.print("Save CGPA Scorecard to file? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                String path = "data/cgpa_cards/cgpa_" + s.getRollNumber() + ".txt";
                boolean ok = studentService.getRecordService().saveDocumentToFile(rec.toFormattedCgpaCard(), path);
                System.out.println(ok ? "Saved successfully to " + path : "Failed to save.");
            }
        } else if (ch.equals("2")) {
            System.out.print("Enter Department (CSE / ECE / IT / AIML / MECH / CIVIL / DS or ALL): ");
            String dept = scanner.nextLine().trim().toUpperCase();
            String report = studentService.getRecordService().generateSeparateCgpaReport(dept, null);
            System.out.println("\n" + report);
            System.out.print("Save CGPA Ledger to file? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                String path = "data/cgpa_ledger_" + dept.toLowerCase() + ".txt";
                boolean ok = studentService.getRecordService().saveDocumentToFile(report, path);
                System.out.println(ok ? "Saved to " + path : "Failed to save.");
            }
        } else if (ch.equals("3")) {
            String report = studentService.getRecordService().generateSeparateCgpaReport("ALL", 8.5);
            System.out.println("\n" + report);
        }
    }

    public void handleSeparateAttendance() {
        System.out.println("\n--- SEPARATE STUDENT ATTENDANCE REPORT & REGISTER ---");
        System.out.println("1. View Individual Student Attendance Card & Exam Eligibility");
        System.out.println("2. View Department-Wide Attendance Register");
        System.out.println("3. View Attendance Defaulter / Shortage List (< 75%)");
        System.out.println("4. View Critical Detention List (< 65%)");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            Student s = promptStudent();
            if (s == null) return;
            AttendanceRecord rec = studentService.getRecordService().generateAttendanceRecord(s);
            System.out.println("\n" + rec.toFormattedAttendanceCard());
            System.out.print("Save Attendance Card to file? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                String path = "data/attendance_cards/attd_" + s.getRollNumber() + ".txt";
                boolean ok = studentService.getRecordService().saveDocumentToFile(rec.toFormattedAttendanceCard(), path);
                System.out.println(ok ? "Saved successfully to " + path : "Failed to save.");
            }
        } else if (ch.equals("2")) {
            System.out.print("Enter Department (CSE / ECE / IT / AIML / MECH / CIVIL / DS or ALL): ");
            String dept = scanner.nextLine().trim().toUpperCase();
            String report = studentService.getRecordService().generateSeparateAttendanceReport(dept, "ALL");
            System.out.println("\n" + report);
            System.out.print("Save Attendance Register to file? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                String path = "data/attendance_register_" + dept.toLowerCase() + ".txt";
                boolean ok = studentService.getRecordService().saveDocumentToFile(report, path);
                System.out.println(ok ? "Saved to " + path : "Failed to save.");
            }
        } else if (ch.equals("3")) {
            String report = studentService.getRecordService().generateSeparateAttendanceReport("ALL", "SHORTAGE");
            System.out.println("\n" + report);
        } else if (ch.equals("4")) {
            String report = studentService.getRecordService().generateSeparateAttendanceReport("ALL", "DETAINED");
            System.out.println("\n" + report);
        }
    }
}
