package datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Custom Generic FIFO Queue implementation based on linked nodes with head and tail pointers.
 * Displays Front, Rear, and Queue size.
 */
public class CustomQueue<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static class Node<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;

    public CustomQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        T item = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return item;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return front.data;
    }

    public T getFront() {
        return isEmpty() ? null : front.data;
    }

    public T getRear() {
        return isEmpty() ? null : rear.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int getSize() {
        return size;
    }

    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> curr = front;
        while (curr != null) {
            list.add(curr.data);
            curr = curr.next;
        }
        return list;
    }

    public String getStatusString() {
        return String.format("[Queue Status: Size=%d | Front=%s | Rear=%s]",
                size,
                front != null ? front.data.toString() : "null",
                rear != null ? rear.data.toString() : "null");
    }
}
