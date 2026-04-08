import javax.swing.*;
import database.DatabaseManager;
import ui.StudentManagerUI;

// Entry point for the Student Management System
// George K. - CS2 Final Project - May 2025
//
// HOW TO COMPILE AND RUN:
//   1. Get sqlite-jdbc-3.51.3.0.jar from https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.51.3.0.jar
//   2. Put the jar in the same folder as this file
//
//   Compile (Mac/Linux):
//     javac -cp .:sqlite-jdbc-3.51.3.0.jar -sourcepath src src/Main.java -d out
//   Compile (Windows):
//     javac -cp .;sqlite-jdbc-3.51.3.0.jar -sourcepath src src/Main.java -d out
//
//   Run (Mac/Linux):
//     java -cp out:sqlite-jdbc-3.51.3.0.jar Main
//   Run (Windows):
//     java -cp out;sqlite-jdbc-3.51.3.0.jar Main
//
//   Data is saved automatically in students.db

public class Main {

    public static void main(String[] args) {
        // connect to database before launching UI
        DatabaseManager db = new DatabaseManager();

        if (!db.connect()) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to the database.\n\n" +
                            "Make sure sqlite-jdbc-3.51.3.0.jar is in the same folder\n" +
                            "and that you included it in the classpath when compiling.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // insert demo data on first run
        try {
            if (db.isEmpty()) {
                db.insertDemoData();
            }
        } catch (Exception e) {
            System.out.println("Could not insert demo data: " + e.getMessage());
        }

        // launch the GUI on the event dispatch thread
        SwingUtilities.invokeLater(() -> {
            StudentManagerUI ui = new StudentManagerUI(db);
            ui.setVisible(true);
        });
    }
}
