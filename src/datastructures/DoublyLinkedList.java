package datastructures;

import model.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Doubly Linked List for Student records.
 * Supports forward/backward traversal, insertion at head/tail, deletion, and search.
 */
public class DoublyLinkedList implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        public Student data;
        public Node prev;
        public Node next;

        public Node(Student data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void insertHead(Student student) {
        Node newNode = new Node(student);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    public void insertTail(Student student) {
        Node newNode = new Node(student);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public boolean deleteById(long studentId) {
        Node curr = head;
        while (curr != null) {
            if (curr.data.getStudentId() == studentId) {
                if (curr == head && curr == tail) {
                    head = tail = null;
                } else if (curr == head) {
                    head = head.next;
                    head.prev = null;
                } else if (curr == tail) {
                    tail = tail.prev;
                    tail.next = null;
                } else {
                    curr.prev.next = curr.next;
                    curr.next.prev = curr.prev;
                }
                size--;
                return true;
            }
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

    public List<Student> traverseForward() {
        List<Student> list = new ArrayList<>();
        Node curr = head;
        while (curr != null) {
            list.add(curr.data);
            curr = curr.next;
        }
        return list;
    }

    public List<Student> traverseBackward() {
        List<Student> list = new ArrayList<>();
        Node curr = tail;
        while (curr != null) {
            list.add(curr.data);
            curr = curr.prev;
        }
        return list;
    }

    public void reverse() {
        Node curr = head;
        Node temp = null;
        while (curr != null) {
            temp = curr.prev;
            curr.prev = curr.next;
            curr.next = temp;
            curr = curr.prev;
        }
        if (temp != null) {
            tail = head;
            head = temp.prev;
        }
    }

    public int getSize() { return size; }
    public boolean isEmpty() { return size == 0; }
    public Node getHead() { return head; }
    public Node getTail() { return tail; }
}
