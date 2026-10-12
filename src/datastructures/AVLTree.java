package datastructures;

import model.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Self-balancing AVL Tree indexed by Student ID.
 * Implements LL, RR, LR, and RL rotations with complete event logging and height maintenance.
 */
public class AVLTree implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class AVLNode implements Serializable {
        private static final long serialVersionUID = 1L;
        public Student student;
        public int height;
        public AVLNode left;
        public AVLNode right;

        public AVLNode(Student student) {
            this.student = student;
            this.height = 1;
            this.left = null;
            this.right = null;
        }

        public long getKey() {
            return student.getStudentId();
        }
    }

    public static class RotationEvent {
        public String type; // LL, RR, LR, RL
        public long nodeKey;
        public String beforeState;
        public String afterState;

        public RotationEvent(String type, long nodeKey, String beforeState, String afterState) {
            this.type = type;
            this.nodeKey = nodeKey;
            this.beforeState = beforeState;
            this.afterState = afterState;
        }

        @Override
        public String toString() {
            return String.format("Rotation: %s | Node Key: %d | Complexity: O(log n)\n  Before: %s\n  After:  %s",
                    type, nodeKey, beforeState, afterState);
        }
    }

    private AVLNode root;
    private int size;
    private List<RotationEvent> rotationLogs;

    public AVLTree() {
        this.root = null;
        this.size = 0;
        this.rotationLogs = new ArrayList<>();
    }

    public int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    public int getBalanceFactor(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(AVLNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    /**
     * Right Rotation (LL Case).
     */
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode t2 = x.right;

        String before = String.format("Root=%d (BF=%d), LeftChild=%d", y.getKey(), getBalanceFactor(y), x.getKey());

        x.right = y;
        y.left = t2;

        updateHeight(y);
        updateHeight(x);

        String after = String.format("NewRoot=%d, RightChild=%d", x.getKey(), y.getKey());
        rotationLogs.add(new RotationEvent("LL (Right Rotate)", y.getKey(), before, after));

        return x;
    }

    /**
     * Left Rotation (RR Case).
     */
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode t2 = y.left;

        String before = String.format("Root=%d (BF=%d), RightChild=%d", x.getKey(), getBalanceFactor(x), y.getKey());

        y.left = x;
        x.right = t2;

        updateHeight(x);
        updateHeight(y);

        String after = String.format("NewRoot=%d, LeftChild=%d", y.getKey(), x.getKey());
        rotationLogs.add(new RotationEvent("RR (Left Rotate)", x.getKey(), before, after));

        return y;
    }

    public void insert(Student student) {
        if (student == null) return;
        root = insertRec(root, student);
    }

    private AVLNode insertRec(AVLNode node, Student student) {
        if (node == null) {
            size++;
            return new AVLNode(student);
        }

        if (student.getStudentId() < node.getKey()) {
            node.left = insertRec(node.left, student);
        } else if (student.getStudentId() > node.getKey()) {
            node.right = insertRec(node.right, student);
        } else {
            node.student = student; // Update existing
            return node;
        }

        updateHeight(node);
        int balance = getBalanceFactor(node);

        // LL Case
        if (balance > 1 && student.getStudentId() < node.left.getKey()) {
            return rightRotate(node);
        }

        // RR Case
        if (balance < -1 && student.getStudentId() > node.right.getKey()) {
            return leftRotate(node);
        }

        // LR Case
        if (balance > 1 && student.getStudentId() > node.left.getKey()) {
            rotationLogs.add(new RotationEvent("LR (Left-Right)", node.getKey(),
                    "Pre-LR: Left child requires left rotation first", "Executing LR sequence"));
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RL Case
        if (balance < -1 && student.getStudentId() < node.right.getKey()) {
            rotationLogs.add(new RotationEvent("RL (Right-Left)", node.getKey(),
                    "Pre-RL: Right child requires right rotation first", "Executing RL sequence"));
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public boolean delete(long studentId) {
        int oldSize = size;
        root = deleteRec(root, studentId);
        return size < oldSize;
    }

    private AVLNode deleteRec(AVLNode node, long studentId) {
        if (node == null) return null;

        if (studentId < node.getKey()) {
            node.left = deleteRec(node.left, studentId);
        } else if (studentId > node.getKey()) {
            node.right = deleteRec(node.right, studentId);
        } else {
            size--;
            if (node.left == null || node.right == null) {
                AVLNode temp = (node.left != null) ? node.left : node.right;
                if (temp == null) {
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                AVLNode temp = findMin(node.right);
                node.student = temp.student;
                node.right = deleteRec(node.right, temp.getKey());
                size++; // Compensate recursion decrement
            }
        }

        if (node == null) return null;

        updateHeight(node);
        int balance = getBalanceFactor(node);

        // LL Case
        if (balance > 1 && getBalanceFactor(node.left) >= 0) {
            return rightRotate(node);
        }

        // LR Case
        if (balance > 1 && getBalanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RR Case
        if (balance < -1 && getBalanceFactor(node.right) <= 0) {
            return leftRotate(node);
        }

        // RL Case
        if (balance < -1 && getBalanceFactor(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode findMin(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public static class AVLSearchResult {
        public Student student;
        public int comparisons;
        public boolean found;
    }

    public AVLSearchResult search(long studentId) {
        AVLSearchResult res = new AVLSearchResult();
        AVLNode curr = root;
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

    public List<Student> inorderTraversal() {
        List<Student> list = new ArrayList<>();
        inorderRec(root, list);
        return list;
    }

    private void inorderRec(AVLNode node, List<Student> list) {
        if (node != null) {
            inorderRec(node.left, list);
            list.add(node.student);
            inorderRec(node.right, list);
        }
    }

    public int getSize() { return size; }
    public AVLNode getRoot() { return root; }
    public List<RotationEvent> getRotationLogs() { return rotationLogs; }
    public void clearRotationLogs() { rotationLogs.clear(); }

    public String printTreeVisual() {
        StringBuilder sb = new StringBuilder();
        printTreeHelper(root, "", true, sb);
        return sb.toString();
    }

    private void printTreeHelper(AVLNode node, String prefix, boolean isTail, StringBuilder sb) {
        if (node != null) {
            sb.append(prefix).append(isTail ? "└── " : "├── ")
              .append(node.getKey()).append(" [BF=").append(getBalanceFactor(node)).append(", H=").append(node.height).append("] (")
              .append(node.student.getName()).append(")\n");
            List<AVLNode> children = new ArrayList<>();
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
