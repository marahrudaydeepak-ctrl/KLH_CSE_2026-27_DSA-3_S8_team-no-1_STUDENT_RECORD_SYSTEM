package datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Custom Max-Heap implementation supporting custom comparators or natural order.
 * Used for Heap Sort, top-performer rankings, and priority processing.
 */
public class MaxHeap<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> heap;
    private Comparator<T> comparator;

    public MaxHeap() {
        this(null);
    }

    public MaxHeap(Comparator<T> comparator) {
        this.heap = new ArrayList<>();
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<T>) a).compareTo(b);
    }

    public void insert(T item) {
        heap.add(item);
        siftUp(heap.size() - 1);
    }

    public T extractMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        T max = heap.get(0);
        T last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }
        return max;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return heap.get(0);
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (compare(heap.get(i), heap.get(parent)) > 0) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int i) {
        int n = heap.size();
        while (2 * i + 1 < n) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int largest = i;

            if (left < n && compare(heap.get(left), heap.get(largest)) > 0) {
                largest = left;
            }
            if (right < n && compare(heap.get(right), heap.get(largest)) > 0) {
                largest = right;
            }

            if (largest != i) {
                swap(i, largest);
                i = largest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    public List<T> toList() {
        return new ArrayList<>(heap);
    }
}
