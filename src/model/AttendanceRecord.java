package model;

import java.io.Serializable;

/**
 * Dedicated Model for Separate Attendance Tracking, Statutory Audit,
 * Exam Eligibility Evaluation, and Defaulter Recovery calculations.
 */
public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        ELIGIBLE("EXAMINATION ELIGIBLE", "Attendance meets or exceeds statutory 75% criteria. Approved for Hall Ticket."),
        CONDONATION_ZONE("CONDONATION REQUIRED", "Attendance between 65% - 74.99%. Medical/Dean Condonation required to sit exams."),
        DETAINED("SHORTAGE - DETAINED", "Attendance below 65%. Barred from End-Semester Exams as per regulatory statutes.");

        private final String title;
        private final String description;

        Status(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    private long studentId;
    private String rollNumber;
    private String studentName;
    private String department;
    private String course;
    private int year;
    private int semester;
    private String section;

    private int totalWorkingDays;
    private int totalClassesHeld;
    private int classesAttended;
    private int classesMissed;
    private double attendancePercentage;
    private Status status;
    private int classesNeededFor75;
    private double maxPossibleAttendance;

    public AttendanceRecord() {}

    public AttendanceRecord(Student student) {
        this.studentId = student.getStudentId();
        this.rollNumber = student.getRollNumber();
        this.studentName = student.getName();
        this.department = student.getDepartment();
        this.course = student.getCourse();
        this.year = student.getYear();
        this.semester = student.getSemester();
        this.section = student.getSection();

        this.totalWorkingDays = 90;
        this.totalClassesHeld = 180; // Standard 180 instruction sessions per semester
        this.attendancePercentage = Math.round(student.getAttendance() * 10.0) / 10.0;

        this.classesAttended = (int) Math.round(this.totalClassesHeld * (this.attendancePercentage / 100.0));
        this.classesMissed = Math.max(0, this.totalClassesHeld - this.classesAttended);

        if (this.attendancePercentage >= 75.0) {
            this.status = Status.ELIGIBLE;
            this.classesNeededFor75 = 0;
        } else if (this.attendancePercentage >= 65.0) {
            this.status = Status.CONDONATION_ZONE;
            // Solve: (attended + x) / (held + x) >= 0.75 => x >= (0.75 * held - attended) / 0.25
            double needed = (0.75 * this.totalClassesHeld - this.classesAttended) / 0.25;
            this.classesNeededFor75 = Math.max(1, (int) Math.ceil(needed));
        } else {
            this.status = Status.DETAINED;
            double needed = (0.75 * this.totalClassesHeld - this.classesAttended) / 0.25;
            this.classesNeededFor75 = Math.max(1, (int) Math.ceil(needed));
        }

        // Assuming 30 additional lectures remaining in current term
        int remaining = 30;
        this.maxPossibleAttendance = Math.round(((double) (this.classesAttended + remaining) / (this.totalClassesHeld + remaining)) * 1000.0) / 10.0;
    }

    public long getStudentId() { return studentId; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public String getCourse() { return course; }
    public int getYear() { return year; }
    public int getSemester() { return semester; }
    public String getSection() { return section; }
    public int getTotalWorkingDays() { return totalWorkingDays; }
    public int getTotalClassesHeld() { return totalClassesHeld; }
    public int getClassesAttended() { return classesAttended; }
    public int getClassesMissed() { return classesMissed; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public Status getStatus() { return status; }
    public int getClassesNeededFor75() { return classesNeededFor75; }
    public double getMaxPossibleAttendance() { return maxPossibleAttendance; }

    /**
     * Formats this record as a dedicated, standalone Attendance Performance Card.
     */
    public String toFormattedAttendanceCard() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          \n");
        sb.append("                               OFFICE OF DEAN (ACADEMIC ATTENDANCE AUDIT)                          \n");
        sb.append("                               DEDICATED STUDENT ATTENDANCE REPORT CARD                            \n");
        sb.append("====================================================================================================\n");
        sb.append(String.format(" Student Name     : %-32s | Roll Number  : %-18s\n", studentName, rollNumber));
        sb.append(String.format(" Student ID       : %-32d | Department   : %-18s\n", studentId, department));
        sb.append(String.format(" Program / Course : %-32s | Year / Sem   : Year %d, Sem %-10d\n", course, year, semester));
        sb.append(String.format(" Section / Group  : Section %-24s | Working Days : %-3d Days (180 Periods)\n", section, totalWorkingDays));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" RECORDED ATTENDANCE PERCENTAGE : %5.1f%%                  | STATUTORY REQUIREMENT : 75.00%%\n", attendancePercentage));

        int barLength = (int) Math.round((attendancePercentage / 100.0) * 35);
        String bar = "█".repeat(Math.max(0, barLength));
        sb.append(String.format(" ATTENDANCE VISUAL PROGRESS     : [%-35s] %5.1f%%\n", bar, attendancePercentage));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" TOTAL CLASSES CONDUCTED        : %-4d Instruction Periods\n", totalClassesHeld));
        sb.append(String.format(" TOTAL CLASSES PRESENT          : %-4d Periods Attended\n", classesAttended));
        sb.append(String.format(" TOTAL CLASSES ABSENT / MISSED  : %-4d Periods Missed\n", classesMissed));
        sb.append(String.format(" STATUTORY REGULATORY STATUS    : %s\n", status.getTitle()));
        sb.append(String.format(" REGULATORY DIRECTIVE           : %s\n", status.getDescription()));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(" DEFICIT & RECOVERY AUDIT:\n");
        if (status == Status.ELIGIBLE) {
            sb.append(" • Current Status: SATISFACTORY & COMPLIANT. No attendance deficit recorded.\n");
            sb.append(" • Student is fully eligible to receive Semester Hall Ticket without condonation.\n");
        } else {
            sb.append(String.format(" • ATTENDANCE DEFICIT : Deficit of %.1f%% below the required 75.00%% statutory limit.\n", (75.0 - attendancePercentage)));
            sb.append(String.format(" • RECOVERY TARGET   : Must attend %d consecutive upcoming classes without missing any to reach 75%%.\n", classesNeededFor75));
            sb.append(String.format(" • PROJECTED CEILING : Maximum achievable attendance if 100%% upcoming classes attended: %.1f%%.\n", maxPossibleAttendance));
            if (status == Status.CONDONATION_ZONE) {
                sb.append(" • ACTION REQUIRED   : Submit formal medical/official representation to Head of Department for Condonation.\n");
            } else {
                sb.append(" • ACTION REQUIRED   : Immediate meeting with Proctor and Academic Dean mandatory. Risk of semester detention.\n");
            }
        }
        sb.append("====================================================================================================\n");
        sb.append("      [Verified by: Attendance Officer]        [Head of Department]        [Dean of Student Welfare]       \n");
        sb.append("====================================================================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[Attendance Record: %s (%s) | Attd: %.1f%% | Status: %s | Present: %d/%d]",
                studentName, rollNumber, attendancePercentage, status.getTitle(), classesAttended, totalClassesHeld);
    }
}
