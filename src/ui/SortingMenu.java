package ui;

import algorithms.parallel.ParallelSort;
import algorithms.randomized.RandomizedQuickSort;
import algorithms.sorting.*;
import model.AlgorithmResult;
import model.Student;
import services.StudentService;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for the Advanced Sorting Engine.
 * Supports Bubble, Selection, Insertion, Merge, Quick, Heap, Randomized QuickSort,
 * Parallel MergeSort, and Comprehensive Benchmark Comparisons.
 */
public class SortingMenu {

    private final StudentService studentService;
    private final Scanner scanner;

    public SortingMenu(StudentService studentService, Scanner scanner) {
        this.studentService = studentService;
        this.scanner = scanner;
    }

    public void display() {
        while (true) {
            System.out.println("\n====================================================");
            System.out.println("               ADVANCED SORTING MENU                ");
            System.out.println("====================================================");
            System.out.println("1. Bubble Sort");
            System.out.println("2. Selection Sort");
            System.out.println("3. Insertion Sort");
            System.out.println("4. Merge Sort (Stable Divide-and-Conquer)");
            System.out.println("5. Quick Sort (In-Place Partitioning)");
            System.out.println("6. Heap Sort (Binary Heap Transformation)");
            System.out.println("7. Randomized Quick Sort (Uniform Random Pivot)");
            System.out.println("8. Parallel ForkJoin Merge Sort (Multi-Threaded)");
            System.out.println("9. Compare All Sorting Algorithms (Benchmark)");
            System.out.println("10. Back to Main Menu");
            System.out.print("Enter your choice (1-10): ");

            String input = scanner.nextLine().trim();
            if (input.equals("10")) return;
            if (input.equals("9")) {
                handleCompare();
                continue;
            }

            SortCriteria criteria = promptSortCriteria();
            boolean ascending = promptSortOrder();
            List<Student> students = studentService.getAllStudents();

            AlgorithmResult result = null;
            switch (input) {
                case "1": result = BubbleSort.sort(students, criteria, ascending); break;
                case "2": result = SelectionSort.sort(students, criteria, ascending); break;
                case "3": result = InsertionSort.sort(students, criteria, ascending); break;
                case "4": result = MergeSort.sort(students, criteria, ascending); break;
                case "5": result = QuickSort.sort(students, criteria, ascending); break;
                case "6": result = HeapSort.sort(students, criteria, ascending); break;
                case "7": result = RandomizedQuickSort.sort(students, criteria, ascending, 42L); break;
                case "8": result = ParallelSort.executeParallelSort(students, criteria, ascending); break;
                default:
                    System.out.println("Invalid selection.");
                    continue;
            }

            if (result != null) {
                studentService.getExecutionService().recordExecution(result);
                System.out.println("\n" + result.toFormattedDisplay());

                @SuppressWarnings("unchecked")
                List<Student> sortedList = (List<Student>) result.getOutput();
                System.out.println("\n--- TOP 10 SORTED RECORDS ---");
                for (int i = 0; i < Math.min(10, sortedList.size()); i++) {
                    System.out.printf("%2d. %s\n", i + 1, sortedList.get(i));
                }
            }
        }
    }

    private SortCriteria promptSortCriteria() {
        System.out.println("\nSelect Sorting Field:");
        System.out.println("1. Student ID");
        System.out.println("2. Name");
        System.out.println("3. Marks");
        System.out.println("4. Attendance");
        System.out.println("5. GPA");
        System.out.println("6. CGPA");
        System.out.print("Field Choice (1-6) [Default: 6 (CGPA)]: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": return SortCriteria.STUDENT_ID;
            case "2": return SortCriteria.NAME;
            case "3": return SortCriteria.MARKS;
            case "4": return SortCriteria.ATTENDANCE;
            case "5": return SortCriteria.GPA;
            case "6":
            default: return SortCriteria.CGPA;
        }
    }

    private boolean promptSortOrder() {
        System.out.print("Order (1 for Descending / Highest First, 2 for Ascending) [Default: 1]: ");
        String ord = scanner.nextLine().trim();
        return "2".equals(ord);
    }

    private void handleCompare() {
        SortCriteria criteria = promptSortCriteria();
        System.out.println("\nRunning side-by-side benchmark across sorting algorithms...");
        String table = studentService.getExecutionService().compareSortingAlgorithms(studentService.getAllStudents(), criteria);
        System.out.println("\n" + table);
    }
}
