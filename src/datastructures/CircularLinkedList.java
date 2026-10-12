package datastructures;

import model.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Circular Linked List for Student records.
 * Supports insert, delete, search, and circular traversal.
 */
public class CircularLinkedList implements Serializable {
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
    private Node tail;
    private int size;

    public CircularLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void insert(Student student) {
        Node newNode = new Node(student);
        if (head == null) {
            head = newNode;
            tail = newNode;
            newNode.next = head;
        } else {
            tail.next = newNode;
            tail = newNode;
            tail.next = head;
        }
        size++;
    }

    public boolean deleteById(long studentId) {
        if (head == null) return false;

        Node curr = head;
        Node prev = null;

        do {
            if (curr.data.getStudentId() == studentId) {
                if (curr == head && curr == tail) {
                    head = null;
                    tail = null;
                } else if (curr == head) {
                    head = head.next;
                    tail.next = head;
                } else if (curr == tail) {
                    tail = prev;
                    tail.next = head;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return true;
            }
            prev = curr;
            curr = curr.next;
        } while (curr != head);

        return false;
    }

    public Student searchById(long studentId) {
        if (head == null) return null;
        Node curr = head;
        do {
            if (curr.data.getStudentId() == studentId) {
                return curr.data;
            }
            curr = curr.next;
        } while (curr != head);
        return null;
    }

    public List<Student> toList() {
        List<Student> list = new ArrayList<>();
        if (head == null) return list;
        Node curr = head;
        do {
            list.add(curr.data);
            curr = curr.next;
        } while (curr != head);
        return list;
    }

    public int getSize() { return size; }
    public boolean isEmpty() { return size == 0; }
    public Node getHead() { return head; }
}
