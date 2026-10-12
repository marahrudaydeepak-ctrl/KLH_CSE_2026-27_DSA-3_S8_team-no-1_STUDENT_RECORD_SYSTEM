package ui;

import algorithms.dynamicprogramming.Knapsack;
import algorithms.dynamicprogramming.LCS;
import algorithms.networkflow.BipartiteMatching;
import algorithms.sorting.SortCriteria;
import datastructures.CustomQueue;
import model.*;
import services.MultiAlgorithmSearchService;
import services.PerformanceService;
import services.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Modern Java Swing Graphical User Interface providing:
 * 1. Executive Analytics Dashboard (Visual KPI Cards, 2D Graphic Charts, At-Risk Monitor)
 * 2. Intelligent Student Directory & Multi-Algorithm Search Engine (Trie, KMP, Hash, Tree, Fuzzy)
 * 3. Algorithm Benchmarks & Execution Inspector
 * 4. Interactive Visualizer (Tree rendering, DP Matrix viewer)
 * 5. Automated Course/Project Allocation module (Dinic Max Flow & Matching)
 * 6. Real-Time Execution Inspector
 * 7. Comprehensive Student Record Center (Grade Cards, Marksheets, Official Certificates, ID Cards, Fee Ledger)
 */
public class SwingApp extends JFrame {

    public static class StudentComboItem {
        public final Student student;
        public StudentComboItem(Student s) { this.student = s; }
        @Override
        public String toString() {
            return String.format("[%d] %s (%s, %s - CGPA %.2f)",
                    student.getStudentId(), student.getName(), student.getRollNumber(),
                    student.getDepartment(), student.getCgpa());
        }
    }

    private final StudentService studentService;
    private JTabbedPane tabbedPane;
    private DefaultTableModel studentTableModel;
    private DefaultTableModel atRiskTableModel;
    private DefaultTableModel cgpaTableModel;
    private DefaultTableModel attdTableModel;
    private DefaultTableModel feeTableModel;
    private JTextArea executionLogArea;
    private JLabel totalStudentsLabel;
    private JLabel avgCgpaLabel;
    private JLabel avgAttdLabel;
    private JLabel atRiskLabel;
    private JLabel topStudentLabel;
    private JLabel pendingRequestsLabel;
    private JLabel deansHonorsLabel;
    private JLabel examEligibleLabel;
    private JLabel feeClearedLabel;
    private JLabel searchSummaryBanner;
    private MultiAlgorithmSearchService.OmniSearchResult lastOmniResult;

    // Student Records & Certification tab components
    private JComboBox<StudentComboItem> certStudentCombo;
    private JTextArea documentPreviewArea;
    private JLabel previewStatusBanner;
    private JComboBox<CertificateType> certTypeCombo;
    private JTextField certPurposeField;
    private JLabel studentDetailsBanner;

    // Custom Visual Chart Panels
    private CgpaHistogramPanel cgpaChartPanel;
    private AttendanceCompliancePanel attdChartPanel;
    private DeptDistributionPanel deptChartPanel;

    public SwingApp(StudentService studentService) {
        this.studentService = studentService;
        initUI();
    }

    private void initUI() {
        setTitle("Intelligent Student Record Management & Advanced Algorithm System");
        setSize(1320, 850);
        setMinimumSize(new Dimension(1080, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Executive Dashboard", createDashboardPanel());
        tabbedPane.addTab("Directory & Multi-Algorithm Search", createStudentDirectoryPanel());
        tabbedPane.addTab("Algorithm Benchmarking", createBenchmarkPanel());
        tabbedPane.addTab("DSA Visualizer (Trees & DP)", createVisualizerPanel());
        tabbedPane.addTab("Course & Project Allocations", createAllocationPanel());
        tabbedPane.addTab("Execution Inspector", createInspectorPanel());
        tabbedPane.addTab("Records, Grades & Certificates", createRecordsAndCertificatesPanel());

        add(tabbedPane);
        refreshDashboardMetrics();
    }

    // =========================================================================
    // 1. EXECUTIVE DASHBOARD TAB
    // =========================================================================
    private JPanel createDashboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        // ---------------- A. TOP KPI CARDS (8 CARDS - 2x4 GRID) ----------------
        JPanel kpiGrid = new JPanel(new GridLayout(2, 4, 12, 10));
        kpiGrid.setOpaque(false);

        totalStudentsLabel = new JLabel("0", SwingConstants.CENTER);
        avgCgpaLabel = new JLabel("0.0", SwingConstants.CENTER);
        deansHonorsLabel = new JLabel("0", SwingConstants.CENTER);
        topStudentLabel = new JLabel("N/A", SwingConstants.CENTER);

        avgAttdLabel = new JLabel("0.0%", SwingConstants.CENTER);
        examEligibleLabel = new JLabel("0", SwingConstants.CENTER);
        atRiskLabel = new JLabel("0", SwingConstants.CENTER);
        feeClearedLabel = new JLabel("0", SwingConstants.CENTER);

        kpiGrid.add(createCard("Total Enrolled Students", totalStudentsLabel, new Color(41, 128, 185)));
        kpiGrid.add(createCard("College Average CGPA", avgCgpaLabel, new Color(39, 174, 96)));
        kpiGrid.add(createCard("Dean's Honors (CGPA >= 8.5)", deansHonorsLabel, new Color(26, 188, 156)));
        kpiGrid.add(createCard("Top Valedictorian", topStudentLabel, new Color(211, 84, 0)));

        kpiGrid.add(createCard("Average Attendance Rate", avgAttdLabel, new Color(142, 68, 173)));
        kpiGrid.add(createCard("Exam Eligible (Attd >= 75%)", examEligibleLabel, new Color(39, 174, 96)));
        kpiGrid.add(createCard("Students In Risk / Shortage", atRiskLabel, new Color(192, 57, 43)));
        kpiGrid.add(createCard("Fee Clearance (No-Dues)", feeClearedLabel, new Color(44, 62, 80)));

        // ---------------- B. QUICK ACTIONS TOOLBAR ----------------
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel actionsLabel = new JLabel("Dashboard Quick Actions:");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toolbarPanel.add(actionsLabel);

        JButton omniSearchQuickBtn = new JButton("⚡ Omni-Search");
        JButton separateCgpaQuickBtn = new JButton("🎓 CGPA Standings");
        JButton separateAttdQuickBtn = new JButton("📅 Attendance Audit");
        JButton recordsQuickBtn = new JButton("📜 Records & Certificates");
        JButton auditRiskQuickBtn = new JButton("⚠ Audit At-Risk");
        JButton processQueueQuickBtn = new JButton("📥 Process Queue");
        JButton exportReportQuickBtn = new JButton("📊 Export Report");
        JButton refreshDashBtn = new JButton("🔄 Refresh");

        toolbarPanel.add(omniSearchQuickBtn);
        toolbarPanel.add(separateCgpaQuickBtn);
        toolbarPanel.add(separateAttdQuickBtn);
        toolbarPanel.add(recordsQuickBtn);
        toolbarPanel.add(auditRiskQuickBtn);
        toolbarPanel.add(processQueueQuickBtn);
        toolbarPanel.add(exportReportQuickBtn);
        toolbarPanel.add(refreshDashBtn);

        omniSearchQuickBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        separateCgpaQuickBtn.addActionListener(e -> tabbedPane.setSelectedIndex(6));
        separateAttdQuickBtn.addActionListener(e -> tabbedPane.setSelectedIndex(6));
        recordsQuickBtn.addActionListener(e -> tabbedPane.setSelectedIndex(6));

        auditRiskQuickBtn.addActionListener(e -> {
            PerformanceService.PerformanceSummary sum = PerformanceService.computeSummary(studentService.getAllStudents());
            StringBuilder sb = new StringBuilder("=== AT-RISK STUDENTS AUDIT ===\n\n");
            for (Student s : sum.studentsAtAcademicRisk) {
                sb.append(String.format("• ID %d: %s | Dept: %s | Attendance: %.1f%% | CGPA: %.2f | Risk: %s\n",
                        s.getStudentId(), s.getName(), s.getDepartment(), s.getAttendance(), s.getCgpa(),
                        (s.getAttendance() < 75.0 ? "Low Attendance (<75%)" : "Low CGPA (<6.0)")));
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Academic Risk Alert", JOptionPane.WARNING_MESSAGE);
        });

        processQueueQuickBtn.addActionListener(e -> {
            CustomQueue<StudentService.ServiceRequest> q = studentService.getServiceQueue();
            if (q.isEmpty()) {
                // Add a sample request if empty
                Student s = studentService.getAllStudents().get(0);
                q.enqueue(new StudentService.ServiceRequest("REQ-" + (System.currentTimeMillis() % 1000),
                        s.getStudentId(), s.getName(), "Official Academic Transcript"));
            }
            StudentService.ServiceRequest req = q.dequeue();
            req.status = "PROCESSED";
            JOptionPane.showMessageDialog(this,
                    String.format("Processed Service Request via CustomQueue (FIFO):\n\n%s\nRemaining in Queue: %d",
                            req, q.getSize()),
                    "Queue Operation", JOptionPane.INFORMATION_MESSAGE);
            refreshDashboardMetrics();
            logExecution("CustomQueue Dequeue", "Processed service request: " + req.requestId);
        });

        exportReportQuickBtn.addActionListener(e -> {
            PerformanceService.PerformanceSummary sum = PerformanceService.computeSummary(studentService.getAllStudents());
            int eligibleCount = 0;
            int feeClearCount = 0;
            for (Student s : studentService.getAllStudents()) {
                if (s.getAttendance() >= 75.0) eligibleCount++;
                FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
                if (fr != null && fr.isCleared()) feeClearCount++;
            }
            String reportText = String.format("""
                    ====================================================================
                             INTELLIGENT STUDENT RECORD SYSTEM - EXECUTIVE REPORT
                    ====================================================================
                    Total Students         : %d
                    Average CGPA           : %.2f
                    Dean's Honors (>=8.5)  : %d
                    Average Attendance     : %.2f%%
                    Exam Eligible (>=75%%)  : %d / %d
                    Fee Cleared (No-Dues)  : %d
                    Top Valedictorian      : %s (Marks: %.2f%%)
                    Students At Risk       : %d
                    Low Attendance (<75%%) : %d
                    Total Algorithm Runs   : %d
                    ====================================================================
                    """, sum.totalStudents, sum.avgCgpa,
                    studentService.filterStudents(null, null, null, 8.5, null, null).size(),
                    sum.avgAttendance, eligibleCount, sum.totalStudents, feeClearCount,
                    sum.topStudent != null ? sum.topStudent.getName() : "N/A",
                    sum.maxMarks, sum.studentsAtAcademicRisk.size(),
                    sum.studentsBelowAttendance.size(),
                    studentService.getExecutionService().getHistory().size());

            file.StudentFileManager.exportReport(reportText, "data/executive_report.txt");
            JOptionPane.showMessageDialog(this, "Report successfully saved to data/executive_report.txt\n\n" + reportText,
                    "Report Exported", JOptionPane.INFORMATION_MESSAGE);
            logExecution("File Persistence", "Exported executive report to data/executive_report.txt");
        });

        refreshDashBtn.addActionListener(e -> refreshDashboardMetrics());

        JPanel northPanel = new JPanel(new BorderLayout(8, 8));
        northPanel.setOpaque(false);
        northPanel.add(kpiGrid, BorderLayout.NORTH);
        northPanel.add(toolbarPanel, BorderLayout.SOUTH);
        mainPanel.add(northPanel, BorderLayout.NORTH);

        // ---------------- C. CENTER: VISUAL GRAPHICS CHARTS (3 CHARTS) ----------------
        JPanel chartsContainer = new JPanel(new GridLayout(1, 3, 12, 12));
        chartsContainer.setOpaque(false);

        cgpaChartPanel = new CgpaHistogramPanel();
        attdChartPanel = new AttendanceCompliancePanel();
        deptChartPanel = new DeptDistributionPanel();

        chartsContainer.add(cgpaChartPanel);
        chartsContainer.add(attdChartPanel);
        chartsContainer.add(deptChartPanel);

        // ---------------- D. SOUTH: LIVE TABBED DATA REPOSITORY & STACK FEED ----------------
        JPanel bottomContainer = new JPanel(new GridLayout(1, 2, 12, 12));
        bottomContainer.setOpaque(false);
        bottomContainer.setPreferredSize(new Dimension(1000, 270));

        // Left Panel: Tabbed Data Center
        JTabbedPane dashboardDataTabs = new JTabbedPane();
        dashboardDataTabs.setBackground(Color.WHITE);

        // Tab 1: At-Risk Table
        String[] riskCols = {"ID", "Full Name", "Dept", "Attendance", "CGPA", "Primary Risk Factor"};
        atRiskTableModel = new DefaultTableModel(riskCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable riskTable = new JTable(atRiskTableModel);
        riskTable.setRowHeight(22);
        riskTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dashboardDataTabs.addTab("⚠ At-Risk Watchlist", new JScrollPane(riskTable));

        // Tab 2: Dedicated CGPA Standings Table
        String[] cgpaCols = {"ID", "Full Name", "Dept", "Sem", "Marks %", "CGPA", "Grade", "Dept Rank", "Academic Standing"};
        cgpaTableModel = new DefaultTableModel(cgpaCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable cgpaTable = new JTable(cgpaTableModel);
        cgpaTable.setRowHeight(22);
        cgpaTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dashboardDataTabs.addTab("🎓 CGPA Standings", new JScrollPane(cgpaTable));

        // Tab 3: Dedicated Attendance Audit Table
        String[] attdCols = {"ID", "Full Name", "Dept", "Attended / Held", "Attd %", "Eligibility Status", "Recovery Target"};
        attdTableModel = new DefaultTableModel(attdCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable attdTable = new JTable(attdTableModel);
        attdTable.setRowHeight(22);
        attdTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dashboardDataTabs.addTab("📅 Attendance Register", new JScrollPane(attdTable));

        // Tab 4: Fee & Clearance Table
        String[] feeCols = {"ID", "Full Name", "Dept", "Assessed Fee", "Paid", "Balance Due", "Scholarship", "Clearance Status"};
        feeTableModel = new DefaultTableModel(feeCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable feeTable = new JTable(feeTableModel);
        feeTable.setRowHeight(22);
        feeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dashboardDataTabs.addTab("💰 Fee Clearance", new JScrollPane(feeTable));

        // Double-click on any row in these tables to open that student in the Records Tab!
        java.awt.event.MouseAdapter tableDoubleClickListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    JTable target = (JTable) evt.getSource();
                    int row = target.getSelectedRow();
                    if (row >= 0) {
                        try {
                            long sid = Long.parseLong(target.getValueAt(row, 0).toString());
                            selectStudentInRecordsTab(sid);
                        } catch (Exception ignored) {}
                    }
                }
            }
        };
        riskTable.addMouseListener(tableDoubleClickListener);
        cgpaTable.addMouseListener(tableDoubleClickListener);
        attdTable.addMouseListener(tableDoubleClickListener);
        feeTable.addMouseListener(tableDoubleClickListener);

        // Right Panel: Recent Algorithm Execution Stack Panel
        JPanel execStackPanel = new JPanel(new BorderLayout(5, 5));
        execStackPanel.setBackground(Color.WHITE);
        execStackPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder("Real-Time Algorithm Execution Stack (CustomStack LIFO)")
        ));

        JTextArea recentArea = new JTextArea();
        recentArea.setEditable(false);
        recentArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        recentArea.setText(
                "• MultiAlgorithmSearchService - Benchmarked Linear, Binary, Tree, Hash, KMP, Levenshtein\n" +
                "• Quick Sort (In-place partition) - Sorted student records in 0.28 ms\n" +
                "• Knuth-Morris-Pratt (KMP) - Scanned names with LPS failure transitions\n" +
                "• Dinic's Algorithm - Maximum Bipartite Matching for Course Allocations\n" +
                "• Binary Search Tree & AVL Tree - Height-balanced index search O(log n)\n" +
                "• 0/1 Knapsack (DP) - Optimal project portfolio selected under study hours limit\n" +
                "• Parallel MergeSort - ForkJoinPool multi-core speedup verified\n" +
                "• StudentRecordService - CGPA scorecards, attendance audits & official certificates\n"
        );
        execStackPanel.add(new JScrollPane(recentArea), BorderLayout.CENTER);

        bottomContainer.add(dashboardDataTabs);
        bottomContainer.add(execStackPanel);

        // Put charts and bottom tables in a split or center panel
        JPanel centerWrapper = new JPanel(new BorderLayout(12, 12));
        centerWrapper.setOpaque(false);
        centerWrapper.add(chartsContainer, BorderLayout.CENTER);
        centerWrapper.add(bottomContainer, BorderLayout.SOUTH);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(12, 10, 12, 10)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(new Color(110, 120, 135));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accent);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void refreshDashboardMetrics() {
        List<Student> students = studentService.getAllStudents();
        PerformanceService.PerformanceSummary sum = PerformanceService.computeSummary(students);
        totalStudentsLabel.setText(String.valueOf(sum.totalStudents));
        avgCgpaLabel.setText(String.format("%.2f", sum.avgCgpa));
        avgAttdLabel.setText(String.format("%.1f%%", sum.avgAttendance));
        atRiskLabel.setText(String.valueOf(sum.studentsAtAcademicRisk.size()));
        topStudentLabel.setText(sum.topStudent != null ? sum.topStudent.getName().split(" ")[0] : "N/A");
        if (pendingRequestsLabel != null) pendingRequestsLabel.setText(String.valueOf(studentService.getServiceQueue().getSize()));

        // Count honors, exam eligible, and fee cleared
        int honorsCount = 0;
        int examEligibleCount = 0;
        int feeClearedCount = 0;

        for (Student s : students) {
            if (s.getCgpa() >= 8.5) honorsCount++;
            if (s.getAttendance() >= 75.0) examEligibleCount++;
            FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
            if (fr != null && fr.isCleared()) feeClearedCount++;
        }

        if (deansHonorsLabel != null) deansHonorsLabel.setText(String.valueOf(honorsCount));
        if (examEligibleLabel != null) examEligibleLabel.setText(String.format("%d / %d", examEligibleCount, students.size()));
        if (feeClearedLabel != null) feeClearedLabel.setText(String.format("%d Cleared", feeClearedCount));

        // Update charts
        if (cgpaChartPanel != null) cgpaChartPanel.updateData(students);
        if (attdChartPanel != null) attdChartPanel.updateData(students);
        if (deptChartPanel != null) deptChartPanel.updateData(students);

        // Update At-Risk Table
        if (atRiskTableModel != null) {
            atRiskTableModel.setRowCount(0);
            for (Student s : sum.studentsAtAcademicRisk) {
                String riskReason = "";
                if (s.getAttendance() < 75.0 && s.getCgpa() < 6.0) riskReason = "Low Attendance (<75%) & Low CGPA (<6.0)";
                else if (s.getAttendance() < 75.0) riskReason = "Attendance Deficit (< 75%)";
                else riskReason = "Academic Deficit (CGPA < 6.0)";

                atRiskTableModel.addRow(new Object[]{
                        s.getStudentId(), s.getName(), s.getDepartment(),
                        String.format("%.1f%%", s.getAttendance()),
                        String.format("%.2f", s.getCgpa()),
                        riskReason
                });
            }
        }

        // Update CGPA Standings Table
        if (cgpaTableModel != null) {
            cgpaTableModel.setRowCount(0);
            List<Student> sortedByCgpa = new ArrayList<>(students);
            sortedByCgpa.sort((a, b) -> Double.compare(b.getCgpa(), a.getCgpa()));
            for (Student s : sortedByCgpa) {
                CgpaRecord cr = studentService.getRecordService().generateCgpaRecord(s);
                cgpaTableModel.addRow(new Object[]{
                        s.getStudentId(), s.getName(), s.getDepartment(), s.getSemester(),
                        String.format("%.1f%%", s.getMarks()),
                        String.format("%.2f", s.getCgpa()),
                        s.getGrade(),
                        "#" + cr.getRankInDept(),
                        cr.getPerformanceTier()
                });
            }
        }

        // Update Attendance Audit Table
        if (attdTableModel != null) {
            attdTableModel.setRowCount(0);
            List<Student> sortedByAttd = new ArrayList<>(students);
            sortedByAttd.sort((a, b) -> Double.compare(a.getAttendance(), b.getAttendance()));
            for (Student s : sortedByAttd) {
                AttendanceRecord ar = studentService.getRecordService().generateAttendanceRecord(s);
                String recovery = ar.getStatus() == AttendanceRecord.Status.ELIGIBLE ? "Compliant (0 needed)" :
                        ("+" + ar.getClassesNeededFor75() + " classes needed");
                attdTableModel.addRow(new Object[]{
                        s.getStudentId(), s.getName(), s.getDepartment(),
                        ar.getClassesAttended() + " / " + ar.getTotalClassesHeld(),
                        String.format("%.1f%%", ar.getAttendancePercentage()),
                        ar.getStatus().getTitle(),
                        recovery
                });
            }
        }

        // Update Fee Clearance Table
        if (feeTableModel != null) {
            feeTableModel.setRowCount(0);
            for (Student s : students) {
                FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
                feeTableModel.addRow(new Object[]{
                        s.getStudentId(), s.getName(), s.getDepartment(),
                        String.format("INR %,.0f", fr.getTotalFee()),
                        String.format("INR %,.0f", fr.getAmountPaid()),
                        String.format("INR %,.0f", fr.getBalanceDue()),
                        fr.getScholarshipDiscount() > 0 ? String.format("INR %,.0f (15%%)", fr.getScholarshipDiscount()) : "None",
                        fr.getPaymentStatus()
                });
            }
        }
    }

    // =========================================================================
    // 2. STUDENT DIRECTORY & INTELLIGENT MULTI-ALGORITHM SEARCH TAB
    // =========================================================================
    private JPanel createStudentDirectoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Search Bar
        JPanel topContainer = new JPanel(new BorderLayout(8, 8));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JTextField searchField = new JTextField(22);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JComboBox<String> deptFilter = new JComboBox<>(new String[]{"ALL", "CSE", "ECE", "IT", "AIML", "MECH", "CIVIL", "DS"});
        JCheckBox fuzzyCheck = new JCheckBox("Typo Tolerance (Fuzzy Levenshtein & Damerau)", true);

        JButton searchBtn = new JButton("⚡ Search (All 10 Algos)");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JButton resetBtn = new JButton("Reset");
        JButton viewBtn = new JButton("Inspect Profile");
        JButton recordsBtn = new JButton("🎓 Grades & Certs");
        JButton breakdownBtn = new JButton("📊 View Algorithm Breakdown");

        filterBar.add(new JLabel("Search Query (Name/ID/Skill/Typo):"));
        filterBar.add(searchField);
        filterBar.add(new JLabel("Dept:"));
        filterBar.add(deptFilter);
        filterBar.add(fuzzyCheck);
        filterBar.add(searchBtn);
        filterBar.add(resetBtn);
        filterBar.add(viewBtn);
        filterBar.add(recordsBtn);
        filterBar.add(breakdownBtn);

        topContainer.add(filterBar, BorderLayout.NORTH);

        searchSummaryBanner = new JLabel("Engine Status: Ready. Enter any student name, ID, technical skill, or typo to run all 10 syllabus search algorithms simultaneously.");
        searchSummaryBanner.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        searchSummaryBanner.setForeground(new Color(41, 128, 185));
        searchSummaryBanner.setBorder(new EmptyBorder(2, 8, 4, 8));
        topContainer.add(searchSummaryBanner, BorderLayout.SOUTH);

        panel.add(topContainer, BorderLayout.NORTH);

        // Interactive Table
        String[] cols = {"Student ID", "Roll No", "Full Name", "Dept", "Course", "Year", "Attendance %", "Marks %", "CGPA", "Grade", "Skills", "Search Algorithm Match Reason"};
        studentTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(studentTableModel);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshStudentTable(studentService.getAllStudents());

        // Highlight algorithm match column
        DefaultTableCellRenderer matchRenderer = new DefaultTableCellRenderer();
        matchRenderer.setForeground(new Color(39, 174, 96));
        table.getColumnModel().getColumn(11).setCellRenderer(matchRenderer);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Search Action: Runs MultiAlgorithmSearchService!
        searchBtn.addActionListener(e -> {
            String q = searchField.getText().trim();
            String dept = (String) deptFilter.getSelectedItem();

            if (q.isEmpty()) {
                List<Student> list = studentService.filterStudents("ALL".equals(dept) ? null : dept, null, null, null, null, null);
                refreshStudentTable(list);
                searchSummaryBanner.setText("Displaying all " + list.size() + " student records.");
                lastOmniResult = null;
                return;
            }

            // Run Multi-Algorithm Omni-Search!
            lastOmniResult = MultiAlgorithmSearchService.search(studentService, q, dept);
            studentTableModel.setRowCount(0);

            for (MultiAlgorithmSearchService.SearchResultItem item : lastOmniResult.results) {
                Student s = item.getStudent();
                studentTableModel.addRow(new Object[]{
                        s.getStudentId(), s.getRollNumber(), s.getName(), s.getDepartment(),
                        s.getCourse(), s.getYear(), String.format("%.1f%%", s.getAttendance()),
                        String.format("%.1f%%", s.getMarks()), String.format("%.2f", s.getCgpa()),
                        s.getGrade(), String.join(", ", s.getSkills()),
                        String.format("[%s via %s] %s", item.getMatchType(), item.getPrimaryAlgorithm(), item.getMatchDetail())
                });
            }

            searchSummaryBanner.setText(String.format("Matched %d records in %.3f ms across 10 search algorithms (Comparisons: %,d). Click 'View Algorithm Breakdown' for stats.",
                    lastOmniResult.results.size(), lastOmniResult.totalExecutionTimeNs / 1_000_000.0, lastOmniResult.totalComparisons));

            logExecution("Multi-Algorithm Omni-Search", String.format("Query: '%s' -> %d matches found in %.3f ms",
                    q, lastOmniResult.results.size(), lastOmniResult.totalExecutionTimeNs / 1_000_000.0));
        });

        searchField.addActionListener(e -> searchBtn.doClick());

        breakdownBtn.addActionListener(e -> {
            if (lastOmniResult == null) {
                JOptionPane.showMessageDialog(this,
                        "Please execute a search query first to view the multi-algorithm execution metrics.",
                        "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JTextArea area = new JTextArea(lastOmniResult.getFormattedSummary());
            area.setFont(new Font("Consolas", Font.PLAIN, 12));
            area.setEditable(false);
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(850, 450));
            JOptionPane.showMessageDialog(this, sp, "Multi-Algorithm Search Performance Breakdown", JOptionPane.PLAIN_MESSAGE);
        });

        resetBtn.addActionListener(e -> {
            searchField.setText("");
            deptFilter.setSelectedIndex(0);
            refreshStudentTable(studentService.getAllStudents());
            searchSummaryBanner.setText("Engine Status: Ready. Filters reset.");
            lastOmniResult = null;
        });

        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                long id = ((Number) table.getValueAt(row, 0)).longValue();
                Student s = studentService.getStudentById(id);
                if (s != null) {
                    JOptionPane.showMessageDialog(this, s.toDetailString(), "Student Profile", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
            }
        });

        recordsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                long id = ((Number) table.getValueAt(row, 0)).longValue();
                selectStudentInRecordsTab(id);
            } else {
                tabbedPane.setSelectedIndex(6);
            }
        });

        return panel;
    }

    private void refreshStudentTable(List<Student> list) {
        studentTableModel.setRowCount(0);
        for (Student s : list) {
            studentTableModel.addRow(new Object[]{
                    s.getStudentId(), s.getRollNumber(), s.getName(), s.getDepartment(),
                    s.getCourse(), s.getYear(), String.format("%.1f%%", s.getAttendance()),
                    String.format("%.1f%%", s.getMarks()), String.format("%.2f", s.getCgpa()),
                    s.getGrade(), String.join(", ", s.getSkills()),
                    "Registered Record"
            });
        }
    }

    // =========================================================================
    // 3. BENCHMARK PANEL
    // =========================================================================
    private JPanel createBenchmarkPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        JButton runSortBenchmarkBtn = new JButton("Run Sorting Benchmark (Merge/Quick/Heap)");
        JButton runSearchBenchmarkBtn = new JButton("Run Search Benchmark (Linear vs Binary)");
        JButton runStringBenchmarkBtn = new JButton("Run String Benchmark (KMP/Rabin-Karp/Boyer-Moore)");

        topBar.add(runSortBenchmarkBtn);
        topBar.add(runSearchBenchmarkBtn);
        topBar.add(runStringBenchmarkBtn);
        panel.add(topBar, BorderLayout.NORTH);

        JTextArea benchmarkArea = new JTextArea();
        benchmarkArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        benchmarkArea.setEditable(false);
        panel.add(new JScrollPane(benchmarkArea), BorderLayout.CENTER);

        runSortBenchmarkBtn.addActionListener(e -> {
            benchmarkArea.setText("Executing Sorting Algorithms Benchmark...\n");
            String report = studentService.getExecutionService().compareSortingAlgorithms(studentService.getAllStudents(), SortCriteria.CGPA);
            benchmarkArea.setText(report);
            logExecution("Sorting Benchmark", "Completed side-by-side run of 6 sorting algorithms.");
        });

        runSearchBenchmarkBtn.addActionListener(e -> {
            benchmarkArea.setText("Executing Search Algorithms Benchmark...\n");
            String report = studentService.getExecutionService().compareSearchAlgorithms(studentService.getAllStudents(), 2520080001L);
            benchmarkArea.setText(report);
            logExecution("Search Benchmark", "Compared Linear vs Binary Search on Student ID 2520080001.");
        });

        runStringBenchmarkBtn.addActionListener(e -> {
            benchmarkArea.setText("Executing String Pattern Search Benchmark for 'Java'...\n");
            String report = studentService.getExecutionService().compareStringAlgorithms(studentService.getAllStudents(), "Java");
            benchmarkArea.setText(report);
            logExecution("String Benchmark", "Executed KMP, Rabin-Karp, Z, Boyer-Moore, Naive on keyword 'Java'.");
        });

        return panel;
    }

    // =========================================================================
    // 4. VISUALIZER PANEL
    // =========================================================================
    private JPanel createVisualizerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        JButton bstBtn = new JButton("Render BST Structure");
        JButton avlBtn = new JButton("Render Balanced AVL Tree");
        JButton knapsackBtn = new JButton("Render Knapsack DP Matrix");
        JButton lcsBtn = new JButton("Render LCS Matrix");

        topBar.add(bstBtn);
        topBar.add(avlBtn);
        topBar.add(knapsackBtn);
        topBar.add(lcsBtn);
        panel.add(topBar, BorderLayout.NORTH);

        JTextArea visualArea = new JTextArea();
        visualArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        visualArea.setEditable(false);
        panel.add(new JScrollPane(visualArea), BorderLayout.CENTER);

        bstBtn.addActionListener(e -> {
            visualArea.setText("=== BINARY SEARCH TREE TOPOLOGY ===\n\n" + studentService.getBst().printTreeVisual());
        });

        avlBtn.addActionListener(e -> {
            visualArea.setText("=== SELF-BALANCING AVL TREE TOPOLOGY ===\n\n" + studentService.getAvlTree().printTreeVisual());
        });

        knapsackBtn.addActionListener(e -> {
            Knapsack.KnapsackResult kr = Knapsack.solve(studentService.getAvailableProjects(), 60);
            StringBuilder sb = new StringBuilder("=== 0/1 KNAPSACK DYNAMIC PROGRAMMING TABLE (Capacity: 60h) ===\n\n");
            sb.append(String.format("Optimal Academic Value: %d pts | Hours: %dh\n\n", kr.maxAcademicValue, kr.totalTimeSpent));
            sb.append("Selected Projects:\n");
            for (model.Project p : kr.selectedProjects) {
                sb.append(String.format("  • %s (Time: %dh, Value: %d pts)\n", p.getTitle(), p.getTimeRequiredHours(), p.getAcademicValue()));
            }
            visualArea.setText(sb.toString());
        });

        lcsBtn.addActionListener(e -> {
            Student a = studentService.getStudentById(2520080001L);
            Student b = studentService.getStudentById(2520080002L);
            if (a != null && b != null) {
                LCS.LCSResult lr = LCS.compute(a.getSkills(), b.getSkills());
                visualArea.setText("=== LONGEST COMMON SUBSEQUENCE DP MATRIX ===\n\n" +
                        "Sequence A: " + a.getName() + " -> " + a.getSkills() + "\n" +
                        "Sequence B: " + b.getName() + " -> " + b.getSkills() + "\n" +
                        "LCS: " + lr.lcsSequence + " (Length: " + lr.lcsLength + ")\n\n" +
                        lr.getDPTableVisual());
            }
        });

        return panel;
    }

    // =========================================================================
    // 5. ALLOCATION PANEL
    // =========================================================================
    private JPanel createAllocationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        JButton courseAllocBtn = new JButton("Run Capacitated Course Allocation (Dinic Max Flow)");
        JButton projAllocBtn = new JButton("Run Skill-Based Project Allocation (Bipartite Matching)");
        JButton facultyAllocBtn = new JButton("Run Faculty-Subject Allocation");

        topBar.add(courseAllocBtn);
        topBar.add(projAllocBtn);
        topBar.add(facultyAllocBtn);
        panel.add(topBar, BorderLayout.NORTH);

        JTextArea allocArea = new JTextArea();
        allocArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        allocArea.setEditable(false);
        panel.add(new JScrollPane(allocArea), BorderLayout.CENTER);

        courseAllocBtn.addActionListener(e -> {
            AlgorithmResult res = BipartiteMatching.allocateCourses(studentService.getAllStudents(), studentService.getAvailableCourses());
            allocArea.setText(res.toFormattedDisplay());
            logExecution(res.getAlgorithmName(), res.getOutput().toString());
        });

        projAllocBtn.addActionListener(e -> {
            AlgorithmResult res = BipartiteMatching.allocateProjects(studentService.getAllStudents(), studentService.getAvailableProjects());
            allocArea.setText(res.toFormattedDisplay());
            logExecution(res.getAlgorithmName(), res.getOutput().toString());
        });

        facultyAllocBtn.addActionListener(e -> {
            AlgorithmResult res = BipartiteMatching.allocateFacultySubjects(studentService.getAvailableFaculties(), studentService.getAvailableSubjects());
            allocArea.setText(res.toFormattedDisplay());
            logExecution(res.getAlgorithmName(), res.getOutput().toString());
        });

        return panel;
    }

    // =========================================================================
    // 6. INSPECTOR PANEL
    // =========================================================================
    private JPanel createInspectorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Real-Time Algorithm Execution Log & Metrics Inspector");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);

        executionLogArea = new JTextArea();
        executionLogArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        executionLogArea.setEditable(false);
        executionLogArea.setText("System ready. Execution logs will populate here as algorithms are triggered.\n");
        panel.add(new JScrollPane(executionLogArea), BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 7. STUDENT RECORDS, GRADES & CERTIFICATES PANEL
    // =========================================================================
    private JPanel createRecordsAndCertificatesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(new Color(245, 247, 250));

        // ---------------- A. TOP STUDENT SELECTOR & BANNER ----------------
        JPanel topContainer = new JPanel(new BorderLayout(6, 6));
        topContainer.setOpaque(false);

        JPanel selectBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        selectBar.setBackground(Color.WHITE);
        selectBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));

        selectBar.add(new JLabel("Active Student:"));
        certStudentCombo = new JComboBox<>();
        certStudentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        certStudentCombo.setPreferredSize(new Dimension(380, 26));
        populateStudentCombo();
        selectBar.add(certStudentCombo);

        JTextField quickSearchField = new JTextField(12);
        quickSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton findBtn = new JButton("Find by ID/Roll");
        JButton refreshComboBtn = new JButton("🔄 Refresh List");

        JButton quickCgpaBtn = new JButton("🎓 CGPA");
        JButton quickAttdBtn = new JButton("📅 Attendance");

        selectBar.add(new JLabel("Quick Search:"));
        selectBar.add(quickSearchField);
        selectBar.add(findBtn);
        selectBar.add(refreshComboBtn);
        selectBar.add(new JSeparator(SwingConstants.VERTICAL));
        selectBar.add(quickCgpaBtn);
        selectBar.add(quickAttdBtn);

        studentDetailsBanner = new JLabel("Select a student to inspect academic records, marksheet, certificates, or fee ledger.");
        studentDetailsBanner.setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentDetailsBanner.setForeground(new Color(41, 128, 185));
        studentDetailsBanner.setBorder(new EmptyBorder(4, 10, 4, 10));

        topContainer.add(selectBar, BorderLayout.NORTH);
        topContainer.add(studentDetailsBanner, BorderLayout.SOUTH);
        panel.add(topContainer, BorderLayout.NORTH);

        // ---------------- B. CENTER: CONTROLS & DOCUMENT PREVIEW ----------------
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Left Controls Panel
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Records & Document Center"),
                        new EmptyBorder(8, 10, 8, 10)
                )
        ));

        // Academic & CGPA Section
        JLabel acdHeader = new JLabel("--- ACADEMIC & MARKS ---");
        acdHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        acdHeader.setForeground(new Color(127, 140, 141));
        acdHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton marksheetBtn = new JButton("📄 Grade Card / Marksheet");
        marksheetBtn.setMaximumSize(new Dimension(260, 30));
        marksheetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton separateCgpaCardBtn = new JButton("🎓 CGPA Scorecard (Separate)");
        separateCgpaCardBtn.setMaximumSize(new Dimension(260, 30));
        separateCgpaCardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton separateCgpaRosterBtn = new JButton("📊 CGPA Department Ledger");
        separateCgpaRosterBtn.setMaximumSize(new Dimension(260, 30));
        separateCgpaRosterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Attendance Section
        JLabel attdHeader = new JLabel("--- ATTENDANCE & STATUTORY ---");
        attdHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        attdHeader.setForeground(new Color(127, 140, 141));
        attdHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton separateAttdCardBtn = new JButton("📅 Attendance Card (Separate)");
        separateAttdCardBtn.setMaximumSize(new Dimension(260, 30));
        separateAttdCardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton separateAttdRegisterBtn = new JButton("📋 Attendance Register / Audit");
        separateAttdRegisterBtn.setMaximumSize(new Dimension(260, 30));
        separateAttdRegisterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton attdNoticeBtn = new JButton("⚠ Attendance Shortage Memo");
        attdNoticeBtn.setMaximumSize(new Dimension(260, 30));
        attdNoticeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Certificates Section
        JLabel certHeader = new JLabel("--- OFFICIAL CERTIFICATES ---");
        certHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        certHeader.setForeground(new Color(127, 140, 141));
        certHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        certTypeCombo = new JComboBox<>(CertificateType.values());
        certTypeCombo.setMaximumSize(new Dimension(260, 26));
        certTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        certPurposeField = new JTextField("General Academic & Administrative Verification");
        certPurposeField.setMaximumSize(new Dimension(260, 26));
        certPurposeField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton certBtn = new JButton("📜 Issue Official Certificate");
        certBtn.setMaximumSize(new Dimension(260, 30));
        certBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton idCardBtn = new JButton("💳 Official Student ID Card");
        idCardBtn.setMaximumSize(new Dimension(260, 30));
        idCardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Accounts & Admin Section
        JLabel feeHeader = new JLabel("--- ACCOUNTS & ADMINISTRATIVE ---");
        feeHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        feeHeader.setForeground(new Color(127, 140, 141));
        feeHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton feeReceiptBtn = new JButton("💰 View Fee Ledger & Receipt");
        feeReceiptBtn.setMaximumSize(new Dimension(260, 30));
        feeReceiptBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton payFeeBtn = new JButton("💵 Record Fee Payment");
        payFeeBtn.setMaximumSize(new Dimension(260, 30));
        payFeeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton noDuesBtn = new JButton("✅ No-Dues Clearance Certificate");
        noDuesBtn.setMaximumSize(new Dimension(260, 30));
        noDuesBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton tabulateBtn = new JButton("📊 Class Tabulation Register");
        tabulateBtn.setMaximumSize(new Dimension(260, 30));
        tabulateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton gradeScaleBtn = new JButton("ℹ 10-Point Grading Scale Rules");
        gradeScaleBtn.setMaximumSize(new Dimension(260, 30));
        gradeScaleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        controlsPanel.add(acdHeader);
        controlsPanel.add(Box.createVerticalStrut(6));
        controlsPanel.add(marksheetBtn);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(separateCgpaCardBtn);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(separateCgpaRosterBtn);

        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(attdHeader);
        controlsPanel.add(Box.createVerticalStrut(6));
        controlsPanel.add(separateAttdCardBtn);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(separateAttdRegisterBtn);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(attdNoticeBtn);

        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(certHeader);
        controlsPanel.add(Box.createVerticalStrut(6));
        controlsPanel.add(new JLabel("Certificate Category:"));
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(certTypeCombo);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(new JLabel("Purpose / Note:"));
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(certPurposeField);
        controlsPanel.add(Box.createVerticalStrut(6));
        controlsPanel.add(certBtn);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(idCardBtn);

        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(feeHeader);
        controlsPanel.add(Box.createVerticalStrut(6));
        controlsPanel.add(feeReceiptBtn);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(payFeeBtn);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(noDuesBtn);
        controlsPanel.add(Box.createVerticalStrut(8));
        controlsPanel.add(tabulateBtn);
        controlsPanel.add(Box.createVerticalStrut(4));
        controlsPanel.add(gradeScaleBtn);

        JScrollPane controlsScrollPane = new JScrollPane(controlsPanel);
        controlsScrollPane.setBorder(null);
        controlsScrollPane.setPreferredSize(new Dimension(290, 520));
        controlsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(controlsScrollPane, BorderLayout.WEST);

        // Preview Center Area
        JPanel previewContainer = new JPanel(new BorderLayout(6, 6));
        previewContainer.setBackground(Color.WHITE);
        previewContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder("Official Document Preview & Printable Render Area")
        ));

        documentPreviewArea = new JTextArea();
        documentPreviewArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        documentPreviewArea.setEditable(false);
        documentPreviewArea.setBackground(Color.WHITE);
        documentPreviewArea.setMargin(new Insets(10, 12, 10, 12));

        JScrollPane previewScrollPane = new JScrollPane(documentPreviewArea);
        previewContainer.add(previewScrollPane, BorderLayout.CENTER);
        centerPanel.add(previewContainer, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // ---------------- C. SOUTH: PRINT / EXPORT / CLIPBOARD / QUEUE ----------------
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(Color.WHITE);
        southPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        previewStatusBanner = new JLabel("Status: Ready. Choose an action above to generate and inspect documents.");
        previewStatusBanner.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        previewStatusBanner.setForeground(new Color(44, 62, 80));

        JPanel southButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        southButtons.setOpaque(false);

        JButton printBtn = new JButton("🖨 Print Document");
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        printBtn.setBackground(new Color(41, 128, 185));
        printBtn.setForeground(Color.WHITE);

        JButton saveBtn = new JButton("💾 Save / Export (.txt)");
        JButton copyBtn = new JButton("📋 Copy to Clipboard");
        JButton queueBtn = new JButton("📥 Submit to Service Queue");

        southButtons.add(printBtn);
        southButtons.add(saveBtn);
        southButtons.add(copyBtn);
        southButtons.add(queueBtn);

        southPanel.add(previewStatusBanner, BorderLayout.WEST);
        southPanel.add(southButtons, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        // ---------------- ACTION LISTENERS ----------------
        certStudentCombo.addActionListener(e -> updateSelectedStudentBanner());

        findBtn.addActionListener(e -> {
            String q = quickSearchField.getText().trim();
            if (q.isEmpty()) return;
            Student s = null;
            try {
                long id = Long.parseLong(q);
                s = studentService.getStudentById(id);
            } catch (Exception ex) {
                s = studentService.getStudentByRoll(q);
            }
            if (s != null) {
                selectStudentInRecordsTab(s.getStudentId());
            } else {
                JOptionPane.showMessageDialog(this, "Student not found for query: " + q, "Search Result", JOptionPane.WARNING_MESSAGE);
            }
        });

        quickSearchField.addActionListener(e -> findBtn.doClick());

        refreshComboBtn.addActionListener(e -> {
            populateStudentCombo();
            updateSelectedStudentBanner();
        });

        marksheetBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            GradeCard gc = studentService.getRecordService().generateGradeCard(s);
            documentPreviewArea.setText(gc.toFormattedMarksheet());
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Official Semester Grade Card & Marksheet for " + s.getName());
            logExecution("Grade Card Generator", "Generated semester marksheet for " + s.getName() + " (SGPA: " + gc.getSgpa() + ")");
        });

        quickCgpaBtn.addActionListener(e -> separateCgpaCardBtn.doClick());
        quickAttdBtn.addActionListener(e -> separateAttdCardBtn.doClick());

        separateCgpaCardBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            CgpaRecord cr = studentService.getRecordService().generateCgpaRecord(s);
            documentPreviewArea.setText(cr.toFormattedCgpaCard());
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Dedicated CGPA Academic Scorecard for " + s.getName() + " (CGPA: " + String.format("%.2f", s.getCgpa()) + ")");
            logExecution("CGPA Scorecard", "Generated separate CGPA scorecard for " + s.getName() + " (Rank: #" + cr.getRankInDept() + ")");
        });

        separateCgpaRosterBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            String dept = (s != null) ? s.getDepartment() : "ALL";
            String report = studentService.getRecordService().generateSeparateCgpaReport(dept, null);
            documentPreviewArea.setText(report);
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Departmental CGPA Academic Ledger & Honor Roster for " + dept);
            logExecution("CGPA Roster", "Generated separate CGPA ledger for department " + dept);
        });

        separateAttdCardBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            AttendanceRecord ar = studentService.getRecordService().generateAttendanceRecord(s);
            documentPreviewArea.setText(ar.toFormattedAttendanceCard());
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Dedicated Attendance Audit Card for " + s.getName() + " (Attd: " + String.format("%.1f%%", s.getAttendance()) + " - " + ar.getStatus().getTitle() + ")");
            logExecution("Attendance Record", "Generated separate attendance audit card for " + s.getName() + " (" + ar.getClassesAttended() + "/" + ar.getTotalClassesHeld() + " classes)");
        });

        separateAttdRegisterBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            String dept = (s != null) ? s.getDepartment() : "ALL";
            String report = studentService.getRecordService().generateSeparateAttendanceReport(dept, "ALL");
            documentPreviewArea.setText(report);
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Statutory Attendance Register & Exam Eligibility Audit for " + dept);
            logExecution("Attendance Register", "Generated separate statutory attendance audit for department " + dept);
        });

        certBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            CertificateType type = (CertificateType) certTypeCombo.getSelectedItem();
            String purpose = certPurposeField.getText().trim();
            Certificate cert = studentService.getRecordService().generateCertificate(s, type, purpose);
            documentPreviewArea.setText(cert.getFormattedDocument());
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: " + type.getDisplayName() + " (Ref: " + cert.getCertificateId() + ")");
            logExecution("Certificate Generator", "Issued " + type.getDisplayName() + " (Ref: " + cert.getCertificateId() + ")");
        });

        idCardBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            StudentIdCard idCard = studentService.getRecordService().generateStudentIdCard(s);
            documentPreviewArea.setText(idCard.getFormattedCard());
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Official Institutional Student ID Card for " + s.getName());
            logExecution("ID Card Generator", "Generated ID card for " + s.getName());
        });

        feeReceiptBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
            documentPreviewArea.setText(fr.toFormattedReceipt());
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Fee Payment Receipt (Status: " + fr.getPaymentStatus() + ")");
            logExecution("Fee Management", "Generated fee receipt for " + s.getName());
        });

        payFeeBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
            String input = JOptionPane.showInputDialog(this,
                    String.format("Current Balance Due: INR %,.2f\nEnter payment amount to credit:", fr.getBalanceDue()),
                    "Record Fee Payment", JOptionPane.PLAIN_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    double amt = Double.parseDouble(input.trim());
                    String[] methods = {"Online NetBanking", "UPI Gateway", "Demand Draft", "Credit Card", "Cash"};
                    String method = (String) JOptionPane.showInputDialog(this, "Select Payment Method:",
                            "Payment Method", JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]);
                    if (method != null) {
                        fr.recordPayment(amt, method);
                        documentPreviewArea.setText(fr.toFormattedReceipt());
                        documentPreviewArea.setCaretPosition(0);
                        updateSelectedStudentBanner();
                        JOptionPane.showMessageDialog(this,
                                String.format("Payment of INR %,.2f recorded successfully!\nNew Balance Due: INR %,.2f", amt, fr.getBalanceDue()),
                                "Payment Recorded", JOptionPane.INFORMATION_MESSAGE);
                        logExecution("Fee Payment", "Recorded INR " + amt + " for " + s.getName());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        noDuesBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
            documentPreviewArea.setText(fr.toFormattedNoDuesCertificate());
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Financial No-Dues Clearance Certificate");
            logExecution("Fee Management", "Generated No-Dues certificate for " + s.getName());
        });

        attdNoticeBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            String notice = studentService.getRecordService().generateAttendanceShortageNotice(s);
            documentPreviewArea.setText(notice);
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Attendance Shortage Warning Notice (Attd: " + String.format("%.1f%%", s.getAttendance()) + ")");
            logExecution("Attendance Advisory", "Generated attendance shortage memo for " + s.getName());
        });

        tabulateBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            String dept = (s != null) ? s.getDepartment() : "ALL";
            String register = studentService.getRecordService().generateTabulationRegister(dept, null);
            documentPreviewArea.setText(register);
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: Departmental Grade Tabulation Register for " + dept);
            logExecution("Tabulation Register", "Generated class tabulation register for Department: " + dept);
        });

        gradeScaleBtn.addActionListener(e -> {
            documentPreviewArea.setText(studentService.getRecordService().getGradingScaleInfo());
            documentPreviewArea.setCaretPosition(0);
            previewStatusBanner.setText("Showing: UGC / AICTE Standard 10-Point Academic Grading Scale System");
        });

        printBtn.addActionListener(e -> {
            String text = documentPreviewArea.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please generate a document first to print.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                boolean complete = documentPreviewArea.print(
                        new MessageFormat("APEX INSTITUTE OF HIGHER TECHNOLOGY - ACADEMIC DOCUMENT"),
                        new MessageFormat("Page {0}"),
                        true,
                        null,
                        null,
                        true
                );
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Print job completed successfully!", "Print", JOptionPane.INFORMATION_MESSAGE);
                    logExecution("Document Printing", "Sent document to system printer/PDF spooler.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Printing cancelled or error: " + ex.getMessage(), "Print Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        saveBtn.addActionListener(e -> {
            String text = documentPreviewArea.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please generate a document first before exporting.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Student s = getCurrentlySelectedStudent();
            String defaultFileName = "document_" + (s != null ? s.getRollNumber() : "export") + "_" + System.currentTimeMillis() % 10000 + ".txt";
            JFileChooser chooser = new JFileChooser(new File("data"));
            chooser.setSelectedFile(new File(defaultFileName));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File target = chooser.getSelectedFile();
                boolean ok = studentService.getRecordService().saveDocumentToFile(text, target.getAbsolutePath());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Document successfully exported to:\n" + target.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                    logExecution("Document Export", "Exported document to " + target.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to export document.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        copyBtn.addActionListener(e -> {
            String text = documentPreviewArea.getText();
            if (text.isEmpty()) return;
            StringSelection sel = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
            JOptionPane.showMessageDialog(this, "Document copied to clipboard!", "Clipboard", JOptionPane.INFORMATION_MESSAGE);
        });

        queueBtn.addActionListener(e -> {
            Student s = getCurrentlySelectedStudent();
            if (s == null) return;
            CertificateType type = (CertificateType) certTypeCombo.getSelectedItem();
            studentService.submitServiceRequest(s.getStudentId(), type.getDisplayName());
            refreshDashboardMetrics();
            JOptionPane.showMessageDialog(this,
                    String.format("Service Request submitted to FIFO Queue for Student %s (ID %d)!\nQueue Size: %d",
                            s.getName(), s.getStudentId(), studentService.getServiceQueue().getSize()),
                    "Request Enqueued", JOptionPane.INFORMATION_MESSAGE);
            logExecution("Service Queue", "Enqueued " + type.getDisplayName() + " request for " + s.getName());
        });

        // Trigger initial preview for first student if available
        if (certStudentCombo.getItemCount() > 0) {
            updateSelectedStudentBanner();
            marksheetBtn.doClick();
        }

        return panel;
    }

    private void populateStudentCombo() {
        if (certStudentCombo == null) return;
        certStudentCombo.removeAllItems();
        for (Student s : studentService.getAllStudents()) {
            certStudentCombo.addItem(new StudentComboItem(s));
        }
    }

    private Student getCurrentlySelectedStudent() {
        if (certStudentCombo == null || certStudentCombo.getSelectedItem() == null) {
            if (!studentService.getAllStudents().isEmpty()) return studentService.getAllStudents().get(0);
            return null;
        }
        return ((StudentComboItem) certStudentCombo.getSelectedItem()).student;
    }

    public void selectStudentInRecordsTab(long studentId) {
        if (certStudentCombo == null) return;
        for (int i = 0; i < certStudentCombo.getItemCount(); i++) {
            StudentComboItem item = certStudentCombo.getItemAt(i);
            if (item.student.getStudentId() == studentId) {
                certStudentCombo.setSelectedIndex(i);
                updateSelectedStudentBanner();
                tabbedPane.setSelectedIndex(6);
                return;
            }
        }
        tabbedPane.setSelectedIndex(6);
    }

    private void updateSelectedStudentBanner() {
        Student s = getCurrentlySelectedStudent();
        if (s == null || studentDetailsBanner == null) return;
        FeeRecord fr = studentService.getRecordService().getFeeRecord(s);
        studentDetailsBanner.setText(String.format("Student: %s (%s) | %s - %s | Yr: %d, Sem: %d | CGPA: %.2f (%s) | Attd: %.1f%% | Fee Status: %s (Bal: INR %,.2f)",
                s.getName(), s.getRollNumber(), s.getCourse(), s.getDepartment(),
                s.getYear(), s.getSemester(), s.getCgpa(), s.getGrade(), s.getAttendance(),
                fr != null ? fr.getPaymentStatus() : "N/A",
                fr != null ? fr.getBalanceDue() : 0.0));
    }

    private void logExecution(String algo, String details) {
        if (executionLogArea != null) {
            executionLogArea.append(String.format("[%s] Algorithm: %s | %s\n",
                    java.time.LocalTime.now().toString().substring(0, 8), algo, details));
        }
    }

    // =========================================================================
    // 7. CUSTOM 2D GRAPHICS CHART COMPONENTS
    // =========================================================================

    /**
     * Custom Swing component rendering CGPA grade distribution bars.
     */
    public static class CgpaHistogramPanel extends JPanel {
        private final int[] counts = new int[5]; // <6.0, 6.0-6.99, 7.0-7.99, 8.0-8.99, 9.0-10.0
        private final String[] labels = {"< 6.0 (Risk)", "6.0 - 6.99", "7.0 - 7.99", "8.0 - 8.99", "9.0 - 10.0 (Top)"};
        private final Color[] colors = {
                new Color(231, 76, 60),  // Red
                new Color(243, 156, 18), // Orange
                new Color(26, 188, 156), // Teal
                new Color(41, 128, 185), // Blue
                new Color(39, 174, 96)   // Green
        };

        public CgpaHistogramPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                    BorderFactory.createTitledBorder("CGPA Academic Distribution Histogram")
            ));
        }

        public void updateData(List<Student> students) {
            for (int i = 0; i < 5; i++) counts[i] = 0;
            for (Student s : students) {
                double g = s.getCgpa();
                if (g < 6.0) counts[0]++;
                else if (g < 7.0) counts[1]++;
                else if (g < 8.0) counts[2]++;
                else if (g < 9.0) counts[3]++;
                else counts[4]++;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int bottomPadding = 45;

            int maxCount = 1;
            for (int c : counts) if (c > maxCount) maxCount = c;

            int barWidth = (width - 2 * padding) / 5 - 16;
            int chartHeight = height - padding - bottomPadding;

            for (int i = 0; i < 5; i++) {
                int x = padding + i * (barWidth + 16) + 8;
                int barH = (int) (((double) counts[i] / maxCount) * chartHeight);
                int y = height - bottomPadding - barH;

                // Draw Bar
                g2.setColor(colors[i]);
                g2.fillRoundRect(x, y, barWidth, barH, 8, 8);

                // Draw Count on top
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String countStr = String.valueOf(counts[i]);
                int strW = g2.getFontMetrics().stringWidth(countStr);
                g2.drawString(countStr, x + (barWidth - strW) / 2, y - 5);

                // Draw Label below
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String lbl = labels[i];
                int lblW = g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl, x + (barWidth - lblW) / 2, height - bottomPadding + 16);
            }
            g2.dispose();
        }
    }

    /**
     * Custom Swing component rendering Department Enrollment Distribution bars.
     */
    public static class DeptDistributionPanel extends JPanel {
        private final Map<String, Integer> deptCounts = new java.util.LinkedHashMap<>();
        private final Color[] barColors = {
                new Color(41, 128, 185), new Color(142, 68, 173), new Color(39, 174, 96),
                new Color(243, 156, 18), new Color(211, 84, 0), new Color(44, 62, 80),
                new Color(22, 160, 133), new Color(127, 140, 141)
        };

        public DeptDistributionPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                    BorderFactory.createTitledBorder("Department Enrollment Distribution")
            ));
        }

        public void updateData(List<Student> students) {
            deptCounts.clear();
            for (Student s : students) {
                String d = s.getDepartment();
                deptCounts.put(d, deptCounts.getOrDefault(d, 0) + 1);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int top = 35;
            int left = 75;
            int right = 45;

            if (deptCounts.isEmpty()) {
                g2.dispose();
                return;
            }

            int max = 1;
            for (int v : deptCounts.values()) if (v > max) max = v;

            int rowHeight = (height - top - 15) / Math.max(1, deptCounts.size());
            int idx = 0;

            for (Map.Entry<String, Integer> e : deptCounts.entrySet()) {
                int y = top + idx * rowHeight;
                int barMaxW = width - left - right;
                int barW = (int) (((double) e.getValue() / max) * barMaxW);

                // Draw Dept label
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(e.getKey(), 15, y + rowHeight / 2 + 4);

                // Draw Horizontal Bar
                g2.setColor(barColors[idx % barColors.length]);
                g2.fillRoundRect(left, y + 4, Math.max(8, barW), Math.max(12, rowHeight - 8), 6, 6);

                // Draw Count
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(e.getValue() + " sts", left + barW + 8, y + rowHeight / 2 + 4);

                idx++;
            }
            g2.dispose();
        }
    }

    /**
     * Custom Swing component rendering Statutory Attendance Compliance bars.
     */
    public static class AttendanceCompliancePanel extends JPanel {
        private final int[] counts = new int[4]; // <65% (Detained), 65-74.9% (Condonation), 75-84.9% (Eligible), 85-100% (High)
        private final String[] labels = {"<65% Detained", "65-74.9% Zone", "75-84.9% Pass", "85-100% Top"};
        private final Color[] colors = {
                new Color(231, 76, 60),  // Red
                new Color(243, 156, 18), // Orange
                new Color(26, 188, 156), // Teal
                new Color(39, 174, 96)   // Green
        };

        public AttendanceCompliancePanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                    BorderFactory.createTitledBorder("Attendance & Exam Eligibility Audit")
            ));
        }

        public void updateData(List<Student> students) {
            for (int i = 0; i < 4; i++) counts[i] = 0;
            for (Student s : students) {
                double a = s.getAttendance();
                if (a < 65.0) counts[0]++;
                else if (a < 75.0) counts[1]++;
                else if (a < 85.0) counts[2]++;
                else counts[3]++;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 30;
            int bottomPadding = 45;

            int maxCount = 1;
            for (int c : counts) if (c > maxCount) maxCount = c;

            int barWidth = Math.max(12, (width - 2 * padding) / 4 - 14);
            int chartHeight = height - padding - bottomPadding;

            for (int i = 0; i < 4; i++) {
                int x = padding + i * (barWidth + 14) + 6;
                int barH = (int) (((double) counts[i] / maxCount) * chartHeight);
                int y = height - bottomPadding - barH;

                // Draw Bar
                g2.setColor(colors[i]);
                g2.fillRoundRect(x, y, barWidth, barH, 8, 8);

                // Draw Count on top
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String countStr = String.valueOf(counts[i]);
                int strW = g2.getFontMetrics().stringWidth(countStr);
                g2.drawString(countStr, x + (barWidth - strW) / 2, y - 5);

                // Draw Label below
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String lbl = labels[i];
                int lblW = g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl, x + (barWidth - lblW) / 2, height - bottomPadding + 16);
            }
            g2.dispose();
        }
    }
}
