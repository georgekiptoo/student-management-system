
package database;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Handles all database operations for the student app
// Uses SQLite via JDBC
public class DatabaseManager {

    private static final String DB_FILE = "students.db";
    private Connection conn;

    // connect to the database and create the table if it doesn't exist
    public boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            createTableIfNeeded();
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite driver not found. Make sure sqlite-jdbc jar is in classpath.");
            return false;
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    private void createTableIfNeeded() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                "id      INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name    TEXT NOT NULL," +
                "age     INTEGER," +
                "grade   TEXT," +
                "subject TEXT," +
                "score   REAL," +
                "email   TEXT)";
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing database: " + e.getMessage());
        }
    }

    // check if the table has any rows
    public boolean isEmpty() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
        boolean empty = rs.next() && rs.getInt(1) == 0;
        rs.close();
        stmt.close();
        return empty;
    }

    // INSERT a new student
    public void addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (name, age, grade, subject, score, email) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, s.getName());
        ps.setInt   (2, s.getAge());
        ps.setString(3, s.getGrade());
        ps.setString(4, s.getSubject());
        ps.setDouble(5, s.getScore());
        ps.setString(6, s.getEmail());
        ps.executeUpdate();
        ps.close();
    }

    // UPDATE an existing student by id
    public void updateStudent(Student s) throws SQLException {
        String sql = "UPDATE students SET name=?, age=?, grade=?, subject=?, score=?, email=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, s.getName());
        ps.setInt   (2, s.getAge());
        ps.setString(3, s.getGrade());
        ps.setString(4, s.getSubject());
        ps.setDouble(5, s.getScore());
        ps.setString(6, s.getEmail());
        ps.setInt   (7, s.getId());
        ps.executeUpdate();
        ps.close();
    }

    // DELETE a student by id
    public void deleteStudent(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    // SELECT students with optional search/filter params
    public List<Student> getStudents(String search, String grade,
                                     String subject, double minScore, double maxScore)
            throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT id, name, age, grade, subject, score, email FROM students WHERE 1=1");

        if (search != null && !search.isEmpty())
            sql.append(" AND (LOWER(name) LIKE ? OR LOWER(email) LIKE ?)");
        if (grade != null && !grade.equals("All"))
            sql.append(" AND grade = ?");
        if (subject != null && !subject.equals("All"))
            sql.append(" AND subject = ?");

        sql.append(" AND score >= ? AND score <= ?");
        sql.append(" ORDER BY id");

        PreparedStatement ps = conn.prepareStatement(sql.toString());
        int i = 1;

        if (search != null && !search.isEmpty()) {
            ps.setString(i++, "%" + search.toLowerCase() + "%");
            ps.setString(i++, "%" + search.toLowerCase() + "%");
        }
        if (grade != null && !grade.equals("All"))
            ps.setString(i++, grade);
        if (subject != null && !subject.equals("All"))
            ps.setString(i++, subject);

        ps.setDouble(i++, minScore);
        ps.setDouble(i,   maxScore);

        ResultSet rs = ps.executeQuery();
        List<Student> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Student(
                    rs.getInt   ("id"),
                    rs.getString("name"),
                    rs.getInt   ("age"),
                    rs.getString("grade"),
                    rs.getString("subject"),
                    rs.getDouble("score"),
                    rs.getString("email")
            ));
        }
        rs.close();
        ps.close();
        return list;
    }

    // get total number of students in the database
    public int getTotalCount() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
        int count = rs.next() ? rs.getInt(1) : 0;
        rs.close();
        stmt.close();
        return count;
    }

    // get summary stats for the DB Info dialog
    public String getStats() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) as total, AVG(score) as avg, MAX(score) as max, MIN(score) as min FROM students");
        String result = "";
        if (rs.next()) {
            result = String.format(
                    "Database file : %s\n\nTotal students : %d\nAverage score : %.1f\nHighest score : %.1f\nLowest score  : %.1f",
                    DB_FILE, rs.getInt("total"), rs.getDouble("avg"),
                    rs.getDouble("max"), rs.getDouble("min"));
        }
        rs.close();
        stmt.close();
        return result;
    }

    // export all students to a CSV file
    public int exportToCSV(String path) throws Exception {
        List<Student> all = getStudents(null, "All", "All", 0, 100);
        java.io.FileWriter fw = new java.io.FileWriter(path);
        fw.write("id,name,age,grade,subject,score,email\n");
        for (Student s : all) {
            fw.write(s.getId()      + "," +
                    s.getName()    + "," +
                    s.getAge()     + "," +
                    s.getGrade()   + "," +
                    s.getSubject() + "," +
                    s.getScore()   + "," +
                    s.getEmail()   + "\n");
        }
        fw.close();
        return all.size();
    }

    // insert the demo students (called only when DB is empty)
    public void insertDemoData() throws SQLException {
        String[][] demo = {
                {"KIPTOO George", "20", "A", "Mathematics",    "95.5", "kiptoogeorge@gmail.com"},
                {"MOHAMMED Seyyedmohammed",     "19", "B", "Science",         "82.0", "mohammedseyyed@gmail.com"},
                {"MANEA David",   "21", "A", "Computer Science","91.0", "maneadavid@gmail.com"},
                {"ALBU Alexandru",   "18", "C", "History",         "74.0", "albualexandru@gmail.com"},
                {"GEORGESCU Davidoff",  "22", "B", "English",         "88.5", "georgescudavid@gmail.com"},
                {"HOLBEA Dan",     "20", "D", "Geography",       "63.0", "holbeadan@gmail.com"},
                {"MIHAELA Moraru",     "19", "A", "Art",             "97.0", "mihaelamoraru@gmail.com"},
                {"MIHAI Bodgan",   "21", "B", "Mathematics",    "80.5", "mihaibodgan@gmail.com"},
        };
        for (String[] d : demo) {
            Student s = new Student(0, d[0],
                    Integer.parseInt(d[1]), d[2], d[3],
                    Double.parseDouble(d[4]), d[5]);
            addStudent(s);
        }
    }
}
