package model;

/**
 * Enumeration of institutional certificates available for generation and printing.
 */
public enum CertificateType {
    BONAFIDE("Bonafide Student Certificate", "Certifies current active enrollment and good standing."),
    COURSE_COMPLETION("Course Completion Certificate", "Certifies successful fulfillment of academic degree curriculum."),
    MERIT_EXCELLENCE("Certificate of Academic Excellence", "Honors top-tier academic performance and dean's list distinction."),
    CHARACTER_CONDUCT("Conduct & Character Certificate", "Certifies exemplary personal conduct, discipline, and moral character."),
    OFFICIAL_TRANSCRIPT("Official Academic Transcript", "Consolidated official verification of academic grades and credits."),
    TRANSFER_LEAVING("Transfer & College Leaving Certificate", "Formal leaving certificate for transfer or higher education."),
    INTERNSHIP_NOC("Internship No-Objection Certificate (NOC)", "Institutional endorsement for external corporate/research internships."),
    ATTENDANCE_WARNING("Attendance Shortage Warning Notice", "Official warning notice for students below the mandatory 75% threshold.");

    private final String displayName;
    private final String description;

    CertificateType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
