package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model managing Student Academic Fee ledger, payment tracking,
 * receipt issuance, and No-Dues Clearance certificates.
 */
public class FeeRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private long studentId;
    private String rollNumber;
    private String studentName;
    private String department;
    private int academicYear;
    private int semester;

    private double tuitionFee;
    private double labFee;
    private double libraryFee;
    private double examFee;
    private double amenitiesFee;
    private double scholarshipDiscount;
    private double totalFee;
    private double amountPaid;
    private double balanceDue;
    private String paymentStatus; // PAID, PARTIAL, DUE

    private String lastReceiptNumber;
    private String lastPaymentDate;
    private String lastPaymentMethod;

    public FeeRecord() {}

    public FeeRecord(Student s) {
        this.studentId = s.getStudentId();
        this.rollNumber = s.getRollNumber();
        this.studentName = s.getName();
        this.department = s.getDepartment();
        this.academicYear = s.getYear();
        this.semester = s.getSemester();

        // Baseline fees
        this.tuitionFee = 75000.0;
        this.labFee = 15000.0;
        this.libraryFee = 5000.0;
        this.examFee = 3500.0;
        this.amenitiesFee = 6500.0;

        // Merit scholarship discount if CGPA >= 9.0
        if (s.getCgpa() >= 9.0) {
            this.scholarshipDiscount = 25000.0; // Merit scholarship
        } else if (s.getCgpa() >= 8.5) {
            this.scholarshipDiscount = 15000.0;
        } else {
            this.scholarshipDiscount = 0.0;
        }

        double gross = tuitionFee + labFee + libraryFee + examFee + amenitiesFee;
        this.totalFee = gross - this.scholarshipDiscount;

        // Realistic deterministic initial payment status based on student ID
        int mod = (int) (Math.abs(s.getStudentId()) % 10);
        if (mod < 6) {
            // Fully paid
            this.amountPaid = this.totalFee;
            this.balanceDue = 0.0;
            this.paymentStatus = "PAID";
            this.lastReceiptNumber = String.format("RCP-%d-%04d", LocalDate.now().getYear(), Math.abs(s.getStudentId() % 9000) + 1000);
            this.lastPaymentDate = LocalDate.now().minusDays(15).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
            this.lastPaymentMethod = "Online NetBanking";
        } else if (mod < 9) {
            // Partial
            this.amountPaid = 50000.0;
            this.balanceDue = this.totalFee - this.amountPaid;
            this.paymentStatus = "PARTIAL";
            this.lastReceiptNumber = String.format("RCP-%d-%04d", LocalDate.now().getYear(), Math.abs(s.getStudentId() % 9000) + 1000);
            this.lastPaymentDate = LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
            this.lastPaymentMethod = "UPI Gateway";
        } else {
            // Due
            this.amountPaid = 0.0;
            this.balanceDue = this.totalFee;
            this.paymentStatus = "DUE";
            this.lastReceiptNumber = "N/A";
            this.lastPaymentDate = "N/A";
            this.lastPaymentMethod = "None";
        }
    }

    public synchronized void recordPayment(double amount, String method) {
        if (amount <= 0) return;
        this.amountPaid += amount;
        if (this.amountPaid >= this.totalFee) {
            this.amountPaid = this.totalFee;
            this.balanceDue = 0.0;
            this.paymentStatus = "PAID";
        } else {
            this.balanceDue = this.totalFee - this.amountPaid;
            this.paymentStatus = "PARTIAL";
        }
        this.lastPaymentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        this.lastReceiptNumber = String.format("RCP-%d-%04d", LocalDate.now().getYear(), (int) (System.currentTimeMillis() % 9000) + 1000);
        this.lastPaymentMethod = (method != null && !method.trim().isEmpty()) ? method.trim() : "Online Payment";
    }

    public String toFormattedReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          \n");
        sb.append("                                   OFFICE OF FINANCE & ACCOUNTS                                    \n");
        sb.append("                                    OFFICIAL FEE PAYMENT RECEIPT                                   \n");
        sb.append("====================================================================================================\n");
        sb.append(String.format(" Receipt Number: %-30s | Date of Payment: %s\n", lastReceiptNumber, lastPaymentDate));
        sb.append(String.format(" Student Name  : %-30s | Roll Number    : %s\n", studentName, rollNumber));
        sb.append(String.format(" Department    : %-30s | Academic Year  : Year %d (Sem %d)\n", department, academicYear, semester));
        sb.append(String.format(" Payment Mode  : %-30s | Overall Status : %s\n", lastPaymentMethod, paymentStatus));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" %-4s | %-50s | %15s\n", "S.No", "FEE HEAD DESCRIPTION", "AMOUNT (INR)"));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("  1.  | %-50s | %,15.2f\n", "Tuition & Instruction Fee (Per Semester)", tuitionFee));
        sb.append(String.format("  2.  | %-50s | %,15.2f\n", "Computing & Specialized Lab Equipment Fee", labFee));
        sb.append(String.format("  3.  | %-50s | %,15.2f\n", "Digital Library & Journal Subscriptions Fee", libraryFee));
        sb.append(String.format("  4.  | %-50s | %,15.2f\n", "End-Semester Autonomous Examination Fee", examFee));
        sb.append(String.format("  5.  | %-50s | %,15.2f\n", "Campus Amenities, Sports & Healthcare Fund", amenitiesFee));
        if (scholarshipDiscount > 0) {
            sb.append(String.format("  *   | %-50s | -%,14.2f\n", "LESS: Institutional Merit Scholarship Award", scholarshipDiscount));
        }
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" TOTAL NET APPLICABLE FEE : %,15.2f\n", totalFee));
        sb.append(String.format(" TOTAL AMOUNT PAID TO DATE: %,15.2f\n", amountPaid));
        sb.append(String.format(" REMAINING BALANCE DUE    : %,15.2f\n", balanceDue));
        sb.append("====================================================================================================\n");
        sb.append(" Status: ").append(paymentStatus.equals("PAID") ? "ALL DUES CLEARED FOR CURRENT SEMESTER" : "OUTSTANDING DUES PENDING").append("\n\n");
        sb.append("     [Computer Generated Document - No Physical Signature Required]         [Bursar / Accounts Officer]\n");
        sb.append("====================================================================================================\n");
        return sb.toString();
    }

    public String toFormattedNoDuesCertificate() {
        StringBuilder sb = new StringBuilder();
        sb.append("****************************************************************************************************\n");
        sb.append("*                          APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          *\n");
        sb.append("*                               NO DUES & FINANCIAL CLEARANCE CERTIFICATE                          *\n");
        sb.append("****************************************************************************************************\n\n");
        sb.append(String.format(" Date: %s | Certificate ID: ND-CLR-%d-%04d\n\n",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")),
                LocalDate.now().getYear(), Math.abs(studentId % 9000) + 1000));
        sb.append(" This is to certify that ").append(studentName.toUpperCase()).append(" (Roll No: ").append(rollNumber).append("),\n");
        sb.append(" Department of ").append(department).append(", has fulfilled all financial obligations for the academic period.\n\n");
        sb.append(" Clearance Summary:\n");
        sb.append(String.format("   • Total Prescribed Fee : INR %,.2f\n", totalFee));
        sb.append(String.format("   • Total Paid Amount    : INR %,.2f\n", amountPaid));
        sb.append(String.format("   • Outstanding Balance  : INR %,.2f [%s]\n\n", balanceDue, balanceDue == 0 ? "CLEAR" : "PENDING"));
        if (balanceDue == 0) {
            sb.append(" The student holds NO FINANCIAL DUES towards the Institute Accounts, Library, Laboratories, or Hostels.\n");
            sb.append(" Approved for Semester Registration, Hall Ticket Issuance, or Degree Conferment.\n\n");
        } else {
            sb.append(" ATTENTION: Clearance CANNOT be granted until the outstanding balance of INR ").append(String.format("%,.2f", balanceDue)).append(" is cleared.\n\n");
        }
        sb.append("   [Accounts Officer]                          [Librarian]                        [Dean of Students]\n");
        sb.append("****************************************************************************************************\n");
        return sb.toString();
    }

    // Getters and Setters
    public long getStudentId() { return studentId; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public int getAcademicYear() { return academicYear; }
    public int getSemester() { return semester; }
    public double getTuitionFee() { return tuitionFee; }
    public double getLabFee() { return labFee; }
    public double getLibraryFee() { return libraryFee; }
    public double getExamFee() { return examFee; }
    public double getAmenitiesFee() { return amenitiesFee; }
    public double getScholarshipDiscount() { return scholarshipDiscount; }
    public double getTotalFee() { return totalFee; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalanceDue() { return balanceDue; }
    public boolean isCleared() { return balanceDue <= 0.0 || "PAID".equalsIgnoreCase(paymentStatus); }
    public String getPaymentStatus() { return paymentStatus; }
    public String getLastReceiptNumber() { return lastReceiptNumber; }
    public String getLastPaymentDate() { return lastPaymentDate; }
    public String getLastPaymentMethod() { return lastPaymentMethod; }

    @Override
    public String toString() {
        return String.format("[Fee: %s | Total: %,.2f | Paid: %,.2f | Bal: %,.2f | %s]",
                rollNumber, totalFee, amountPaid, balanceDue, paymentStatus);
    }
}
