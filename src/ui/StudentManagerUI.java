import database.DatabaseManager;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentManagerUI extends JFrame {

    private DatabaseManager db = new DatabaseManager();
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtName = new JTextField(10);
    private JTextField txtAge = new JTextField(5);

    public StudentManagerUI() {
        setTitle("Student Manager");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID","Name","Age"},0);
        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        loadData();
        setVisible(true);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel();

        panel.add(new JLabel("Name:"));
        panel.add(txtName);

        panel.add(new JLabel("Age:"));
        panel.add(txtAge);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addStudent());

        JButton delBtn = new JButton("Delete");
        delBtn.addActionListener(e -> deleteStudent());

        panel.add(addBtn);
        panel.add(delBtn);

        return panel;
    }

    private void loadData() {
        model.setRowCount(0);
        List<Student> students = db.getAllStudents();
        for (Student s : students) {
            model.addRow(new Object[]{s.getId(), s.getName(), s.getAge()});
        }
    }

    private void addStudent() {
        String name = txtName.getText();
        int age = Integer.parseInt(txtAge.getText());

        db.addStudent(new Student(name, age, "A", "Math", 90, "email@test.com"));
        loadData();
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) model.getValueAt(row, 0);
            db.deleteStudent(id);
            loadData();
        }
    }
}
