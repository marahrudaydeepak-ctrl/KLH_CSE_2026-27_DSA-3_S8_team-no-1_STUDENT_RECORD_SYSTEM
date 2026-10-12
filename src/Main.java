import services.StudentService;
import ui.MainMenu;
import ui.SwingApp;

import javax.swing.SwingUtilities;

/**
 * Primary Application Entry Point.
 * Launches either the Full Interactive Console Menu or the Modern Swing GUI.
 */
public class Main {

    public static void main(String[] args) {
        StudentService studentService = new StudentService();

        boolean launchGui = false;
        boolean runTests = false;

        for (String arg : args) {
            if ("--gui".equalsIgnoreCase(arg) || "-g".equalsIgnoreCase(arg)) {
                launchGui = true;
            } else if ("--test".equalsIgnoreCase(arg) || "-t".equalsIgnoreCase(arg)) {
                runTests = true;
            }
        }

        if (runTests) {
            test.ComprehensiveTestSuite.runAllTests(studentService);
            return;
        }

        if (launchGui) {
            launchSwingGUI(studentService);
        } else {
            // Check if user wants GUI or Console if running in interactive terminal
            System.out.println("====================================================================");
            System.out.println(" INTELLIGENT STUDENT RECORD MANAGEMENT & ADVANCED ALGORITHM SYSTEM ");
            System.out.println("====================================================================");
            System.out.println("Starting application in Console Mode...");
            System.out.println("(Tip: Run with '--gui' to directly launch the graphical interface)");
            System.out.println("====================================================================\n");

            MainMenu mainMenu = new MainMenu(studentService);
            mainMenu.start();
        }
    }

    public static void launchSwingGUI(StudentService service) {
        SwingUtilities.invokeLater(() -> {
            SwingApp app = new SwingApp(service);
            app.setVisible(true);
        });
    }
}
