package ui;

import algorithms.sorting.SortCriteria;
import file.StudentFileManager;
import model.*;
import services.*;
import java.util.*;

/**
 * Main Interactive Console Controller implementing the exact 27-item Master Menu.
 */
public class MainMenu {

    private final StudentService studentService;
    private final Scanner scanner;
    private final StudentMenu studentMenu;
    private final SearchMenu searchMenu;
    private final SortingMenu sortingMenu;
    private final AlgorithmMenu algorithmMenu;

    public MainMenu(StudentService studentService) {
        this.studentService = studentService;
        this.scanner = new Scanner(System.in);
        this.studentMenu = new StudentMenu(studentService, scanner);
        this.searchMenu = new SearchMenu(studentService, scanner);
        this.sortingMenu = new SortingMenu(studentService, scanner);
        this.algorithmMenu = new AlgorithmMenu(studentService, scanner);
    }

    public void start() {
        while (true) {
            printHeader();
            System.out.println("0. 📊 Executive Analytics Dashboard & KPI Visualizer");
            System.out.println("1. Student Management");
            System.out.println("2. Search Student (With Intelligent Multi-Algorithm Engine)");
            System.out.println("3. Sort Students");
            System.out.println("4. Attendance Management");
            System.out.println("5. Marks & Performance");
            System.out.println("6. Student Ranking");
            System.out.println("7. String Search Algorithms");
            System.out.println("8. Fuzzy Search");
            System.out.println("9. Student Similarity & Duplicate Detection");
            System.out.println("10. Linked List Operations");
            System.out.println("11. Hashing");
            System.out.println("12. BST Operations");
            System.out.println("13. AVL Tree Operations");
            System.out.println("14. Trie Operations");
            System.out.println("15. Graph Operations");
            System.out.println("16. Dynamic Programming");
            System.out.println("17. Network Flow");
            System.out.println("18. NP-Completeness");
            System.out.println("19. Approximation Algorithms");
            System.out.println("20. Parameterized Algorithms");
            System.out.println("21. Randomized Algorithms");
            System.out.println("22. Parallel Algorithms");
            System.out.println("23. Algorithm Comparison");
            System.out.println("24. Execution History");
            System.out.println("25. File Management");
            System.out.println("26. Reports");
            System.out.println("27. Exit");
            System.out.println("28. 🎓 Student Records, Grade Cards & Certificate Printing Center");
            System.out.print("\nEnter your choice (0-28): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "0": handleDashboard(); break;
                case "1": studentMenu.display(); break;
                case "2": searchMenu.display(); break;
                case "3": sortingMenu.display(); break;
                case "4": handleAttendance(); break;
                case "5": handleMarksPerformance(); break;
                case "6": handleStudentRanking(); break;
                case "7": handleStringSearch(); break;
                case "8": searchMenu.display(); break;
                case "9": handleSimilarityDuplicates(); break;
                case "10": algorithmMenu.handleLinkedLists(); break;
                case "11": algorithmMenu.handleHashing(); break;
                case "12": algorithmMenu.handleBST(); break;
                case "13": algorithmMenu.handleAVL(); break;
                case "14": searchMenu.display(); break;
                case "15": algorithmMenu.handleGraph(); break;
                case "16": algorithmMenu.handleDynamicProgramming(); break;
                case "17": algorithmMenu.handleNetworkFlow(); break;
                case "18": algorithmMenu.handleNPCompleteness(); break;
                case "19": algorithmMenu.handleApproximation(); break;
                case "20": algorithmMenu.handleParameterized(); break;
                case "21": algorithmMenu.handleRandomized(); break;
                case "22": algorithmMenu.handleParallel(); break;
                case "23": handleAlgorithmComparison(); break;
                case "24": handleExecutionHistory(); break;
                case "25": handleFileManagement(); break;
                case "26": handleReports(); break;
                case "27":
                    System.out.println("\nThank you for using INTELLIGENT STUDENT RECORD MANAGEMENT SYSTEM. Goodbye!");
                    return;
                case "28": handleRecordsAndCertificates(); break;
                default:
                    System.out.println("Invalid selection. Please choose an option between 0 and 28.");
            }
        }
    }

    private void printHeader() {
        System.out.println("\n====================================================");
        System.out.println("       INTELLIGENT STUDENT RECORD SYSTEM            ");
        System.out.println("   (Advanced DSA & Automated Algorithmic Engine)    ");
        System.out.println("====================================================");
        System.out.printf(" Records Active: %d | Undo Stack: %d | Service Queue: %d\n",
                studentService.getTotalCount(), studentService.getUndoStack().size(),
                studentService.getServiceQueue().getSize());
        System.out.println("----------------------------------------------------");
    }

    private void handleAttendance() {
        System.out.println("\n--- ATTENDANCE MANAGEMENT ---");
        PerformanceService.PerformanceSummary sum = PerformanceService.computeSummary(studentService.getAllStudents());
        System.out.printf("College-Wide Average Attendance: %.2f%%\n", sum.avgAttendance);
        System.out.printf("Students Below Mandatory 75%% Threshold (%d students):\n", sum.studentsBelowAttendance.size());
        for (Student s : sum.studentsBelowAttendance) {
            System.out.printf("  [ID: %d] %-20s (%s) - Attendance: %.1f%% [WARNING]\n",
                    s.getStudentId(), s.getName(), s.getDepartment(), s.getAttendance());
        }

        System.out.println("\nOptions: 1. View Full Separate Attendance Register & Exam Audit | 2. Return to Menu");
        System.out.print("Choice: ");
        String aCh = scanner.nextLine().trim();
        if (aCh.equals("1")) {
            studentMenu.handleSeparateAttendance();
        }
    }

    private void handleMarksPerformance() {
        System.out.println("\n--- MARKS & ACADEMIC PERFORMANCE ANALYSIS ---");
        PerformanceService.PerformanceSummary sum = PerformanceService.computeSummary(studentService.getAllStudents());
        System.out.printf("Total Students Analyzed: %d\n", sum.totalStudents);
        System.out.printf("Average Marks: %.2f%% | Average CGPA: %.2f\n", sum.avgMarks, sum.avgCgpa);
        System.out.printf("Highest Marks: %.2f%% (Top Student: %s)\n", sum.maxMarks, sum.topStudent != null ? sum.topStudent.getName() : "N/A");
        System.out.printf("Lowest Marks: %.2f%%\n", sum.minMarks);
        System.out.printf("Top Performers (CGPA >= 9.0): %d students\n", sum.topPerformers.size());
        System.out.printf("At-Risk Students (CGPA < 5.0 or Marks < 40%%): %d students\n", sum.studentsAtAcademicRisk.size());

        for (Student s : sum.studentsAtAcademicRisk) {
            System.out.printf("  [AT-RISK] ID %d - %s (%s, CGPA: %.2f, Marks: %.1f%%)\n",
                    s.getStudentId(), s.getName(), s.getDepartment(), s.getCgpa(), s.getMarks());
        }

        System.out.println("\nOptions: 1. View Full Separate CGPA Ledger & Scorecards | 2. Return to Menu");
        System.out.print("Choice: ");
        String mCh = scanner.nextLine().trim();
        if (mCh.equals("1")) {
            studentMenu.handleSeparateCgpa();
        }
    }

    private void handleStudentRanking() {
        System.out.println("\n--- STUDENT RANKING ENGINE (Custom Sorting) ---");
        System.out.println("1. Rank by CGPA (Merge Sort)");
        System.out.println("2. Rank by Total Marks (Merge Sort)");
        System.out.println("3. Rank by Attendance (Merge Sort)");
        System.out.println("4. Rank by Holistic Combined Score [CGPA*5 + Marks*0.3 + Attd*0.2]");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        List<PerformanceService.RankedStudent> ranked;
        switch (ch) {
            case "2": ranked = PerformanceService.rankStudents(studentService.getAllStudents(), SortCriteria.MARKS); break;
            case "3": ranked = PerformanceService.rankStudents(studentService.getAllStudents(), SortCriteria.ATTENDANCE); break;
            case "4": ranked = PerformanceService.rankByCombinedScore(studentService.getAllStudents()); break;
            case "1":
            default: ranked = PerformanceService.rankStudents(studentService.getAllStudents(), SortCriteria.CGPA); break;
        }

        System.out.println("\n==========================================================================================================");
        System.out.println("                                      STUDENT MERIT RANK LIST                                             ");
        System.out.println("==========================================================================================================");
        for (int i = 0; i < Math.min(15, ranked.size()); i++) {
            System.out.println(ranked.get(i));
        }
        System.out.println("==========================================================================================================");
    }

    private void handleStringSearch() {
        System.out.println("\n--- STRING SEARCH ALGORITHMS COMPARISON ---");
        System.out.print("Enter pattern to test across all student names, skills, and projects: ");
        String pat = scanner.nextLine().trim();
        String report = studentService.getExecutionService().compareStringAlgorithms(studentService.getAllStudents(), pat);
        System.out.println("\n" + report);
    }

    private void handleSimilarityDuplicates() {
        System.out.println("\n--- STUDENT SIMILARITY & DUPLICATE DETECTION ---");
        System.out.println("1. Scan Entire Database for Duplicate Student Records");
        System.out.println("2. Compare Profile Similarity Between Two Students (LCS + Jaccard + Edit Distance)");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            List<SimilarityService.DuplicateReport> duplicates = SimilarityService.findDuplicates(studentService.getAllStudents());
            if (duplicates.isEmpty()) {
                System.out.println("No duplicates detected in the database. All records unique!");
            } else {
                System.out.printf("Detected %d potential duplicate records:\n", duplicates.size());
                for (SimilarityService.DuplicateReport d : duplicates) {
                    System.out.println(d);
                }
            }
        } else if (ch.equals("2")) {
            System.out.print("Enter Student ID 1 (e.g. 2520080001): ");
            long id1 = Long.parseLong(scanner.nextLine().trim());
            System.out.print("Enter Student ID 2 (e.g. 2520080002): ");
            long id2 = Long.parseLong(scanner.nextLine().trim());

            Student s1 = studentService.getStudentById(id1);
            Student s2 = studentService.getStudentById(id2);

            if (s1 != null && s2 != null) {
                AlgorithmResult res = SimilarityService.compareProfiles(s1, s2);
                studentService.getExecutionService().recordExecution(res);
                System.out.println("\n" + res.toFormattedDisplay());
            } else {
                System.out.println("One or both student IDs not found.");
            }
        }
    }

    private void handleAlgorithmComparison() {
        System.out.println("\n====================================================");
        System.out.println("              ALGORITHM COMPARISON ENGINE           ");
        System.out.println("====================================================");
        System.out.println("1. Compare Search Algorithms (Linear vs Binary Search)");
        System.out.println("2. Compare Sorting Algorithms (Bubble vs Selection vs Insertion vs Merge vs Quick vs Heap)");
        System.out.println("3. Compare String Algorithms (Naive vs KMP vs Z vs Rabin-Karp vs Boyer-Moore)");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            System.out.print("Enter target Student ID to compare: ");
            long id = 2520080001L;
            try { id = Long.parseLong(scanner.nextLine().trim()); } catch (Exception ignored) {}
            String res = studentService.getExecutionService().compareSearchAlgorithms(studentService.getAllStudents(), id);
            System.out.println("\n" + res);
        } else if (ch.equals("2")) {
            String res = studentService.getExecutionService().compareSortingAlgorithms(studentService.getAllStudents(), SortCriteria.CGPA);
            System.out.println("\n" + res);
        } else if (ch.equals("3")) {
            System.out.print("Enter search pattern: ");
            String pat = scanner.nextLine().trim();
            String res = studentService.getExecutionService().compareStringAlgorithms(studentService.getAllStudents(), pat);
            System.out.println("\n" + res);
        }
    }

    private void handleExecutionHistory() {
        System.out.println("\n====================================================");
        System.out.println("        ALGORITHM EXECUTION HISTORY (CustomStack)   ");
        System.out.println("====================================================");
        List<AlgorithmExecutionService.HistoryEntry> list = studentService.getExecutionService().getHistory();
        if (list.isEmpty()) {
            System.out.println("No algorithms recorded yet.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    private void handleFileManagement() {
        System.out.println("\n--- FILE & DATA MANAGEMENT ---");
        System.out.println("1. Save Students to CSV (data/students.csv)");
        System.out.println("2. Load Students from CSV");
        System.out.println("3. Backup Database (Binary Object Serialization)");
        System.out.println("4. Restore Database from Backup");
        System.out.println("5. Generate Benchmark Dataset (100, 500, 1000, 5000 students)");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            boolean ok = StudentFileManager.saveToCSV(studentService.getAllStudents(), null);
            System.out.println(ok ? "Saved successfully to data/students.csv!" : "Failed to save.");
        } else if (ch.equals("2")) {
            List<Student> loaded = StudentFileManager.loadFromCSV(null);
            System.out.printf("Loaded %d records from CSV.\n", loaded.size());
        } else if (ch.equals("3")) {
            boolean ok = StudentFileManager.backupRecords(studentService.getAllStudents(), null);
            System.out.println(ok ? "Backup created successfully at data/students_backup.dat!" : "Backup failed.");
        } else if (ch.equals("4")) {
            List<Student> restored = StudentFileManager.restoreRecords(null);
            System.out.printf("Restored %d records from backup.\n", restored.size());
        } else if (ch.equals("5")) {
            System.out.print("Enter count to generate (100, 500, 1000, 5000): ");
            try {
                int count = Integer.parseInt(scanner.nextLine().trim());
                List<Student> bench = SampleDataGenerator.generateBenchmarkDataset(count, System.currentTimeMillis());
                for (Student s : bench) {
                    studentService.addStudent(s);
                }
                System.out.printf("Successfully generated and registered %,d synthetic students! Current Total: %,d\n",
                        count, studentService.getTotalCount());
            } catch (Exception e) {
                System.out.println("Error generating dataset: " + e.getMessage());
            }
        }
    }

    private void handleReports() {
        System.out.println("\n--- ACADEMIC & ALGORITHMIC REPORTS ---");
        List<Student> students = studentService.getAllStudents();
        PerformanceService.PerformanceSummary sum = PerformanceService.computeSummary(students);

        int honorsCount = 0, eligibleCount = 0, condonationCount = 0, detainedCount = 0, feeClearedCount = 0;
        double totalFeesAssessed = 0.0, totalFeesCollected = 0.0, totalBalanceDue = 0.0;
        for (Student s : students) {
            if (s.getCgpa() >= 8.5) honorsCount++;
            if (s.getAttendance() >= 75.0) eligibleCount++;
            else if (s.getAttendance() >= 65.0) condonationCount++;
            else detainedCount++;

            FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
            if (fr != null) {
                totalFeesAssessed += fr.getTotalFee();
                totalFeesCollected += fr.getAmountPaid();
                totalBalanceDue += fr.getBalanceDue();
                if (fr.isCleared()) feeClearedCount++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          \n");
        sb.append("                         INTELLIGENT STUDENT RECORD SYSTEM - EXECUTIVE REPORT                       \n");
        sb.append("====================================================================================================\n");
        sb.append(String.format(" Total Enrolled Students      : %-4d            | College Top Valedictorian   : %s\n",
                sum.totalStudents, sum.topStudent != null ? sum.topStudent.getName() : "N/A"));
        sb.append(String.format(" College Average CGPA         : %-5.2f           | Dean's Honors List (>=8.5)  : %d students\n",
                sum.avgCgpa, honorsCount));
        sb.append(String.format(" College Average Attendance   : %-5.1f%%          | Statutory Exam Eligible     : %d of %d (%.1f%%)\n",
                sum.avgAttendance, eligibleCount, sum.totalStudents, (eligibleCount * 100.0 / Math.max(1, sum.totalStudents))));
        sb.append(String.format(" Attendance Shortage (<75%%)   : %-4d            | Detained (<65%% Attendance)  : %d students\n",
                (condonationCount + detainedCount), detainedCount));
        sb.append(String.format(" Students at Academic Risk    : %-4d            | Total Algorithm Executions  : %d\n",
                sum.studentsAtAcademicRisk.size(), studentService.getExecutionService().getHistory().size()));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(" FINANCIAL LEDGER & ACCOUNTS CLEARANCE SUMMARY:\n");
        sb.append(String.format(" Total Fees Assessed          : INR %,.2f\n", totalFeesAssessed));
        sb.append(String.format(" Total Fees Collected         : INR %,.2f (%.1f%%)\n", totalFeesCollected, (totalFeesCollected * 100.0 / Math.max(1, totalFeesAssessed))));
        sb.append(String.format(" Total Outstanding Dues       : INR %,.2f\n", totalBalanceDue));
        sb.append(String.format(" No-Dues Clearance Issued     : %d of %d students\n", feeClearedCount, sum.totalStudents));
        sb.append("====================================================================================================\n");

        System.out.println(sb.toString());
        StudentFileManager.exportReport(sb.toString(), "data/executive_report.txt");
        System.out.println("Report also exported to data/executive_report.txt");
    }

    private void handleDashboard() {
        List<Student> students = studentService.getAllStudents();
        PerformanceService.PerformanceSummary sum = PerformanceService.computeSummary(students);

        int honorsCount = 0, eligibleCount = 0, condonationCount = 0, detainedCount = 0, feeClearedCount = 0;
        double totalFeesAssessed = 0.0, totalFeesCollected = 0.0, totalBalanceDue = 0.0;
        for (Student s : students) {
            if (s.getCgpa() >= 8.5) honorsCount++;
            if (s.getAttendance() >= 75.0) eligibleCount++;
            else if (s.getAttendance() >= 65.0) condonationCount++;
            else detainedCount++;

            FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
            if (fr != null) {
                totalFeesAssessed += fr.getTotalFee();
                totalFeesCollected += fr.getAmountPaid();
                totalBalanceDue += fr.getBalanceDue();
                if (fr.isCleared()) feeClearedCount++;
            }
        }

        System.out.println("\n+-----------------------------------------------------------------------------------------------+");
        System.out.println("|                   EXECUTIVE ANALYTICS DASHBOARD & KPI CONTROL CENTER                          |");
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        System.out.printf("|  [ENROLLED STUDENTS]  : %-4d               |  [AVERAGE CGPA]       : %-5.2f                  |\n", sum.totalStudents, sum.avgCgpa);
        System.out.printf("|  [DEAN'S HONORS >=8.5]: %-4d students       |  [TOP VALEDICTORIAN]  : %-18s |\n",
                honorsCount, sum.topStudent != null ? sum.topStudent.getName() : "N/A");
        System.out.printf("|  [AVERAGE ATTENDANCE] : %-5.1f%%             |  [EXAM ELIGIBLE >=75%%]: %-4d (%5.1f%%)         |\n",
                sum.avgAttendance, eligibleCount, (eligibleCount * 100.0 / Math.max(1, sum.totalStudents)));
        System.out.printf("|  [AT-RISK / SHORTAGE] : %-4d students       |  [DETAINED (<65%%)]    : %-4d students          |\n",
                sum.studentsAtAcademicRisk.size(), detainedCount);
        System.out.printf("|  [FEES COLLECTED]     : INR %,.0f     |  [FEE NO-DUES ISSUED] : %-4d of %-4d             |\n",
                totalFeesCollected, feeClearedCount, sum.totalStudents);
        System.out.printf("|  [SERVICE QUEUE SIZE] : %-4d requests        |  [ALGO RUNS IN STACK] : %-4d                   |\n",
                studentService.getServiceQueue().getSize(), studentService.getExecutionService().getHistory().size());
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        System.out.println("|                            1. ACADEMIC CGPA DISTRIBUTION HISTOGRAM                            |");
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        int cRisk = 0, cAvg = 0, cGood = 0, cExc = 0, cTop = 0;
        for (Student s : students) {
            double g = s.getCgpa();
            if (g < 6.0) cRisk++;
            else if (g < 7.0) cAvg++;
            else if (g < 8.0) cGood++;
            else if (g < 9.0) cExc++;
            else cTop++;
        }
        printHistogramBar("9.0 - 10.0 (Top)", cTop, sum.totalStudents);
        printHistogramBar("8.0 - 8.99 (Exc)", cExc, sum.totalStudents);
        printHistogramBar("7.0 - 7.99 (Good)", cGood, sum.totalStudents);
        printHistogramBar("6.0 - 6.99 (Pass)", cAvg, sum.totalStudents);
        printHistogramBar("< 6.0 (At-Risk) ", cRisk, sum.totalStudents);
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        System.out.println("|                       2. STATUTORY ATTENDANCE & EXAM ELIGIBILITY METER                        |");
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        int attdHigh = 0, attdPass = 0, attdZone = 0, attdFail = 0;
        for (Student s : students) {
            double a = s.getAttendance();
            if (a < 65.0) attdFail++;
            else if (a < 75.0) attdZone++;
            else if (a < 85.0) attdPass++;
            else attdHigh++;
        }
        printHistogramBar(">=85% High/Honors", attdHigh, sum.totalStudents);
        printHistogramBar("75-84.9% Eligible", attdPass, sum.totalStudents);
        printHistogramBar("65-74.9% Cond.Zone", attdZone, sum.totalStudents);
        printHistogramBar("<65% Detained Bar", attdFail, sum.totalStudents);
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        System.out.println("|                             3. AT-RISK STUDENT MONITORING LIST                                |");
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        for (Student s : sum.studentsAtAcademicRisk) {
            AttendanceRecord ar = studentService.getRecordService().generateAttendanceRecord(s);
            String recovery = ar.getStatus() == AttendanceRecord.Status.ELIGIBLE ? "Compliant" : ("+" + ar.getClassesNeededFor75() + " classes needed");
            System.out.printf("|  * ID: %-10d | %-18s | Dept: %-4s | Attd: %5.1f%% | CGPA: %4.2f | %-16s |\n",
                    s.getStudentId(), s.getName(), s.getDepartment(), s.getAttendance(), s.getCgpa(), recovery);
        }
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        System.out.println("|                              DASHBOARD QUICK ACTIONS MENU                                     |");
        System.out.println("+-----------------------------------------------------------------------------------------------+");
        System.out.println("  1. Inspect Dedicated Student CGPA Scorecard");
        System.out.println("  2. View College-Wide CGPA Performance Ledger");
        System.out.println("  3. Inspect Dedicated Student Attendance Audit Card");
        System.out.println("  4. View Statutory Attendance Register (< 75% Defaulters)");
        System.out.println("  5. Generate Student Semester Grade Card / Marksheet");
        System.out.println("  6. View Student Fee Ledger & Payment Receipt");
        System.out.println("  7. Export Executive Dashboard Report to Disk");
        System.out.println("  8. Return to Master Menu");
        System.out.print("Enter action choice (1-8): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> studentMenu.handleSeparateCgpa();
            case "2" -> System.out.println(studentService.getRecordService().generateSeparateCgpaReport("ALL", null));
            case "3" -> studentMenu.handleSeparateAttendance();
            case "4" -> System.out.println(studentService.getRecordService().generateSeparateAttendanceReport("ALL", "SHORTAGE"));
            case "5" -> studentMenu.handleGradeCard();
            case "6" -> studentMenu.handleFeeLedger();
            case "7" -> handleReports();
            default -> { /* return */ }
        }
    }

    private void printHistogramBar(String label, int count, int total) {
        int barLength = total > 0 ? (int) Math.round(((double) count / total) * 35) : 0;
        String bar = "#".repeat(Math.max(0, barLength));
        double pct = total > 0 ? (count * 100.0 / total) : 0.0;
        System.out.printf("|  %-18s : %-35s %2d students (%5.1f%%) |\n", label, bar, count, pct);
    }

    private void handleRecordsAndCertificates() {
        while (true) {
            System.out.println("\n====================================================");
            System.out.println("   STUDENT RECORDS, GRADES & CERTIFICATION CENTER   ");
            System.out.println("====================================================");
            System.out.println("1. Generate & View Student Grade Card (Marksheet)");
            System.out.println("2. Generate & Print Official Institutional Certificates");
            System.out.println("3. Class-Wide Grade Tabulation Register");
            System.out.println("4. Official Student Identity Card (ID Card)");
            System.out.println("5. Student Fee Ledger & Payment Receipts");
            System.out.println("6. Attendance Shortage Warning Advisories (< 75%)");
            System.out.println("7. Official 10-Point Academic Grading Scale System");
            System.out.println("8. Show CGPA of Students Separately (Scorecard & Dept Roster)");
            System.out.println("9. Show Attendance of Students Separately (Register & Exam Eligibility)");
            System.out.println("10. Return to Master Menu");
            System.out.print("Enter choice (1-10): ");

            String ch = scanner.nextLine().trim();
            switch (ch) {
                case "1": studentMenu.handleGradeCard(); break;
                case "2": studentMenu.handleCertificates(); break;
                case "3": studentMenu.handleTabulationRegister(); break;
                case "4": studentMenu.handleIdCard(); break;
                case "5": studentMenu.handleFeeLedger(); break;
                case "6": studentMenu.handleAttendanceNotice(); break;
                case "7":
                    System.out.println("\n" + studentService.getRecordService().getGradingScaleInfo());
                    break;
                case "8": studentMenu.handleSeparateCgpa(); break;
                case "9": studentMenu.handleSeparateAttendance(); break;
                case "10": return;
                default:
                    System.out.println("Invalid selection. Please choose an option between 1 and 10.");
            }
        }
    }
}
