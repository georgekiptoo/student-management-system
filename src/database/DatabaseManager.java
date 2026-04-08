package database;
private Connection conn;

public DatabaseManager() {
    connect();
    createTable();
}

private void connect() {
    try {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection(DB_URL);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS students (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT, age INTEGER, grade TEXT, subject TEXT, score REAL, email TEXT)";
    try (Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public List<Student> getAllStudents() {
    List<Student> list = new ArrayList<>();
    String sql = "SELECT * FROM students";

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            list.add(new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("grade"),
                    rs.getString("subject"),
                    rs.getDouble("score"),
                    rs.getString("email")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

public void addStudent(Student s) {
    String sql = "INSERT INTO students(name,age,grade,subject,score,email) VALUES(?,?,?,?,?,?)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, s.getName());
        ps.setInt(2, s.getAge());
        ps.setString(3, s.getGrade());
        ps.setString(4, s.getSubject());
        ps.setDouble(5, s.getScore());
        ps.setString(6, s.getEmail());
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void deleteStudent(int id) {
    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id=?")) {
        ps.setInt(1, id);
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
