package file;

import model.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * File persistence manager supporting CSV text import/export,
 * binary serialization backup/restore, and formatted text report generation.
 * Uses BufferedReader, BufferedWriter, FileReader, FileWriter,
 * ObjectInputStream, and ObjectOutputStream.
 */
public class StudentFileManager {

    private static final String DEFAULT_CSV_FILE = "data/students.csv";
    private static final String DEFAULT_BACKUP_FILE = "data/students_backup.dat";

    public static boolean saveToCSV(List<Student> students, String filePath) {
        String path = (filePath != null && !filePath.trim().isEmpty()) ? filePath : DEFAULT_CSV_FILE;
        ensureParentDirectory(path);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            // Header
            bw.write("studentId,rollNumber,name,dateOfBirth,gender,department,course,year,semester,section,email,phone,address,attendance,marks,gpa,cgpa,grade,skills,projects,remarks");
            bw.newLine();

            for (Student s : students) {
                String line = String.format("%d,%s,%s,%s,%s,%s,%s,%d,%d,%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%s,\"%s\",\"%s\",%s",
                        s.getStudentId(),
                        sanitize(s.getRollNumber()),
                        sanitize(s.getName()),
                        sanitize(s.getDateOfBirth()),
                        sanitize(s.getGender()),
                        sanitize(s.getDepartment()),
                        sanitize(s.getCourse()),
                        s.getYear(),
                        s.getSemester(),
                        sanitize(s.getSection()),
                        sanitize(s.getEmail()),
                        sanitize(s.getPhone()),
                        sanitize(s.getAddress()),
                        s.getAttendance(),
                        s.getMarks(),
                        s.getGpa(),
                        s.getCgpa(),
                        sanitize(s.getGrade()),
                        String.join(";", s.getSkills()),
                        String.join(";", s.getProjects()),
                        sanitize(s.getRemarks())
                );
                bw.write(line);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving students to CSV: " + e.getMessage());
            return false;
        }
    }

    public static List<Student> loadFromCSV(String filePath) {
        String path = (filePath != null && !filePath.trim().isEmpty()) ? filePath : DEFAULT_CSV_FILE;
        List<Student> list = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Student s = parseStudentLine(line);
                if (s != null) {
                    list.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students CSV: " + e.getMessage());
        }
        return list;
    }

    private static Student parseStudentLine(String line) {
        try {
            // Basic CSV token parser handling quotes
            List<String> tokens = parseCsvTokens(line);
            if (tokens.size() < 17) return null;

            long id = Long.parseLong(tokens.get(0));
            String roll = tokens.get(1);
            String name = tokens.get(2);
            String dob = tokens.get(3);
            String gender = tokens.get(4);
            String dept = tokens.get(5);
            String course = tokens.get(6);
            int yr = Integer.parseInt(tokens.get(7));
            int sem = Integer.parseInt(tokens.get(8));
            String sec = tokens.get(9);
            String email = tokens.get(10);
            String phone = tokens.get(11);
            String addr = tokens.get(12);
            double att = Double.parseDouble(tokens.get(13));
            double marks = Double.parseDouble(tokens.get(14));
            double gpa = Double.parseDouble(tokens.get(15));
            double cgpa = Double.parseDouble(tokens.get(16));
            String grade = tokens.size() > 17 ? tokens.get(17) : Student.calculateGrade(cgpa);

            List<String> skills = new ArrayList<>();
            if (tokens.size() > 18 && !tokens.get(18).isEmpty()) {
                skills.addAll(Arrays.asList(tokens.get(18).split(";")));
            }

            List<String> projs = new ArrayList<>();
            if (tokens.size() > 19 && !tokens.get(19).isEmpty()) {
                projs.addAll(Arrays.asList(tokens.get(19).split(";")));
            }

            String remarks = tokens.size() > 20 ? tokens.get(20) : "";

            return new Student(id, roll, name, dob, gender, dept, course, yr, sem, sec,
                    email, phone, addr, att, marks, gpa, cgpa, grade, skills, projs,
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), remarks);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> parseCsvTokens(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }

    public static boolean backupRecords(List<Student> students, String backupPath) {
        String path = (backupPath != null && !backupPath.trim().isEmpty()) ? backupPath : DEFAULT_BACKUP_FILE;
        ensureParentDirectory(path);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(new ArrayList<>(students));
            return true;
        } catch (IOException e) {
            System.err.println("Error backing up records: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Student> restoreRecords(String backupPath) {
        String path = (backupPath != null && !backupPath.trim().isEmpty()) ? backupPath : DEFAULT_BACKUP_FILE;
        File file = new File(path);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                return (List<Student>) obj;
            }
        } catch (Exception e) {
            System.err.println("Error restoring records: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static boolean exportReport(String reportContent, String filePath) {
        ensureParentDirectory(filePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(reportContent);
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
            return false;
        }
    }

    private static String sanitize(String val) {
        if (val == null) return "";
        return val.replace(",", " ").replace("\"", "'");
    }

    private static void ensureParentDirectory(String filePath) {
        File f = new File(filePath);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}
