package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Core Student domain model representing comprehensive academic, personal,
 * and extracurricular student record details.
 */
public class Student implements Serializable, Comparable<Student> {
    private static final long serialVersionUID = 1L;

    private long studentId;
    private String rollNumber;
    private String name;
    private String dateOfBirth;
    private String gender;
    private String department;
    private String course;
    private int year;
    private int semester;
    private String section;
    private String email;
    private String phone;
    private String address;
    private double attendance; // percentage 0 - 100
    private double marks;      // percentage / total 0 - 100
    private double gpa;        // 0.0 - 10.0
    private double cgpa;       // 0.0 - 10.0
    private String grade;
    private List<String> skills;
    private List<String> projects;
    private List<String> certifications;
    private List<String> achievements;
    private List<String> internships;
    private String remarks;

    public Student() {
        this.skills = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.certifications = new ArrayList<>();
        this.achievements = new ArrayList<>();
        this.internships = new ArrayList<>();
    }

    public Student(long studentId, String rollNumber, String name, String dateOfBirth, String gender,
                   String department, String course, int year, int semester, String section,
                   String email, String phone, String address, double attendance, double marks,
                   double gpa, double cgpa, String grade, List<String> skills, List<String> projects,
                   List<String> certifications, List<String> achievements, List<String> internships,
                   String remarks) {
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.department = department;
        this.course = course;
        this.year = year;
        this.semester = semester;
        this.section = section;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.attendance = attendance;
        this.marks = marks;
        this.gpa = gpa;
        this.cgpa = cgpa;
        this.grade = (grade == null || grade.isEmpty()) ? calculateGrade(cgpa) : grade;
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
        this.projects = projects != null ? new ArrayList<>(projects) : new ArrayList<>();
        this.certifications = certifications != null ? new ArrayList<>(certifications) : new ArrayList<>();
        this.achievements = achievements != null ? new ArrayList<>(achievements) : new ArrayList<>();
        this.internships = internships != null ? new ArrayList<>(internships) : new ArrayList<>();
        this.remarks = remarks;
    }

    /**
     * Copy constructor for defensive copying and undo/redo operations.
     */
    public Student(Student other) {
        this(other.studentId, other.rollNumber, other.name, other.dateOfBirth, other.gender,
             other.department, other.course, other.year, other.semester, other.section,
             other.email, other.phone, other.address, other.attendance, other.marks,
             other.gpa, other.cgpa, other.grade, other.skills, other.projects,
             other.certifications, other.achievements, other.internships, other.remarks);
    }

    public static String calculateGrade(double cgpa) {
        if (cgpa >= 9.0) return "A+";
        if (cgpa >= 8.0) return "A";
        if (cgpa >= 7.0) return "B+";
        if (cgpa >= 6.0) return "B";
        if (cgpa >= 5.0) return "C";
        if (cgpa >= 4.0) return "P";
        return "F";
    }

    // Getters and Setters
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getAttendance() { return attendance; }
    public void setAttendance(double attendance) { this.attendance = attendance; }

    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { 
        this.cgpa = cgpa; 
        this.grade = calculateGrade(cgpa);
    }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills != null ? skills : new ArrayList<>(); }

    public List<String> getProjects() { return projects; }
    public void setProjects(List<String> projects) { this.projects = projects != null ? projects : new ArrayList<>(); }

    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications != null ? certifications : new ArrayList<>(); }

    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements != null ? achievements : new ArrayList<>(); }

    public List<String> getInternships() { return internships; }
    public void setInternships(List<String> internships) { this.internships = internships != null ? internships : new ArrayList<>(); }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public int compareTo(Student o) {
        return Long.compare(this.studentId, o.studentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return studentId == student.studentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return String.format("[ID: %-5d | Roll: %-12s | Name: %-18s | Dept: %-4s | CGPA: %4.2f | Attd: %5.1f%% | Grade: %-2s]",
                studentId, rollNumber, name, department, cgpa, attendance, grade);
    }

    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================\n");
        sb.append(String.format(" STUDENT PROFILE: ID #%d (%s)\n", studentId, rollNumber));
        sb.append("====================================================================\n");
        sb.append(String.format(" Name          : %s\n", name));
        sb.append(String.format(" Gender / DOB  : %s / %s\n", gender, dateOfBirth));
        sb.append(String.format(" Academic Dept : %s | Course: %s | Year: %d | Sem: %d | Sec: %s\n", 
                department, course, year, semester, section));
        sb.append(String.format(" Contact       : %s | Ph: %s\n", email, phone));
        sb.append(String.format(" Address       : %s\n", address));
        sb.append(String.format(" Attendance    : %.2f%%\n", attendance));
        sb.append(String.format(" Marks / GPA   : %.2f%% | GPA: %.2f | CGPA: %.2f (Grade: %s)\n", 
                marks, gpa, cgpa, grade));
        sb.append(String.format(" Skills        : %s\n", String.join(", ", skills)));
        sb.append(String.format(" Projects      : %s\n", String.join(", ", projects)));
        sb.append(String.format(" Certifications: %s\n", String.join(", ", certifications)));
        sb.append(String.format(" Achievements  : %s\n", String.join(", ", achievements)));
        sb.append(String.format(" Internships   : %s\n", String.join(", ", internships)));
        sb.append(String.format(" Remarks       : %s\n", remarks != null ? remarks : "N/A"));
        sb.append("====================================================================");
        return sb.toString();
    }
}
