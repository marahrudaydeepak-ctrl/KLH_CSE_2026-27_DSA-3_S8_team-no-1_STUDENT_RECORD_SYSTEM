package datastructures;

import model.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Binary Search Tree indexed by Student ID.
 * Supports insert, search, delete, traversals (inorder, preorder, postorder),
 * and ASCII tree visualization.
 */
public class BinarySearchTree implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        public Student student;
        public Node left;
        public Node right;

        public Node(Student student) {
            this.student = student;
            this.left = null;
            this.right = null;
        }

        public long getKey() {
            return student.getStudentId();
        }
    }

    private Node root;
    private int size;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    public void insert(Student student) {
        if (student == null) return;
        root = insertRec(root, student);
    }

    private Node insertRec(Node current, Student student) {
        if (current == null) {
            size++;
            return new Node(student);
        }
        if (student.getStudentId() < current.getKey()) {
            current.left = insertRec(current.left, student);
        } else if (student.getStudentId() > current.getKey()) {
            current.right = insertRec(current.right, student);
        } else {
            // Update existing record
            current.student = student;
        }
        return current;
    }

    public static class SearchResult {
        public Student student;
        public int comparisons;
        public boolean found;
    }

    public SearchResult search(long studentId) {
        SearchResult res = new SearchResult();
        Node curr = root;
        while (curr != null) {
            res.comparisons++;
            if (studentId == curr.getKey()) {
                res.found = true;
                res.student = curr.student;
                return res;
            } else if (studentId < curr.getKey()) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
        }
        res.found = false;
        return res;
    }

    public boolean delete(long studentId) {
        int initialSize = size;
        root = deleteRec(root, studentId);
        return size < initialSize;
    }

    private Node deleteRec(Node current, long studentId) {
        if (current == null) return null;

        if (studentId < current.getKey()) {
            current.left = deleteRec(current.left, studentId);
        } else if (studentId > current.getKey()) {
            current.right = deleteRec(current.right, studentId);
        } else {
            // Node found to be deleted
            size--;
            // Case 1 & 2: 0 or 1 child
            if (current.left == null) return current.right;
            if (current.right == null) return current.left;

            // Case 3: 2 children - get inorder successor (smallest in right subtree)
            Node successor = findMin(current.right);
            current.student = successor.student;
            current.right = deleteRec(current.right, successor.getKey());
            size++; // Compensate for double decrement in recursive call
        }
        return current;
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public List<Student> inorderTraversal() {
        List<Student> list = new ArrayList<>();
        inorderRec(root, list);
        return list;
    }

    private void inorderRec(Node node, List<Student> list) {
        if (node != null) {
            inorderRec(node.left, list);
            list.add(node.student);
            inorderRec(node.right, list);
        }
    }

    public List<Student> preorderTraversal() {
        List<Student> list = new ArrayList<>();
        preorderRec(root, list);
        return list;
    }

    private void preorderRec(Node node, List<Student> list) {
        if (node != null) {
            list.add(node.student);
            preorderRec(node.left, list);
            preorderRec(node.right, list);
        }
    }

    public List<Student> postorderTraversal() {
        List<Student> list = new ArrayList<>();
        postorderRec(root, list);
        return list;
    }

    private void postorderRec(Node node, List<Student> list) {
        if (node != null) {
            postorderRec(node.left, list);
            postorderRec(node.right, list);
            list.add(node.student);
        }
    }

    public int getHeight() {
        return heightRec(root);
    }

    private int heightRec(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(heightRec(node.left), heightRec(node.right));
    }

    public int getSize() { return size; }
    public Node getRoot() { return root; }

    public String printTreeVisual() {
        StringBuilder sb = new StringBuilder();
        printTreeHelper(root, "", true, sb);
        return sb.toString();
    }

    private void printTreeHelper(Node node, String prefix, boolean isTail, StringBuilder sb) {
        if (node != null) {
            sb.append(prefix).append(isTail ? "└── " : "├── ")
              .append(node.getKey()).append(" (").append(node.student.getName()).append(")\n");
            List<Node> children = new ArrayList<>();
            if (node.left != null || node.right != null) {
                if (node.left != null) children.add(node.left);
                if (node.right != null) children.add(node.right);
                for (int i = 0; i < children.size() - 1; i++) {
                    printTreeHelper(children.get(i), prefix + (isTail ? "    " : "│   "), false, sb);
                }
                if (!children.isEmpty()) {
                    printTreeHelper(children.get(children.size() - 1), prefix + (isTail ? "    " : "│   "), true, sb);
                }
            }
        }
    }
}
