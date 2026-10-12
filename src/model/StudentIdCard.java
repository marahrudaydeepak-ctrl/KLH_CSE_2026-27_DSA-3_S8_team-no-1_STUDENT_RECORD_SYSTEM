package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model representing a printable Institutional Student Identity Card.
 */
public class StudentIdCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private long studentId;
    private String rollNumber;
    private String studentName;
    private String department;
    private String course;
    private int year;
    private String section;
    private String bloodGroup;
    private String emergencyPhone;
    private String validUntil;
    private String formattedCard;

    public StudentIdCard(Student s) {
        this.studentId = s.getStudentId();
        this.rollNumber = s.getRollNumber();
        this.studentName = s.getName();
        this.department = s.getDepartment();
        this.course = s.getCourse();
        this.year = s.getYear();
        this.section = s.getSection();
        this.emergencyPhone = s.getPhone();

        // Assign deterministic blood group based on student id
        String[] bGroups = {"O+", "A+", "B+", "AB+", "O-", "A-", "B-"};
        this.bloodGroup = bGroups[(int) (Math.abs(s.getStudentId()) % bGroups.length)];

        int expYear = LocalDate.now().getYear() + (4 - s.getYear()) + 1;
        this.validUntil = "30-JUN-" + expYear;

        this.formattedCard = generateCardLayout(s);
    }

    private String generateCardLayout(Student s) {
        StringBuilder sb = new StringBuilder();
        sb.append("+----------------------------------------------------------------------+\n");
        sb.append("|            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE             |\n");
        sb.append("|                     OFFICIAL STUDENT IDENTITY CARD                   |\n");
        sb.append("+----------------------------------------------------------------------+\n");
        sb.append("|  +-------------------+                                               |\n");
        sb.append(String.format("|  |    PHOTO BOX      |   STUDENT NAME: %-29s |\n", truncate(s.getName(), 29)));
        sb.append(String.format("|  |                   |   ROLL NO     : %-29s |\n", truncate(s.getRollNumber(), 29)));
        sb.append(String.format("|  |  [ PASSPORT SIZE ]|   STUDENT ID  : %-29d |\n", s.getStudentId()));
        sb.append(String.format("|  |                   |   PROGRAM     : %-29s |\n", truncate(s.getCourse() + " (" + s.getDepartment() + ")", 29)));
        sb.append(String.format("|  |                   |   YEAR / SEM  : Year %d, Sem %-16d |\n", s.getYear(), s.getSemester()));
        sb.append(String.format("|  +-------------------+   BLOOD GROUP : %-29s |\n", bloodGroup));
        sb.append(String.format("|  CONTACT : %-15s   EMERGENCY PH: %-24s |\n", s.getPhone(), emergencyPhone));
        sb.append(String.format("|  VALID UNTIL: %-12s   CAMPUS HOSTEL / BLOCK : %-16s |\n", validUntil, "CAMPUS SEC-" + s.getSection()));
        sb.append("|                                                                      |\n");
        sb.append(String.format("|  BARCODE: ||| ||||| || |||| ||| ||||||| ||| ||  [ %-18s ]   |\n", s.getRollNumber()));
        sb.append("|                                                                      |\n");
        sb.append("|  [Holder Signature]                             [Issuing Authority]  |\n");
        sb.append("+----------------------------------------------------------------------+\n");
        sb.append("| TERMS: 1. Always wear ID card inside premises. 2. Loss must be       |\n");
        sb.append("| reported to Security Officer immediately. 3. Non-transferable.       |\n");
        sb.append("+----------------------------------------------------------------------+\n");
        return sb.toString();
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() > maxLen ? val.substring(0, maxLen) : val;
    }

    public long getStudentId() { return studentId; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public String getCourse() { return course; }
    public int getYear() { return year; }
    public String getSection() { return section; }
    public String getBloodGroup() { return bloodGroup; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public String getValidUntil() { return validUntil; }
    public String getFormattedCard() { return formattedCard; }

    @Override
    public String toString() {
        return String.format("Student ID Card [%s - %s]", rollNumber, studentName);
    }
}
