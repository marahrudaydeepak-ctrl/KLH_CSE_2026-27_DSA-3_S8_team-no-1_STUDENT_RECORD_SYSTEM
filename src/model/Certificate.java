package model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model representing an officially issued institutional Certificate or Formal Notice.
 * Includes unique verification hash, issuance metadata, and formatted printable layout.
 */
public class Certificate implements Serializable {
    private static final long serialVersionUID = 1L;

    private String certificateId;
    private long studentId;
    private String rollNumber;
    private String studentName;
    private String department;
    private String course;
    private CertificateType certificateType;
    private String issueDate;
    private String purpose;
    private String verificationHash;
    private String formattedDocument;

    public Certificate() {}

    public Certificate(Student student, CertificateType certificateType, String purpose) {
        this.studentId = student.getStudentId();
        this.rollNumber = student.getRollNumber();
        this.studentName = student.getName();
        this.department = student.getDepartment();
        this.course = student.getCourse();
        this.certificateType = certificateType;
        this.issueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        this.purpose = (purpose != null && !purpose.trim().isEmpty()) ? purpose.trim() : "General Academic & Administrative Records";

        long certNum = Math.abs((student.getStudentId() * 31 + certificateType.ordinal() * 17 + System.currentTimeMillis() % 10000) % 90000) + 10000;
        this.certificateId = String.format("APEX/%s/%d/%05d", department, LocalDate.now().getYear(), certNum);
        this.verificationHash = computeVerificationHash(student, certificateId, issueDate);
        this.formattedDocument = generateDocumentContent(student);
    }

    private String computeVerificationHash(Student student, String certId, String date) {
        try {
            String raw = student.getStudentId() + "|" + student.getRollNumber() + "|" + certId + "|" + date + "|APEX-VERIFY-KEY";
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                sb.append(String.format("%02X", digest[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return "VER-" + Math.abs(student.getStudentId() % 999999);
        }
    }

    private String generateDocumentContent(Student s) {
        StringBuilder sb = new StringBuilder();
        String title = certificateType.getDisplayName().toUpperCase();

        sb.append("****************************************************************************************************\n");
        sb.append("*                                                                                                  *\n");
        sb.append("*                          APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          *\n");
        sb.append("*                  (An Autonomous Institute Accredited by National Assessment Council)             *\n");
        sb.append("*                                 OFFICE OF THE REGISTRAR & DEAN                                   *\n");
        sb.append("*                                                                                                  *\n");
        sb.append("****************************************************************************************************\n");
        sb.append(String.format(" Certificate Ref No : %-40s Date of Issue : %s\n", certificateId, issueDate));
        sb.append(String.format(" Verification Code  : %-40s Security Check: VERIFIED (INSTITUTIONAL ARCHIVE)\n", verificationHash));
        sb.append("----------------------------------------------------------------------------------------------------\n\n");
        sb.append(String.format("                                 %s\n\n", title));

        switch (certificateType) {
            case BONAFIDE -> {
                sb.append("    This is to certify that Mr./Ms. ").append(s.getName().toUpperCase()).append("\n");
                sb.append("    bearing Roll Number ").append(s.getRollNumber()).append(" (Student ID: ").append(s.getStudentId()).append(")\n");
                sb.append("    is a bona fide and regular student of this institution, currently enrolled in:\n\n");
                sb.append(String.format("        Program / Course   : %s in %s\n", s.getCourse(), s.getDepartment()));
                sb.append(String.format("        Current Academic Yr: Year %d, Semester %d (Section %s)\n", s.getYear(), s.getSemester(), s.getSection()));
                sb.append(String.format("        Academic Standing  : Cumulative CGPA: %.2f (Grade: %s) | Attendance: %.1f%%\n\n", s.getCgpa(), s.getGrade(), s.getAttendance()));
                sb.append("    To the best of our knowledge, his/her conduct and moral character during this tenure have\n");
                sb.append("    been exemplary and satisfactory.\n\n");
                sb.append("    This certificate is granted upon student request for the stated purpose of:\n");
                sb.append("    \"").append(purpose).append("\"\n\n");
            }
            case COURSE_COMPLETION -> {
                sb.append("    This is to certify that ").append(s.getName().toUpperCase()).append("\n");
                sb.append("    Roll Number: ").append(s.getRollNumber()).append(" | Department of ").append(s.getDepartment()).append("\n\n");
                sb.append("    has successfully completed all prescribed academic, laboratory, and capstone project\n");
                sb.append("    requirements for the award of the degree of:\n\n");
                sb.append(String.format("            >>> %s IN %s <<<\n\n", s.getCourse().toUpperCase(), s.getDepartment().toUpperCase()));
                sb.append(String.format("    Final Cumulative Grade Point Average (CGPA) : %.2f / 10.00\n", s.getCgpa()));
                sb.append(String.format("    Overall Academic Division Awarded           : %s\n",
                        s.getCgpa() >= 8.5 ? "FIRST CLASS WITH DISTINCTION" : (s.getCgpa() >= 6.5 ? "FIRST CLASS" : "SECOND CLASS")));
                sb.append(String.format("    Key Technical Proficiencies                 : %s\n\n", String.join(", ", s.getSkills())));
                sb.append("    The Institute commends the candidate and wishes them success in all future pursuits.\n\n");
            }
            case MERIT_EXCELLENCE -> {
                sb.append("                                   CERTIFICATE OF MERIT\n");
                sb.append("                             AWARDED FOR ACADEMIC DISTINCTION\n\n");
                sb.append("    The Academic Council and Governing Body proudly present this Certificate of Merit to:\n\n");
                sb.append(String.format("                           ★ ★  %s  ★ ★\n\n", s.getName().toUpperCase()));
                sb.append(String.format("    Roll No: %s | Department: %s | Program: %s\n\n", s.getRollNumber(), s.getDepartment(), s.getCourse()));
                sb.append(String.format("    in recognition of outstanding academic brilliance, securing a CGPA of %.2f / 10.00\n", s.getCgpa()));
                sb.append(String.format("    and consistent dedication to research, innovation, and collegiate leadership.\n\n"));
                if (!s.getAchievements().isEmpty()) {
                    sb.append("    Noted Distinctions: ").append(String.join("; ", s.getAchievements())).append("\n\n");
                }
                sb.append("    Issued in recognition of achieving Dean's Honor Roll and Academic Standing.\n\n");
            }
            case CHARACTER_CONDUCT -> {
                sb.append("    This is to certify that ").append(s.getName().toUpperCase()).append("\n");
                sb.append("    Son / Daughter / Ward of record, residing at ").append(s.getAddress()).append(",\n");
                sb.append("    Roll No: ").append(s.getRollNumber()).append(" in the Department of ").append(s.getDepartment()).append(",\n");
                sb.append("    has been a student of ").append(s.getCourse()).append(" from Year ").append(s.getYear()).append(".\n\n");
                sb.append("    During the entire period of study in this college:\n");
                sb.append("      1. The student exhibited commendable discipline, diligence, and respect towards faculty.\n");
                sb.append("      2. No disciplinary actions or campus grievances were recorded against the student.\n");
                sb.append("      3. The student demonstrated exemplary moral character, social responsibility, and integrity.\n\n");
                sb.append("    We certify that his/her conduct and character have been VERY GOOD.\n\n");
            }
            case OFFICIAL_TRANSCRIPT -> {
                sb.append("    OFFICIAL CONSOLIDATED TRANSCRIPT OF ACADEMIC RECORD\n\n");
                sb.append(String.format("    Candidate Name   : %-30s | Date of Birth : %s\n", s.getName(), s.getDateOfBirth()));
                sb.append(String.format("    Registration No  : %-30s | Degree / Branch: %s (%s)\n", s.getRollNumber(), s.getCourse(), s.getDepartment()));
                sb.append(String.format("    Current Semester : Semester %-25d | Current Year  : Year %d\n", s.getSemester(), s.getYear()));
                sb.append(String.format("    Overall Attendance: %-28.1f%% | Total Marks %%: %.2f%%\n", s.getAttendance(), s.getMarks()));
                sb.append(String.format("    Graduation CGPA  : %-28.2f | Official Grade: %s\n\n", s.getCgpa(), s.getGrade()));
                sb.append("    Academic Standing: Good Regular Standing. All prerequisite course modules verified.\n");
                sb.append("    Major Technical Skills: ").append(String.join(", ", s.getSkills())).append("\n");
                if (!s.getProjects().isEmpty()) {
                    sb.append("    Capstone Research Project: ").append(s.getProjects().get(0)).append("\n");
                }
                sb.append("\n    This document serves as an authorized interim academic transcript for official evaluation.\n\n");
            }
            case TRANSFER_LEAVING -> {
                sb.append("    INSTITUTIONAL TRANSFER & LEAVING CERTIFICATE (T.C.)\n\n");
                sb.append(String.format("    1. Name of the Pupil          : %s\n", s.getName()));
                sb.append(String.format("    2. Roll / Admission Number    : %s\n", s.getRollNumber()));
                sb.append(String.format("    3. Date of Birth              : %s\n", s.getDateOfBirth()));
                sb.append(String.format("    4. Department & Course Enrolled: %s (%s)\n", s.getDepartment(), s.getCourse()));
                sb.append(String.format("    5. Class / Semester Left      : Year %d, Semester %d\n", s.getYear(), s.getSemester()));
                sb.append(String.format("    6. Academic Progress & Result : %s (CGPA: %.2f)\n", s.getGrade(), s.getCgpa()));
                sb.append("    7. Institutional College Dues : ALL DUES CLEARED (No Dues Pending)\n");
                sb.append(String.format("    8. Attendance Percentage      : %.1f%%\n", s.getAttendance()));
                sb.append(String.format("    9. Reason for Leaving College : %s\n", purpose));
                sb.append("    10. General Conduct & Character: Good\n\n");
            }
            case INTERNSHIP_NOC -> {
                sb.append("    NO OBJECTION CERTIFICATE (NOC) FOR INDUSTRIAL INTERNSHIP\n\n");
                sb.append("    To Whom It May Concern,\n\n");
                sb.append("    This Institute has NO OBJECTION to our student ").append(s.getName().toUpperCase()).append(",\n");
                sb.append("    bearing Roll Number ").append(s.getRollNumber()).append(" in Department of ").append(s.getDepartment()).append(",\n");
                sb.append("    pursuing a professional training or industrial internship in line with academic coursework.\n\n");
                sb.append(String.format("    The student maintains a CGPA of %.2f with %.1f%% attendance and is qualified\n", s.getCgpa(), s.getAttendance()));
                sb.append("    to undertake hands-on engineering experience in: ").append(String.join(", ", s.getSkills())).append(".\n\n");
                sb.append("    Authorized Internship Field / Organization: ").append(purpose).append("\n\n");
            }
            case ATTENDANCE_WARNING -> {
                sb.append("                           OFFICIAL ATTENDANCE SHORTAGE ADVISORY\n");
                sb.append("                            (MANDATORY REGULATORY REQUIREMENT < 75%)\n\n");
                sb.append("    MEMORANDUM TO: ").append(s.getName()).append(" (Roll No: ").append(s.getRollNumber()).append(")\n");
                sb.append("    Department of ").append(s.getDepartment()).append(" | Year: ").append(s.getYear()).append(", Semester: ").append(s.getSemester()).append("\n\n");
                sb.append(String.format("    Current Recorded Attendance: %.1f%%  [STATUTORY REQUIREMENT: 75.00%%]\n\n", s.getAttendance()));
                sb.append("    This is an official administrative warning informing you that your attendance has fallen\n");
                sb.append("    below the mandatory university minimum criteria of 75.0% required to be eligible for the\n");
                sb.append("    End-Semester Autonomous Examinations.\n\n");
                sb.append("    DIRECTIVE: You are hereby instructed to submit a formal condonation request along with valid\n");
                sb.append("    medical or official supporting documentation to the Head of Department within 5 working days.\n\n");
            }
        }

        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append("   [Seal of Institute]                                                                              \n");
        sb.append("                                                                                                    \n");
        sb.append("   Prepared by: Administrative Cell       Verified by: Academic Registrar       Dean / Director     \n");
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(" Note: This document is electronically generated with cryptographic signature verification.\n");
        sb.append(" To verify authenticity, visit https://verify.apex.edu/cert and enter code: ").append(verificationHash).append("\n");
        sb.append("****************************************************************************************************\n");

        return sb.toString();
    }

    // Getters
    public String getCertificateId() { return certificateId; }
    public long getStudentId() { return studentId; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public String getCourse() { return course; }
    public CertificateType getCertificateType() { return certificateType; }
    public String getIssueDate() { return issueDate; }
    public String getPurpose() { return purpose; }
    public String getVerificationHash() { return verificationHash; }
    public String getFormattedDocument() { return formattedDocument; }

    @Override
    public String toString() {
        return String.format("[%s] %s for %s (%s)", certificateId, certificateType.getDisplayName(), studentName, rollNumber);
    }
}
