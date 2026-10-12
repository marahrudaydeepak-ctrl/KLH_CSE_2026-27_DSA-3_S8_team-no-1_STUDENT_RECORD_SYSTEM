package datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Custom Generic Stack implementation based on linked nodes.
 * Used for Undo operations, Recently Viewed records, and Execution History.
 */
public class CustomStack<T> implements Serializable {
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

    private Node<T> top;
    private int size;
    private final int capacity; // -1 for unbounded

    public CustomStack() {
        this(-1);
    }

    public CustomStack(int capacity) {
        this.top = null;
        this.size = 0;
        this.capacity = capacity;
    }

    public void push(T item) {
        if (capacity > 0 && size >= capacity) {
            // Drop oldest element from bottom to maintain bounded capacity (e.g. for recently viewed)
            removeBottom();
        }
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    private void removeBottom() {
        if (top == null) return;
        if (top.next == null) {
            top = null;
            size = 0;
            return;
        }
        Node<T> curr = top;
        while (curr.next.next != null) {
            curr = curr.next;
        }
        curr.next = null;
        size--;
    }

    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T item = top.data;
        top = top.next;
        size--;
        return item;
    }

    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> curr = top;
        while (curr != null) {
            list.add(curr.data);
            curr = curr.next;
        }
        return list;
    }
}
