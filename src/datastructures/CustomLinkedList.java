package datastructures;

import model.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Singly Linked List for Student records.
 * Supports insert, delete, search, reverse, update, and cycle detection.
 */
public class CustomLinkedList implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        public Student data;
        public Node next;

        public Node(Student data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public CustomLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public void insertHead(Student student) {
        Node newNode = new Node(student);
        newNode.next = head;
        head = newNode;
        size++;
    }

    public void insertTail(Student student) {
        Node newNode = new Node(student);
        if (head == null) {
            head = newNode;
        } else {
            Node curr = head;
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newNode;
        }
        size++;
    }

    public boolean deleteById(long studentId) {
        if (head == null) return false;

        if (head.data.getStudentId() == studentId) {
            head = head.next;
            size--;
            return true;
        }

        Node prev = head;
        Node curr = head.next;
        while (curr != null) {
            if (curr.data.getStudentId() == studentId) {
                prev.next = curr.next;
                size--;
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    public Student searchById(long studentId) {
        Node curr = head;
        while (curr != null) {
            if (curr.data.getStudentId() == studentId) {
                return curr.data;
            }
            curr = curr.next;
        }
        return null;
    }

    public boolean update(Student updated) {
        Node curr = head;
        while (curr != null) {
            if (curr.data.getStudentId() == updated.getStudentId()) {
                curr.data = updated;
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    /**
     * In-place iterative reversal of singly linked list.
     */
    public void reverse() {
        Node prev = null;
        Node curr = head;
        Node nextNode;
        while (curr != null) {
            nextNode = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nextNode;
        }
        head = prev;
    }

    /**
     * Floyd's Cycle Detection Algorithm (Tortoise and Hare).
     */
    public boolean detectCycle() {
        if (head == null) return false;
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    public List<Student> toList() {
        List<Student> list = new ArrayList<>();
        Node curr = head;
        int count = 0;
        // Safeguard in case of cycle
        while (curr != null && count < size + 10) {
            list.add(curr.data);
            curr = curr.next;
            count++;
        }
        return list;
    }

    public int getSize() { return size; }
    public boolean isEmpty() { return size == 0; }
    public Node getHead() { return head; }
}
