package algorithms.sorting;

import model.Student;

public enum SortCriteria {
    STUDENT_ID, NAME, MARKS, ATTENDANCE, GPA, CGPA;

    public static int compareStudents(Student a, Student b, SortCriteria criteria, boolean ascending) {
        int cmp = 0;
        switch (criteria) {
            case STUDENT_ID:
                cmp = Long.compare(a.getStudentId(), b.getStudentId());
                break;
            case NAME:
                cmp = a.getName().compareToIgnoreCase(b.getName());
                break;
            case MARKS:
                cmp = Double.compare(a.getMarks(), b.getMarks());
                break;
            case ATTENDANCE:
                cmp = Double.compare(a.getAttendance(), b.getAttendance());
                break;
            case GPA:
                cmp = Double.compare(a.getGpa(), b.getGpa());
                break;
            case CGPA:
                cmp = Double.compare(a.getCgpa(), b.getCgpa());
                break;
        }
        return ascending ? cmp : -cmp;
    }
}
