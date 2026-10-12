package services;

import datastructures.*;
import model.*;
import java.util.*;

/**
 * Central Business Logic and Data Structure Orchestrator.
 * Maintains Student records across custom Singly, Doubly, and Circular Linked Lists,
 * BST, AVL Tree, CustomHashTable, Trie, Academic Graph, Undo Stack, and Service Queues.
 */
public class StudentService {

    public enum UndoType {
        ADD, UPDATE, DELETE
    }

    public static class UndoAction {
        public UndoType type;
        public Student studentBefore;
        public Student studentAfter;

        public UndoAction(UndoType type, Student before, Student after) {
            this.type = type;
            this.studentBefore = before != null ? new Student(before) : null;
            this.studentAfter = after != null ? new Student(after) : null;
        }
    }

    public static class ServiceRequest {
        public String requestId;
        public long studentId;
        public String studentName;
        public String requestType; // Transcript, Certificate, ID Card, Fee Waiver
        public String status;

        public ServiceRequest(String requestId, long studentId, String studentName, String requestType) {
            this.requestId = requestId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.requestType = requestType;
            this.status = "PENDING";
        }

        @Override
        public String toString() {
            return String.format("[%s] Student %d (%s) - Request: %s [%s]",
                    requestId, studentId, studentName, requestType, status);
        }
    }

    // Core In-Memory storage
    private final List<Student> studentList;

    // Custom Data Structures
    private final CustomLinkedList singlyLinkedList;
    private final DoublyLinkedList doublyLinkedList;
    private final CircularLinkedList circularLinkedList;
    private final BinarySearchTree bst;
    private final AVLTree avlTree;
    private final CustomHashTable hashTable;
    private final Trie nameTrie;
    private final Trie skillTrie;
    private final Graph relationshipGraph;

    // Stack & Queue structures
    private final CustomStack<UndoAction> undoStack;
    private final CustomStack<Student> recentlyViewedStack;
    private final CustomQueue<ServiceRequest> serviceQueue;
    private final CustomQueue<Student> admissionQueue;

    // Sub-services
    private final AlgorithmExecutionService executionService;
    private final StudentRecordManagementService recordService;

    // Sample Academic Reference Entities
    private final List<Course> availableCourses;
    private final List<Project> availableProjects;
    private final List<Faculty> availableFaculties;
    private final List<Subject> availableSubjects;

    public StudentService() {
        this.studentList = new ArrayList<>();
        this.singlyLinkedList = new CustomLinkedList();
        this.doublyLinkedList = new DoublyLinkedList();
        this.circularLinkedList = new CircularLinkedList();
        this.bst = new BinarySearchTree();
        this.avlTree = new AVLTree();
        this.hashTable = new CustomHashTable(101);
        this.nameTrie = new Trie();
        this.skillTrie = new Trie();
        this.relationshipGraph = new Graph(200);

        this.undoStack = new CustomStack<>();
        this.recentlyViewedStack = new CustomStack<>(15); // MRU bounded to 15
        this.serviceQueue = new CustomQueue<>();
        this.admissionQueue = new CustomQueue<>();

        this.executionService = new AlgorithmExecutionService();
        this.recordService = new StudentRecordManagementService(this);

        this.availableCourses = initializeCourses();
        this.availableProjects = initializeProjects();
        this.availableFaculties = initializeFaculties();
        this.availableSubjects = initializeSubjects();

        // Seed with 32 initial realistic students
        loadInitialData();
    }

    private void loadInitialData() {
        List<Student> sample = SampleDataGenerator.generateInitialStudents();
        for (Student s : sample) {
            addStudentInternal(s, false);
        }
        java.io.File csvFile = new java.io.File("data/students.csv");
        if (!csvFile.exists()) {
            file.StudentFileManager.saveToCSV(this.studentList, "data/students.csv");
        }
    }

    private List<Course> initializeCourses() {
        List<Course> list = new ArrayList<>();
        list.add(new Course("CS301", "Advanced Algorithms", 10, 4, "CSE"));
        list.add(new Course("CS302", "Distributed Systems", 8, 4, "CSE"));
        list.add(new Course("AI201", "Deep Learning Foundations", 12, 4, "AIML"));
        list.add(new Course("IT401", "Cloud Native Computing", 10, 3, "IT"));
        list.add(new Course("EC301", "Embedded IoT Systems", 6, 4, "ECE"));
        list.add(new Course("DS201", "Big Data Analytics", 8, 3, "DS"));
        list.add(new Course("GEN101", "Technical Communication", 25, 2, "GEN"));
        return list;
    }

    private List<Project> initializeProjects() {
        List<Project> list = new ArrayList<>();
        list.add(new Project("PRJ-01", "Intelligent Attendance & Facial Verification", 40, 95, List.of("Java", "Data Structures", "SQL"), 4));
        list.add(new Project("PRJ-02", "Autonomous Vision Navigation", 60, 90, List.of("Python", "Machine Learning", "C++"), 3));
        list.add(new Project("PRJ-03", "Microservices Enterprise Ledger", 50, 85, List.of("Java", "Spring Boot", "Docker"), 4));
        list.add(new Project("PRJ-04", "Medical Diagnostics Classification", 45, 92, List.of("Python", "Machine Learning"), 3));
        list.add(new Project("PRJ-05", "Campus IoT Smart Grid", 35, 80, List.of("C++", "Cybersecurity"), 3));
        list.add(new Project("PRJ-06", "Distributed Raft Key-Value Store", 55, 96, List.of("Java", "Algorithms", "Data Structures"), 4));
        return list;
    }

    private List<Faculty> initializeFaculties() {
        List<Faculty> list = new ArrayList<>();
        list.add(new Faculty("FAC-01", "Dr. Ramesh Sharma", "CSE", List.of("CS301", "Algorithms", "Data Structures"), 2));
        list.add(new Faculty("FAC-02", "Dr. Sunita Varma", "AIML", List.of("AI201", "Machine Learning", "Python"), 2));
        list.add(new Faculty("FAC-03", "Prof. Amit Patel", "IT", List.of("IT401", "Cloud Computing", "Java"), 2));
        list.add(new Faculty("FAC-04", "Dr. Rajesh Iyer", "ECE", List.of("EC301", "Cybersecurity", "C++"), 2));
        return list;
    }

    private List<Subject> initializeSubjects() {
        List<Subject> list = new ArrayList<>();
        list.add(new Subject("CS301", "Data Structures & Algorithms", 4, "Algorithms", 40));
        list.add(new Subject("AI201", "Machine Learning Systems", 4, "Machine Learning", 30));
        list.add(new Subject("IT401", "Cloud Architecture & DevOps", 3, "Cloud Computing", 35));
        list.add(new Subject("EC301", "Embedded Systems & Security", 4, "Cybersecurity", 25));
        return list;
    }

    // ================= CRUD OPERATIONS =================

    public synchronized boolean addStudent(Student student) {
        if (student == null) return false;
        if (getStudentById(student.getStudentId()) != null) {
            return false; // Duplicate ID prevented
        }
        addStudentInternal(student, true);
        return true;
    }

    private void addStudentInternal(Student student, boolean recordUndo) {
        studentList.add(student);
        singlyLinkedList.insertTail(student);
        doublyLinkedList.insertTail(student);
        circularLinkedList.insert(student);
        bst.insert(student);
        avlTree.insert(student);
        hashTable.insertChaining(String.valueOf(student.getStudentId()), student);
        hashTable.insertChaining(student.getRollNumber(), student);
        hashTable.insertChaining(student.getEmail(), student);
        nameTrie.insert(student.getName(), student);
        for (String skill : student.getSkills()) {
            skillTrie.insert(skill, student);
        }

        // Add to graph
        String sId = "S_" + student.getStudentId();
        relationshipGraph.addVertex(sId, student.getName(), Graph.VertexType.STUDENT);
        String deptId = "DEPT_" + student.getDepartment();
        relationshipGraph.addVertex(deptId, student.getDepartment(), Graph.VertexType.SUBJECT);
        relationshipGraph.addEdge(sId, deptId, 1.0, "MEMBER_OF", true);

        for (String skill : student.getSkills()) {
            String skId = "SKILL_" + skill.toUpperCase();
            relationshipGraph.addVertex(skId, skill, Graph.VertexType.SKILL);
            relationshipGraph.addEdge(sId, skId, 2.0, "POSSESSES_SKILL", true);
        }

        if (recordUndo) {
            undoStack.push(new UndoAction(UndoType.ADD, null, student));
        }
    }

    public synchronized boolean updateStudent(Student updated) {
        if (updated == null) return false;
        for (int i = 0; i < studentList.size(); i++) {
            Student curr = studentList.get(i);
            if (curr.getStudentId() == updated.getStudentId()) {
                undoStack.push(new UndoAction(UndoType.UPDATE, curr, updated));
                studentList.set(i, updated);
                singlyLinkedList.update(updated);
                bst.insert(updated);
                avlTree.insert(updated);
                hashTable.insertChaining(String.valueOf(updated.getStudentId()), updated);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteStudent(long studentId) {
        Student toDelete = getStudentById(studentId);
        if (toDelete == null) return false;

        undoStack.push(new UndoAction(UndoType.DELETE, toDelete, null));

        studentList.removeIf(s -> s.getStudentId() == studentId);
        singlyLinkedList.deleteById(studentId);
        doublyLinkedList.deleteById(studentId);
        circularLinkedList.deleteById(studentId);
        bst.delete(studentId);
        avlTree.delete(studentId);
        hashTable.deleteChaining(String.valueOf(studentId));
        hashTable.deleteChaining(toDelete.getRollNumber());
        hashTable.deleteChaining(toDelete.getEmail());

        return true;
    }

    /**
     * Reverts the most recent modifying operation (ADD, UPDATE, or DELETE) using CustomStack.
     */
    public synchronized boolean undoLastOperation() {
        if (undoStack.isEmpty()) {
            return false;
        }

        UndoAction action = undoStack.pop();
        switch (action.type) {
            case ADD:
                // Delete the added student
                if (action.studentAfter != null) {
                    long id = action.studentAfter.getStudentId();
                    studentList.removeIf(s -> s.getStudentId() == id);
                    singlyLinkedList.deleteById(id);
                    doublyLinkedList.deleteById(id);
                    circularLinkedList.deleteById(id);
                    bst.delete(id);
                    avlTree.delete(id);
                    hashTable.deleteChaining(String.valueOf(id));
                }
                return true;

            case DELETE:
                // Restore the deleted student
                if (action.studentBefore != null) {
                    addStudentInternal(action.studentBefore, false);
                }
                return true;

            case UPDATE:
                // Restore previous state
                if (action.studentBefore != null) {
                    long id = action.studentBefore.getStudentId();
                    for (int i = 0; i < studentList.size(); i++) {
                        if (studentList.get(i).getStudentId() == id) {
                            studentList.set(i, action.studentBefore);
                            break;
                        }
                    }
                    bst.insert(action.studentBefore);
                    avlTree.insert(action.studentBefore);
                    hashTable.insertChaining(String.valueOf(id), action.studentBefore);
                }
                return true;
        }
        return false;
    }

    public Student getStudentById(long studentId) {
        for (Student s : studentList) {
            if (s.getStudentId() == studentId) {
                recentlyViewedStack.push(s);
                return s;
            }
        }
        return null;
    }

    public Student getStudentByRoll(String rollNumber) {
        if (rollNumber == null) return null;
        for (Student s : studentList) {
            if (s.getRollNumber().equalsIgnoreCase(rollNumber.trim())) {
                recentlyViewedStack.push(s);
                return s;
            }
        }
        return null;
    }

    // ================= FILTERING =================

    public List<Student> filterStudents(String department, String course, Integer year,
                                        Double minCgpa, Double minAttendance, String grade) {
        List<Student> filtered = new ArrayList<>();
        for (Student s : studentList) {
            if (department != null && !department.equalsIgnoreCase("ALL") && !s.getDepartment().equalsIgnoreCase(department)) continue;
            if (course != null && !course.equalsIgnoreCase("ALL") && !s.getCourse().equalsIgnoreCase(course)) continue;
            if (year != null && year > 0 && s.getYear() != year) continue;
            if (minCgpa != null && s.getCgpa() < minCgpa) continue;
            if (minAttendance != null && s.getAttendance() < minAttendance) continue;
            if (grade != null && !grade.equalsIgnoreCase("ALL") && !s.getGrade().equalsIgnoreCase(grade)) continue;
            filtered.add(s);
        }
        return filtered;
    }

    // ================= QUEUE SERVICES =================

    public void submitServiceRequest(long studentId, String requestType) {
        Student s = getStudentById(studentId);
        String name = (s != null) ? s.getName() : "Unknown";
        String reqId = "REQ-" + (serviceQueue.getSize() + 101);
        serviceQueue.enqueue(new ServiceRequest(reqId, studentId, name, requestType));
    }

    public ServiceRequest processNextServiceRequest() {
        if (serviceQueue.isEmpty()) return null;
        ServiceRequest req = serviceQueue.dequeue();
        req.status = "COMPLETED";
        return req;
    }

    // Getters
    public List<Student> getAllStudents() { return Collections.unmodifiableList(studentList); }
    public CustomLinkedList getSinglyLinkedList() { return singlyLinkedList; }
    public DoublyLinkedList getDoublyLinkedList() { return doublyLinkedList; }
    public CircularLinkedList getCircularLinkedList() { return circularLinkedList; }
    public BinarySearchTree getBst() { return bst; }
    public AVLTree getAvlTree() { return avlTree; }
    public CustomHashTable getHashTable() { return hashTable; }
    public Trie getNameTrie() { return nameTrie; }
    public Trie getSkillTrie() { return skillTrie; }
    public Graph getRelationshipGraph() { return relationshipGraph; }
    public CustomStack<UndoAction> getUndoStack() { return undoStack; }
    public CustomStack<Student> getRecentlyViewedStack() { return recentlyViewedStack; }
    public CustomQueue<ServiceRequest> getServiceQueue() { return serviceQueue; }
    public CustomQueue<Student> getAdmissionQueue() { return admissionQueue; }
    public AlgorithmExecutionService getExecutionService() { return executionService; }
    public List<Course> getAvailableCourses() { return availableCourses; }
    public List<Project> getAvailableProjects() { return availableProjects; }
    public List<Faculty> getAvailableFaculties() { return availableFaculties; }
    public List<Subject> getAvailableSubjects() { return availableSubjects; }
    public StudentRecordManagementService getRecordService() { return recordService; }
    public int getTotalCount() { return studentList.size(); }
}
