package services;

import model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Central Service for Comprehensive Student Record Management:
 * 1. Academic Grade Cards, Marksheets & SGPA/CGPA evaluation.
 * 2. Institutional Certificate Generation & Printing (Bonafide, Merit, Degree, Transcript, TC, etc.).
 * 3. Student Identity Card (ID Card) formatting and printing.
 * 4. Academic Fee Ledger, Payment Processing & Receipt Generation.
 * 5. Class-wide Grade Tabulation Register and Academic Standing Reports.
 * 6. File Export & Document Printing utilities.
 */
public class StudentRecordManagementService {

    private final StudentService studentService;
    private final Map<Long, GradeCard> gradeCardCache;
    private final Map<Long, FeeRecord> feeRecordCache;
    private final List<Certificate> issuedCertificates;

    public StudentRecordManagementService(StudentService studentService) {
        this.studentService = studentService;
        this.gradeCardCache = new HashMap<>();
        this.feeRecordCache = new HashMap<>();
        this.issuedCertificates = new ArrayList<>();
    }

    // =========================================================================
    // 1. GRADE & MARKSHEET MANAGEMENT
    // =========================================================================

    /**
     * Generates or retrieves the official Academic Grade Card / Marksheet for a student.
     */
    public synchronized GradeCard generateGradeCard(Student student) {
        if (student == null) return null;
        if (gradeCardCache.containsKey(student.getStudentId())) {
            return gradeCardCache.get(student.getStudentId());
        }

        List<SubjectGrade> subjects = buildDepartmentalCurriculum(student);
        GradeCard gradeCard = new GradeCard(student, subjects);
        gradeCardCache.put(student.getStudentId(), gradeCard);
        return gradeCard;
    }

    public synchronized GradeCard getGradeCard(long studentId) {
        if (gradeCardCache.containsKey(studentId)) {
            return gradeCardCache.get(studentId);
        }
        Student s = studentService.getStudentById(studentId);
        if (s != null) {
            return generateGradeCard(s);
        }
        return null;
    }

    /**
     * Constructs realistic curriculum subject grades matching the student's department,
     * semester, and academic score profile.
     */
    private List<SubjectGrade> buildDepartmentalCurriculum(Student s) {
        List<SubjectGrade> list = new ArrayList<>();
        String dept = (s.getDepartment() != null) ? s.getDepartment().toUpperCase() : "CSE";
        double baseMarks = s.getMarks(); // out of 100

        record RawCourse(String code, String name, int credits, double variance) {}
        List<RawCourse> courses = new ArrayList<>();

        switch (dept) {
            case "CSE" -> {
                courses.add(new RawCourse("CS301", "Design & Analysis of Algorithms", 4, 2.0));
                courses.add(new RawCourse("CS302", "Database Management Systems", 4, -1.5));
                courses.add(new RawCourse("CS303", "Operating Systems Architecture", 4, 1.0));
                courses.add(new RawCourse("CS304", "Computer Communication Networks", 3, -2.0));
                courses.add(new RawCourse("CS305", "Theory of Formal Languages & Automata", 3, -3.0));
                courses.add(new RawCourse("CS306", "Full-Stack Web Engineering Lab", 2, 4.0));
            }
            case "AIML" -> {
                courses.add(new RawCourse("AI201", "Machine Learning Foundations & Math", 4, 1.5));
                courses.add(new RawCourse("AI202", "Deep Learning & Neural Networks", 4, 2.0));
                courses.add(new RawCourse("AI203", "Natural Language Processing Systems", 3, -1.0));
                courses.add(new RawCourse("AI204", "Computer Vision & Visual Computing", 4, -2.5));
                courses.add(new RawCourse("AI205", "Ethics & Governance in AI", 2, 3.5));
                courses.add(new RawCourse("AI206", "Intelligent Systems Development Lab", 2, 4.0));
            }
            case "IT" -> {
                courses.add(new RawCourse("IT401", "Cloud Native Computing & DevOps", 4, 2.0));
                courses.add(new RawCourse("IT402", "Distributed Systems & Microservices", 4, -1.0));
                courses.add(new RawCourse("IT403", "Information Security & Cryptography", 3, -2.0));
                courses.add(new RawCourse("IT404", "Enterprise Application Development", 4, 1.5));
                courses.add(new RawCourse("IT405", "Cloud Architecture & Containers Lab", 2, 3.5));
            }
            case "ECE" -> {
                courses.add(new RawCourse("EC301", "Embedded Systems & IoT Hardware", 4, 1.0));
                courses.add(new RawCourse("EC302", "Digital Signal Processing Architecture", 4, -2.5));
                courses.add(new RawCourse("EC303", "VLSI Design & System on Chip", 4, -1.5));
                courses.add(new RawCourse("EC304", "Wireless Communications & RF Design", 3, 0.5));
                courses.add(new RawCourse("EC305", "Microcontrollers & IoT Security Lab", 2, 3.0));
            }
            case "DS" -> {
                courses.add(new RawCourse("DS201", "Big Data Analytics & Hadoop Systems", 4, 1.5));
                courses.add(new RawCourse("DS202", "Statistical Inference & Probability", 4, -1.5));
                courses.add(new RawCourse("DS203", "Data Mining & Predictive Modeling", 3, 0.5));
                courses.add(new RawCourse("DS204", "Applied Machine Learning for Data", 4, 2.0));
                courses.add(new RawCourse("DS205", "Advanced Data Science Laboratory", 2, 4.0));
            }
            case "MECH" -> {
                courses.add(new RawCourse("ME301", "Thermodynamics & Heat Transfer", 4, -1.5));
                courses.add(new RawCourse("ME302", "Fluid Mechanics & Turbo Machines", 4, -2.0));
                courses.add(new RawCourse("ME303", "Design of Machine Elements", 4, 1.0));
                courses.add(new RawCourse("ME304", "Robotics & Industrial Automation", 3, 2.0));
                courses.add(new RawCourse("ME305", "CAD/CAM & Simulation Laboratory", 2, 3.5));
            }
            case "CIVIL" -> {
                courses.add(new RawCourse("CE301", "Structural Analysis & Design", 4, -1.0));
                courses.add(new RawCourse("CE302", "Geotechnical & Soil Engineering", 4, -2.0));
                courses.add(new RawCourse("CE303", "Concrete Technology & Construction", 4, 1.5));
                courses.add(new RawCourse("CE304", "Transportation & Highway Engineering", 3, 0.5));
                courses.add(new RawCourse("CE305", "Surveying & CAD Mapping Lab", 2, 3.0));
            }
            default -> {
                courses.add(new RawCourse("GE101", "Engineering Mathematics & Statistics", 4, 0.0));
                courses.add(new RawCourse("GE102", "Computational Problem Solving", 4, 1.0));
                courses.add(new RawCourse("GE103", "Digital Logic & Systems", 3, -1.0));
                courses.add(new RawCourse("GE104", "Professional Technical Communication", 2, 3.0));
            }
        }

        // Calculate marks with realistic variation per subject
        for (RawCourse rc : courses) {
            double subjectTotal = Math.min(100.0, Math.max(35.0, baseMarks + rc.variance));
            // 30% Internal (Max 30), 70% External (Max 70)
            double internal = Math.min(30.0, Math.round((subjectTotal * 0.3) * 10.0) / 10.0);
            double external = Math.min(70.0, Math.round((subjectTotal * 0.7) * 10.0) / 10.0);
            list.add(new SubjectGrade(rc.code, rc.name, rc.credits, internal, external));
        }

        return list;
    }

    /**
     * Generates a class-wide Grade Tabulation Register for a department.
     */
    public String generateTabulationRegister(String department, Integer semester) {
        List<Student> students = studentService.filterStudents(
                (department != null && !department.equalsIgnoreCase("ALL")) ? department : null,
                null, null, null, null, null);

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          \n");
        sb.append(String.format("                OFFICIAL TABULATION REGISTER - DEPARTMENT OF %-20s         \n",
                department != null ? department.toUpperCase() : "ALL"));
        sb.append("====================================================================================================\n");
        sb.append(String.format(" %-5s | %-12s | %-22s | %-5s | %-4s | %-6s | %-5s | %-5s | %-22s\n",
                "SL.NO", "ROLL NO", "STUDENT NAME", "DEPT", "SEM", "MARKS%", "CGPA", "GRADE", "RESULT STATUS"));
        sb.append("----------------------------------------------------------------------------------------------------\n");

        int idx = 1;
        for (Student s : students) {
            if (semester != null && semester > 0 && s.getSemester() != semester) continue;
            GradeCard gc = generateGradeCard(s);
            sb.append(String.format(" %-5d | %-12s | %-22s | %-5s |  %2d  | %5.1f%% | %5.2f |  %-2s   | %-22s\n",
                    idx++, s.getRollNumber(),
                    s.getName().length() > 22 ? s.getName().substring(0, 22) : s.getName(),
                    s.getDepartment(), s.getSemester(), s.getMarks(), s.getCgpa(), s.getGrade(),
                    gc != null ? gc.getResultStatus() : "PASSED"));
        }

        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" TOTAL STUDENTS TABULATED: %d | Generated on: %s\n", (idx - 1), new Date()));
        sb.append("====================================================================================================\n");
        return sb.toString();
    }

    /**
     * Explains the official academic grading scale.
     */
    public String getGradingScaleInfo() {
        return """
        +---------------------------------------------------------------------------------------+
        |                  OFFICIAL 10-POINT ACADEMIC GRADING SYSTEM (UGC / AICTE)               |
        +------------------+---------------+-------------+--------------------------------------+
        | Percentage Marks | Letter Grade  | Grade Point | Academic Performance Level           |
        +------------------+---------------+-------------+--------------------------------------+
        | 90% and above    | O             | 10          | Outstanding                          |
        | 80% to 89.99%    | A+            | 9           | Excellent                            |
        | 70% to 79.99%    | A             | 8           | Very Good                            |
        | 60% to 69.99%    | B+            | 7           | Good                                 |
        | 55% to 59.99%    | B             | 6           | Above Average                        |
        | 50% to 54.99%    | C             | 5           | Average                              |
        | 40% to 49.99%    | P             | 4           | Pass                                 |
        | Below 40%        | F             | 0           | Fail (Must Re-appear)                |
        +------------------+---------------+-------------+--------------------------------------+
        • Semester Grade Point Average (SGPA) = Sum(Credits * Grade Point) / Sum(Credits)
        • Cumulative Grade Point Average (CGPA) = Weighted average of SGPAs across all semesters
        • Classification: CGPA >= 8.5 -> Distinction | CGPA >= 6.5 -> First Class | CGPA >= 5.0 -> Second Class
        """;
    }

    // =========================================================================
    // 2. CERTIFICATE GENERATION & PRINTING
    // =========================================================================

    /**
     * Generates an official Certificate with security verification hash and archives it.
     */
    public synchronized Certificate generateCertificate(Student student, CertificateType type, String purpose) {
        if (student == null || type == null) return null;
        Certificate cert = new Certificate(student, type, purpose);
        issuedCertificates.add(cert);
        return cert;
    }

    public List<Certificate> getIssuedCertificates() {
        return Collections.unmodifiableList(issuedCertificates);
    }

    public List<Certificate> getCertificatesForStudent(long studentId) {
        List<Certificate> list = new ArrayList<>();
        for (Certificate c : issuedCertificates) {
            if (c.getStudentId() == studentId) {
                list.add(c);
            }
        }
        return list;
    }

    // =========================================================================
    // 3. STUDENT IDENTITY CARD GENERATION
    // =========================================================================

    public StudentIdCard generateStudentIdCard(Student student) {
        if (student == null) return null;
        return new StudentIdCard(student);
    }

    // =========================================================================
    // 4. ACADEMIC FEE & DUES RECORD MANAGEMENT
    // =========================================================================

    public synchronized FeeRecord getFeeRecord(Student student) {
        if (student == null) return null;
        if (!feeRecordCache.containsKey(student.getStudentId())) {
            FeeRecord fr = new FeeRecord(student);
            feeRecordCache.put(student.getStudentId(), fr);
        }
        return feeRecordCache.get(student.getStudentId());
    }

    public synchronized FeeRecord recordFeePayment(long studentId, double amount, String method) {
        Student s = studentService.getStudentById(studentId);
        if (s == null) return null;
        FeeRecord fr = getFeeRecord(s);
        fr.recordPayment(amount, method);
        return fr;
    }

    public String generateFeeReceipt(Student student) {
        FeeRecord fr = getFeeRecord(student);
        return (fr != null) ? fr.toFormattedReceipt() : "Fee record not found.";
    }

    public String generateNoDuesCertificate(Student student) {
        FeeRecord fr = getFeeRecord(student);
        return (fr != null) ? fr.toFormattedNoDuesCertificate() : "Fee record not found.";
    }

    // =========================================================================
    // 5. ATTENDANCE SHORTAGE ADVISORY
    // =========================================================================

    public String generateAttendanceShortageNotice(Student student) {
        if (student == null) return "Student not found.";
        Certificate cert = new Certificate(student, CertificateType.ATTENDANCE_WARNING, "Statutory minimum attendance deficit");
        return cert.getFormattedDocument();
    }

    // =========================================================================
    // 7. SEPARATE CGPA ANALYSIS & REPORTS
    // =========================================================================

    /**
     * Generates a dedicated separate CGPA Record for a student with departmental ranking.
     */
    public CgpaRecord generateCgpaRecord(Student student) {
        if (student == null) return null;
        List<Student> deptStudents = new ArrayList<>(studentService.filterStudents(student.getDepartment(), null, null, null, null, null));
        deptStudents.sort((a, b) -> Double.compare(b.getCgpa(), a.getCgpa()));

        int rank = 1;
        for (int i = 0; i < deptStudents.size(); i++) {
            if (deptStudents.get(i).getStudentId() == student.getStudentId()) {
                rank = i + 1;
                break;
            }
        }
        return new CgpaRecord(student, rank, deptStudents.size());
    }

    /**
     * Generates a dedicated, standalone departmental or college-wide CGPA Performance Ledger.
     */
    public String generateSeparateCgpaReport(String department, Double minCgpa) {
        List<Student> students = new ArrayList<>(studentService.filterStudents(
                (department != null && !department.equalsIgnoreCase("ALL")) ? department : null,
                null, null, minCgpa, null, null));
        students.sort((a, b) -> Double.compare(b.getCgpa(), a.getCgpa()));

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          \n");
        sb.append(String.format("                   SEPARATE CGPA ACADEMIC PERFORMANCE LEDGER - [%s]               \n",
                (department != null && !department.equalsIgnoreCase("ALL")) ? "DEPT: " + department.toUpperCase() : "COLLEGE-WIDE"));
        sb.append("====================================================================================================\n");

        double totalCgpa = 0.0, maxCgpa = 0.0, minCgpaVal = 10.0;
        int countDistinction = 0, countFirst = 0, countSecond = 0, countAtRisk = 0;

        for (Student s : students) {
            double g = s.getCgpa();
            totalCgpa += g;
            if (g > maxCgpa) maxCgpa = g;
            if (g < minCgpaVal) minCgpaVal = g;
            if (g >= 8.5) countDistinction++;
            else if (g >= 6.5) countFirst++;
            else if (g >= 5.0) countSecond++;
            else countAtRisk++;
        }
        double avgCgpa = students.isEmpty() ? 0.0 : Math.round((totalCgpa / students.size()) * 100.0) / 100.0;

        sb.append(String.format(" Students Evaluated: %-4d | Average CGPA: %-4.2f | Highest CGPA: %-4.2f | Lowest CGPA: %-4.2f\n",
                students.size(), avgCgpa, maxCgpa, students.isEmpty() ? 0.0 : minCgpaVal));
        sb.append(String.format(" Distinction (>=8.5): %-3d | First Class (>=6.5): %-3d | Second Class: %-3d | At-Risk (<5.0): %-3d\n",
                countDistinction, countFirst, countSecond, countAtRisk));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" %-5s | %-12s | %-12s | %-22s | %-5s | %-4s | %-6s | %-5s | %-4s | %-20s\n",
                "RANK", "STUDENT ID", "ROLL NO", "STUDENT NAME", "DEPT", "SEM", "MARKS%", "CGPA", "GRD", "ACADEMIC STANDING"));
        sb.append("----------------------------------------------------------------------------------------------------\n");

        int rank = 1;
        for (Student s : students) {
            String standing = s.getCgpa() >= 9.0 ? "Dean's Honor List" :
                    (s.getCgpa() >= 8.5 ? "Distinction" :
                            (s.getCgpa() >= 6.5 ? "First Class" :
                                    (s.getCgpa() >= 5.0 ? "Second Class" : "At-Risk / Remedial")));

            sb.append(String.format(" #%-4d | %-12d | %-12s | %-22s | %-5s |  %2d  | %5.1f%% | %5.2f |  %-2s  | %-20s\n",
                    rank++, s.getStudentId(), s.getRollNumber(),
                    s.getName().length() > 22 ? s.getName().substring(0, 22) : s.getName(),
                    s.getDepartment(), s.getSemester(), s.getMarks(), s.getCgpa(), s.getGrade(), standing));
        }

        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(" Classification Guidelines: CGPA >= 8.50: First Class Distinction | CGPA >= 6.50: First Class | CGPA < 5.00: At-Risk\n");
        sb.append("====================================================================================================\n");
        return sb.toString();
    }

    // =========================================================================
    // 8. SEPARATE ATTENDANCE TRACKING & AUDIT
    // =========================================================================

    /**
     * Generates a dedicated separate Attendance Record for a student.
     */
    public AttendanceRecord generateAttendanceRecord(Student student) {
        if (student == null) return null;
        return new AttendanceRecord(student);
    }

    /**
     * Generates a dedicated, standalone departmental or college-wide Attendance Register and Exam Eligibility Audit.
     */
    public String generateSeparateAttendanceReport(String department, String statusFilter) {
        List<Student> students = new ArrayList<>(studentService.filterStudents(
                (department != null && !department.equalsIgnoreCase("ALL")) ? department : null,
                null, null, null, null, null));

        List<AttendanceRecord> records = new ArrayList<>();
        int eligibleCount = 0, condonationCount = 0, detainedCount = 0;
        double totalAttd = 0.0;

        for (Student s : students) {
            AttendanceRecord ar = new AttendanceRecord(s);
            totalAttd += ar.getAttendancePercentage();

            if (ar.getStatus() == AttendanceRecord.Status.ELIGIBLE) eligibleCount++;
            else if (ar.getStatus() == AttendanceRecord.Status.CONDONATION_ZONE) condonationCount++;
            else detainedCount++;

            if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
                if (statusFilter.equalsIgnoreCase("ELIGIBLE") && ar.getStatus() != AttendanceRecord.Status.ELIGIBLE) continue;
                if (statusFilter.equalsIgnoreCase("CONDONATION") && ar.getStatus() != AttendanceRecord.Status.CONDONATION_ZONE) continue;
                if (statusFilter.equalsIgnoreCase("DETAINED") && ar.getStatus() != AttendanceRecord.Status.DETAINED) continue;
                if (statusFilter.equalsIgnoreCase("SHORTAGE") && ar.getStatus() == AttendanceRecord.Status.ELIGIBLE) continue;
            }
            records.add(ar);
        }

        // Sort by attendance percentage ascending so shortages appear prominently
        records.sort((a, b) -> Double.compare(a.getAttendancePercentage(), b.getAttendancePercentage()));

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          \n");
        sb.append(String.format("             SEPARATE STUDENT ATTENDANCE REGISTER & EXAM ELIGIBILITY AUDIT - [%s]   \n",
                (department != null && !department.equalsIgnoreCase("ALL")) ? "DEPT: " + department.toUpperCase() : "COLLEGE-WIDE"));
        sb.append("====================================================================================================\n");

        double avgAttd = students.isEmpty() ? 0.0 : Math.round((totalAttd / students.size()) * 10.0) / 10.0;
        sb.append(String.format(" Enrolled Students : %-4d | Average Attendance: %-5.1f%% | Statutory Criteria: 75.00%%\n",
                students.size(), avgAttd));
        sb.append(String.format(" Eligible (>=75%%)  : %-4d | Condonation Zone (65-74.9%%): %-3d | Detained (<65%%): %-3d\n",
                eligibleCount, condonationCount, detainedCount));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" %-5s | %-12s | %-20s | %-5s | %-3s | %-4s | %-4s | %-4s | %-6s | %-20s | %-10s\n",
                "SL.NO", "ROLL NO", "STUDENT NAME", "DEPT", "SEC", "HELD", "PRES", "ABST", "ATTD%", "EXAM ELIGIBILITY", "TO REACH 75%"));
        sb.append("----------------------------------------------------------------------------------------------------\n");

        int sl = 1;
        for (AttendanceRecord ar : records) {
            String target = ar.getStatus() == AttendanceRecord.Status.ELIGIBLE ? "CLEARED" : ("+" + ar.getClassesNeededFor75() + " classes");
            sb.append(String.format(" %-5d | %-12s | %-20s | %-5s |  %s  | %4d | %4d | %4d | %5.1f%% | %-20s | %-10s\n",
                    sl++, ar.getRollNumber(),
                    ar.getStudentName().length() > 20 ? ar.getStudentName().substring(0, 20) : ar.getStudentName(),
                    ar.getDepartment(), ar.getSection(),
                    ar.getTotalClassesHeld(), ar.getClassesAttended(), ar.getClassesMissed(),
                    ar.getAttendancePercentage(), ar.getStatus().getTitle(), target));
        }

        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(" Regulations: Attendance >= 75%: Unconditional Hall Ticket | 65% - 74.9%: Dean Condonation | < 65%: Semester Detention\n");
        sb.append("====================================================================================================\n");
        return sb.toString();
    }

    // =========================================================================
    // 9. FILE EXPORT & DOCUMENT PRINTING
    // =========================================================================

    /**
     * Saves any generated document (Grade Card, Certificate, Fee Receipt, ID Card, CGPA Card, Attendance Report) to file.
     */
    public boolean saveDocumentToFile(String content, String relativePath) {
        try {
            File file = new File(relativePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            Files.writeString(Paths.get(file.getAbsolutePath()), content, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving document: " + e.getMessage());
            return false;
        }
    }
}
