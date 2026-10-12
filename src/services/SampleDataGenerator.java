package services;

import model.Student;
import java.util.*;

/**
 * Generator for initial realistic student records (30+ records)
 * and scalable synthetic benchmark datasets (100 to 10,000+ students).
 */
public class SampleDataGenerator {

    private static final String[] FIRST_NAMES = {
            "Tejas", "Aarav", "Priya", "Rohan", "Sneha", "Aditya", "Ananya", "Vikram",
            "Kavya", "Rahul", "Pooja", "Arjun", "Neha", "Siddharth", "Ishaan", "Tanvi",
            "Manish", "Meera", "Varun", "Rhea", "Kiran", "Nikhil", "Deepika", "Harsh",
            "Divya", "Gaurav", "Swati", "Suresh", "Bhavna", "Kunal", "Shreya", "Akash"
    };

    private static final String[] LAST_NAMES = {
            "Reddy", "Sharma", "Kumar", "Patel", "Verma", "Singh", "Nair", "Iyer",
            "Gupta", "Rao", "Joshi", "Deshmukh", "Chopra", "Malhotra", "Mehta", "Bose",
            "Chatterjee", "Mishra", "Pandey", "Saxena", "Agarwal", "Bhatia", "Kapoor", "Choudhury"
    };

    private static final String[] DEPARTMENTS = {"CSE", "ECE", "IT", "MECH", "CIVIL", "AIML", "DS"};
    private static final String[] COURSES = {"B.Tech", "M.Tech", "B.Sc CS", "MCA", "Dual Degree"};
    private static final String[] SECTIONS = {"A", "B", "C", "D"};

    private static final String[] SKILLS_POOL = {
            "Java", "Python", "C++", "SQL", "Data Structures", "Algorithms",
            "Machine Learning", "Web Development", "React", "Spring Boot",
            "Cloud Computing", "Docker", "Kubernetes", "Cybersecurity", "Blockchain"
    };

    private static final String[] PROJECTS_POOL = {
            "Intelligent Attendance System", "Autonomous Drone Navigation",
            "Blockchain Credential Verification", "Distributed Key-Value Store",
            "E-Commerce Microservices", "Medical Image Classification",
            "Smart Campus IoT Grid", "Natural Language Code Generator",
            "Financial Fraud Detection Engine", "Traffic Congestion Optimization"
    };

    /**
     * Generates a curated, realistic initial dataset of 32 student records.
     */
    public static List<Student> generateInitialStudents() {
        List<Student> students = new ArrayList<>();

        students.add(createStudent(2520080001L, "2520080001", "Tejas Reddy", "2003-05-14", "Male", "CSE", "B.Tech", 3, 5, "A", "tejas.reddy@univ.edu", "9876543210", "Hyderabad, Telangana", 92.5, 88.0, 9.1, 9.15, List.of("Java", "Data Structures", "SQL", "Spring Boot"), List.of("Intelligent Attendance System", "Distributed Key-Value Store"), List.of("Oracle Java Certified"), List.of("Dean's Honor List"), List.of("Google Summer of Code"), "Outstanding algorithmic acumen."));
        students.add(createStudent(2520080002L, "2520080002", "Priya Sharma", "2003-08-22", "Female", "CSE", "B.Tech", 3, 5, "A", "priya.sharma@univ.edu", "9876543211", "Bengaluru, Karnataka", 89.0, 91.5, 9.4, 9.38, List.of("Python", "Machine Learning", "Data Structures", "SQL"), List.of("Medical Image Classification", "Natural Language Code Generator"), List.of("AWS Machine Learning Specialist"), List.of("Hackathon 1st Runner Up"), List.of("Microsoft Research Intern"), "Consistent academic excellence."));
        students.add(createStudent(2520080003L, "2520080003", "Aarav Patel", "2002-11-10", "Male", "IT", "B.Tech", 4, 7, "B", "aarav.patel@univ.edu", "9876543212", "Ahmedabad, Gujarat", 78.5, 76.0, 7.8, 7.75, List.of("Java", "Web Development", "React", "Docker"), List.of("E-Commerce Microservices"), List.of("Certified Kubernetes Admin"), List.of("Open Source Contributor"), List.of("Infosys Intern"), "Strong full-stack engineering skills."));
        students.add(createStudent(2520080004L, "2520080004", "Sneha Nair", "2003-01-30", "Female", "AIML", "B.Tech", 3, 5, "A", "sneha.nair@univ.edu", "9876543213", "Kochi, Kerala", 95.0, 94.0, 9.7, 9.68, List.of("Python", "Machine Learning", "Algorithms", "C++"), List.of("Medical Image Classification", "Autonomous Drone Navigation"), List.of("DeepLearning.AI Specialization"), List.of("National Merit Scholar"), List.of("Adobe AI Labs Intern"), "Top performer in Artificial Intelligence."));
        students.add(createStudent(2520080005L, "2520080005", "Rohan Verma", "2003-07-19", "Male", "ECE", "B.Tech", 3, 5, "B", "rohan.verma@univ.edu", "9876543214", "Delhi, NCR", 68.0, 58.0, 5.8, 5.70, List.of("C++", "Cybersecurity"), List.of("Smart Campus IoT Grid"), List.of("CompTIA Security+"), List.of("Robotics Club Lead"), List.of(), "Needs attendance monitoring (< 75%)."));
        students.add(createStudent(2520080006L, "2520080006", "Ananya Iyer", "2003-03-12", "Female", "CSE", "B.Tech", 3, 5, "A", "ananya.iyer@univ.edu", "9876543215", "Chennai, Tamil Nadu", 88.0, 85.0, 8.6, 8.55, List.of("Java", "SQL", "Cloud Computing", "Kubernetes"), List.of("Distributed Key-Value Store"), List.of("AWS Solutions Architect"), List.of("Merit Scholar"), List.of("Amazon Intern"), "Diligent and collaborative researcher."));
        students.add(createStudent(2520080007L, "2520080007", "Aditya Singh", "2002-09-05", "Male", "MECH", "B.Tech", 4, 7, "A", "aditya.singh@univ.edu", "9876543216", "Jaipur, Rajasthan", 72.0, 62.0, 6.2, 6.10, List.of("Python", "Web Development"), List.of("Traffic Congestion Optimization"), List.of(), List.of("Formula Student Design"), List.of("Tata Motors Intern"), "Attendance warning issued."));
        students.add(createStudent(2520080008L, "2520080008", "Kavya Rao", "2003-12-01", "Female", "DS", "B.Tech", 2, 3, "A", "kavya.rao@univ.edu", "9876543217", "Visakhapatnam, AP", 91.0, 89.0, 9.0, 8.95, List.of("Python", "SQL", "Machine Learning", "Algorithms"), List.of("Financial Fraud Detection Engine"), List.of("Data Science Professional"), List.of("Best First-Year Paper"), List.of(), "Excellent analytical and quantitative acumen."));
        students.add(createStudent(2520080009L, "2520080009", "Rahul Kumar", "2003-04-18", "Male", "CSE", "B.Tech", 3, 5, "C", "rahul.kumar@univ.edu", "9876543218", "Patna, Bihar", 84.0, 79.0, 8.1, 8.05, List.of("Java", "C++", "Data Structures"), List.of("Intelligent Attendance System"), List.of(), List.of("Coding Contest Finalist"), List.of(), "Solid foundational data structures skills."));
        students.add(createStudent(2520080010L, "2520080010", "Pooja Deshmukh", "2002-06-25", "Female", "IT", "B.Tech", 4, 7, "A", "pooja.d@univ.edu", "9876543219", "Pune, Maharashtra", 93.0, 92.0, 9.3, 9.25, List.of("Java", "React", "Cloud Computing", "Docker"), List.of("Blockchain Credential Verification"), List.of("Oracle Certified Java Pro"), List.of("Department Gold Medalist"), List.of("Barclays Intern"), "High performance in software engineering."));
        students.add(createStudent(2520080011L, "2520080011", "Arjun Gupta", "2003-10-14", "Male", "AIML", "B.Tech", 2, 3, "B", "arjun.gupta@univ.edu", "9876543220", "Lucknow, UP", 86.0, 83.0, 8.4, 8.35, List.of("Python", "C++", "Machine Learning"), List.of("Autonomous Drone Navigation"), List.of(), List.of(), List.of(), "Active participant in AI labs."));
        students.add(createStudent(2520080012L, "2520080012", "Neha Malhotra", "2004-02-17", "Female", "CSE", "B.Tech", 2, 3, "A", "neha.m@univ.edu", "9876543221", "Chandigarh", 96.0, 95.0, 9.8, 9.75, List.of("C++", "Algorithms", "Data Structures", "Java"), List.of("Distributed Key-Value Store", "Natural Language Code Generator"), List.of("ICPC Regionalist"), List.of("Institute Rank 1"), List.of("Uber Star Intern"), "Exemplary academic and competitive programming record."));
        students.add(createStudent(2520080013L, "2520080013", "Siddharth Bose", "2003-09-08", "Male", "CIVIL", "B.Tech", 3, 5, "A", "siddharth.bose@univ.edu", "9876543222", "Kolkata, WB", 70.0, 52.0, 5.1, 5.05, List.of("SQL", "Python"), List.of("Traffic Congestion Optimization"), List.of(), List.of(), List.of(), "Academic counseling suggested."));
        students.add(createStudent(2520080014L, "2520080014", "Tanvi Joshi", "2003-11-29", "Female", "ECE", "B.Tech", 3, 5, "A", "tanvi.j@univ.edu", "9876543223", "Nagpur, Maharashtra", 88.5, 87.0, 8.8, 8.78, List.of("C++", "Cybersecurity", "Python"), List.of("Smart Campus IoT Grid"), List.of("Cisco Network Associate"), List.of("Women in Tech Scholar"), List.of("Qualcomm Intern"), "Strong embedded and cybersecurity focus."));
        students.add(createStudent(2520080015L, "2520080015", "Ishaan Chopra", "2002-08-11", "Male", "CSE", "B.Tech", 4, 8, "A", "ishaan.c@univ.edu", "9876543224", "Noida, UP", 82.0, 80.0, 8.2, 8.18, List.of("Java", "Spring Boot", "Docker", "SQL"), List.of("E-Commerce Microservices"), List.of(), List.of(), List.of("Oracle Intern"), "Reliable team player in capstone tasks."));

        // Remaining 17 students to reach 32 distinct students
        for (int i = 16; i <= 32; i++) {
            long id = 2520080000L + i;
            String roll = String.valueOf(id);
            String fName = FIRST_NAMES[(i - 1) % FIRST_NAMES.length];
            String lName = LAST_NAMES[(i * 3) % LAST_NAMES.length];
            String name = fName + " " + lName;
            String dept = DEPARTMENTS[i % DEPARTMENTS.length];
            String course = COURSES[i % COURSES.length];
            int year = (i % 4) + 1;
            int sem = year * 2 - (i % 2);
            String sec = SECTIONS[i % SECTIONS.length];
            String email = fName.toLowerCase() + "." + lName.toLowerCase() + i + "@univ.edu";
            String phone = "98765432" + String.format("%02d", i);
            String address = "Campus Hostel Block " + (i % 8 + 1) + ", University Town";
            double attendance = 60.0 + (i * 2.3) % 40.0;
            double marks = 45.0 + (i * 3.1) % 55.0;
            double cgpa = Math.min(10.0, (marks / 10.0) + (i % 5) * 0.1);
            cgpa = Math.round(cgpa * 100.0) / 100.0;
            double gpa = cgpa;

            List<String> skills = new ArrayList<>();
            skills.add(SKILLS_POOL[i % SKILLS_POOL.length]);
            skills.add(SKILLS_POOL[(i + 3) % SKILLS_POOL.length]);
            skills.add(SKILLS_POOL[(i + 7) % SKILLS_POOL.length]);

            List<String> projs = new ArrayList<>();
            projs.add(PROJECTS_POOL[i % PROJECTS_POOL.length]);

            List<String> certs = (i % 3 == 0) ? List.of("Certified Professional " + skills.get(0)) : new ArrayList<>();
            List<String> achs = (cgpa >= 8.5) ? List.of("Merit Certificate") : new ArrayList<>();
            List<String> interns = (year >= 3) ? List.of("Industry Training Partner " + i) : new ArrayList<>();

            students.add(createStudent(id, roll, name, "2003-0" + ((i % 9) + 1) + "-15", (i % 2 == 0) ? "Male" : "Female",
                    dept, course, year, sem, sec, email, phone, address, attendance, marks, gpa, cgpa,
                    skills, projs, certs, achs, interns, "Student in regular standing."));
        }

        return students;
    }

    private static Student createStudent(long id, String roll, String name, String dob, String gender,
                                         String dept, String course, int yr, int sem, String sec,
                                         String email, String phone, String addr, double att, double marks,
                                         double gpa, double cgpa, List<String> skills, List<String> projs,
                                         List<String> certs, List<String> achs, List<String> interns, String remarks) {
        return new Student(id, roll, name, dob, gender, dept, course, yr, sem, sec,
                email, phone, addr, att, marks, gpa, cgpa, null, skills, projs, certs, achs, interns, remarks);
    }

    /**
     * Synthetically generates large benchmark datasets of arbitrary count (100 to 10,000+).
     */
    public static List<Student> generateBenchmarkDataset(int count, long seed) {
        List<Student> list = new ArrayList<>(count);
        Random rand = (seed != 0) ? new Random(seed) : new Random();

        for (int i = 1; i <= count; i++) {
            long id = 2520000000L + i;
            String roll = String.valueOf(id);
            String fName = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)];
            String lName = LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
            String name = fName + " " + lName + " " + i;
            String dept = DEPARTMENTS[rand.nextInt(DEPARTMENTS.length)];
            String course = COURSES[rand.nextInt(COURSES.length)];
            int yr = 1 + rand.nextInt(4);
            int sem = yr * 2 - rand.nextInt(2);
            String sec = SECTIONS[rand.nextInt(SECTIONS.length)];
            String email = "student" + i + "@univ.edu";
            String phone = "9" + String.format("%09d", 100000000 + rand.nextInt(900000000));
            String addr = "Sector " + (rand.nextInt(50) + 1) + ", Academic Zone";

            double att = 50.0 + rand.nextDouble() * 50.0;
            att = Math.round(att * 10.0) / 10.0;

            double marks = 40.0 + rand.nextDouble() * 60.0;
            marks = Math.round(marks * 10.0) / 10.0;

            double cgpa = Math.min(10.0, (marks / 10.0) + (rand.nextDouble() * 0.6 - 0.3));
            cgpa = Math.round(cgpa * 100.0) / 100.0;

            List<String> skills = new ArrayList<>();
            skills.add(SKILLS_POOL[rand.nextInt(SKILLS_POOL.length)]);
            skills.add(SKILLS_POOL[rand.nextInt(SKILLS_POOL.length)]);
            skills.add(SKILLS_POOL[rand.nextInt(SKILLS_POOL.length)]);

            List<String> projs = List.of(PROJECTS_POOL[rand.nextInt(PROJECTS_POOL.length)]);

            list.add(createStudent(id, roll, name, "2003-01-01", rand.nextBoolean() ? "Male" : "Female",
                    dept, course, yr, sem, sec, email, phone, addr, att, marks, cgpa, cgpa,
                    skills, projs, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "Benchmarking Record"));
        }

        return list;
    }
}
