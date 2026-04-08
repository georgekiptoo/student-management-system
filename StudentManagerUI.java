package ui;

import model.Student;
import database.DatabaseManager;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

// Main window of the Student Management System
public class StudentManagerUI extends JFrame {

    static final String[] COLUMNS = {"ID", "Name", "Age", "Grade", "Subject", "Score", "Email"};

    static final String[] GRADES = {"A", "B", "C", "D", "F"};
    static final String[] SUBJECTS = {
            "Mathematics", "Science", "English", "History",
            "Geography", "Computer Science", "Art", "P.E."
    };

    private DatabaseManager db;

    // table
    private JTable table;
    private DefaultTableModel tableModel;

    // form fields
    private JTextField txtName, txtAge, txtScore, txtEmail;
    private JComboBox<String> cbGrade, cbSubject;
    private JButton btnSubmit, btnClear, btnDelete;

    // search and filter
    private JTextField txtSearch;
    private JComboBox<String> cbFilterGrade, cbFilterSubject;
    private JTextField txtMinScore, txtMaxScore;

    private JLabel lblStatus;

    private int editingId = -1; // -1 = adding new

    public StudentManagerUI(DatabaseManager db) {
        this.db = db;

        setTitle("Student Management System");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupMenuBar();
        setupUI();
        refreshTable();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem miExport = new JMenuItem("Export to CSV...");
        JMenuItem miExit   = new JMenuItem("Exit");

        miExport.addActionListener(e -> exportCSV());
        miExit.addActionListener(e -> {
            db.close();
            System.exit(0);
        });

        fileMenu.add(miExport);
        fileMenu.addSeparator();
        fileMenu.add(miExit);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem miDelete = new JMenuItem("Delete Selected");
        miDelete.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        miDelete.addActionListener(e -> deleteStudent());
        editMenu.add(miDelete);

        JMenu dbMenu = new JMenu("Database");
        JMenuItem miInfo = new JMenuItem("Show DB Info");
        miInfo.addActionListener(e -> showDBInfo());
        dbMenu.add(miInfo);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(dbMenu);
        setJMenuBar(menuBar);
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        add(buildSearchPanel(), BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(buildFormPanel(),  BorderLayout.CENTER);
        bottom.add(buildStatusBar(),  BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panel.setBackground(new Color(220, 230, 245));

        panel.add(new JLabel("Search:"));
        txtSearch = new JTextField(15);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) {}
        });
        panel.add(txtSearch);

        panel.add(new JLabel("  Grade:"));
        cbFilterGrade = new JComboBox<>(addAllOption(GRADES));
        cbFilterGrade.setPreferredSize(new Dimension(70, 24));
        cbFilterGrade.addActionListener(e -> refreshTable());
        panel.add(cbFilterGrade);

        panel.add(new JLabel("  Subject:"));
        cbFilterSubject = new JComboBox<>(addAllOption(SUBJECTS));
        cbFilterSubject.setPreferredSize(new Dimension(140, 24));
        cbFilterSubject.addActionListener(e -> refreshTable());
        panel.add(cbFilterSubject);

        panel.add(new JLabel("  Score:"));
        txtMinScore = new JTextField("0",   3);
        txtMaxScore = new JTextField("100", 3);
        panel.add(txtMinScore);
        panel.add(new JLabel("-"));
        panel.add(txtMaxScore);

        JButton btnApply = new JButton("Apply");
        btnApply.addActionListener(e -> refreshTable());
        panel.add(btnApply);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterGrade.setSelectedIndex(0);
            cbFilterSubject.setSelectedIndex(0);
            txtMinScore.setText("0");
            txtMaxScore.setText("100");
            refreshTable();
        });
        panel.add(btnReset);

        return panel;
    }

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 0 || c == 2) return Integer.class;
                if (c == 5) return Double.class;
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(25);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // click row -> load into form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                loadRowIntoForm(table.getSelectedRow());
        });

        // double-click to edit
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) loadRowIntoForm(table.getSelectedRow());
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(45);
        table.getColumnModel().getColumn(3).setPreferredWidth(55);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(65);
        table.getColumnModel().getColumn(6).setPreferredWidth(180);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add / Edit Student"));
        panel.setBackground(new Color(245, 247, 250));

        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fields.setBackground(new Color(245, 247, 250));

        fields.add(new JLabel("Name:"));
        txtName = new JTextField(14);
        fields.add(txtName);

        fields.add(new JLabel("Age:"));
        txtAge = new JTextField(4);
        fields.add(txtAge);

        fields.add(new JLabel("Grade:"));
        cbGrade = new JComboBox<>(GRADES);
        fields.add(cbGrade);

        fields.add(new JLabel("Subject:"));
        cbSubject = new JComboBox<>(SUBJECTS);
        fields.add(cbSubject);

        fields.add(new JLabel("Score(%):"));
        txtScore = new JTextField(5);
        fields.add(txtScore);

        fields.add(new JLabel("Email:"));
        txtEmail = new JTextField(14);
        fields.add(txtEmail);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        buttons.setBackground(new Color(245, 247, 250));

        btnSubmit = new JButton("Add Student");
        btnSubmit.setBackground(new Color(70, 130, 180));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> saveStudent());

        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearForm());

        btnDelete = new JButton("Delete Selected");
        btnDelete.setBackground(new Color(200, 70, 70));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteStudent());

        buttons.add(btnSubmit);
        buttons.add(btnClear);
        buttons.add(btnDelete);

        panel.add(fields,  BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.LIGHT_GRAY);
        lblStatus = new JLabel("Ready");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblStatus);
        return panel;
    }

    // read the selected row and populate the form
    private void loadRowIntoForm(int viewRow) {
        if (viewRow < 0) return;
        int modelRow = table.convertRowIndexToModel(viewRow);
        txtName.setText ((String) tableModel.getValueAt(modelRow, 1));
        txtAge.setText  (tableModel.getValueAt(modelRow, 2).toString());
        cbGrade.setSelectedItem  (tableModel.getValueAt(modelRow, 3));
        cbSubject.setSelectedItem(tableModel.getValueAt(modelRow, 4));
        txtScore.setText(tableModel.getValueAt(modelRow, 5).toString());
        txtEmail.setText((String) tableModel.getValueAt(modelRow, 6));
        editingId = (int) tableModel.getValueAt(modelRow, 0);
        btnSubmit.setText("Update Student");
        btnSubmit.setBackground(new Color(60, 160, 80));
    }

    private void clearForm() {
        txtName.setText("");
        txtAge.setText("");
        txtScore.setText("");
        txtEmail.setText("");
        cbGrade.setSelectedIndex(0);
        cbSubject.setSelectedIndex(0);
        editingId = -1;
        btnSubmit.setText("Add Student");
        btnSubmit.setBackground(new Color(70, 130, 180));
        table.clearSelection();
    }

    // validate form and call DB add or update
    private void saveStudent() {
        String name    = txtName.getText().trim();
        String ageStr  = txtAge.getText().trim();
        String scoreStr = txtScore.getText().trim();
        String email   = txtEmail.getText().trim();
        String grade   = (String) cbGrade.getSelectedItem();
        String subject = (String) cbSubject.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 120) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number between 1 and 120.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double score;
        try {
            score = Double.parseDouble(scoreStr);
            if (score < 0 || score > 100) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Score must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student s = new Student(editingId, name, age, grade, subject, score, email);

        try {
            if (editingId != -1) {
                db.updateStudent(s);
                setStatus("Updated: " + name);
            } else {
                db.addStudent(s);
                setStatus("Added: " + name);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        clearForm();
        refreshTable();
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id       = (int)    tableModel.getValueAt(modelRow, 0);
        String name  = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + name + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                db.deleteStudent(id);
                clearForm();
                refreshTable();
                setStatus("Deleted: " + name);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Could not delete: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // reload table from database using current filter values
    private void refreshTable() {
        String search  = txtSearch  != null ? txtSearch.getText().trim()                  : "";
        String grade   = cbFilterGrade   != null ? (String) cbFilterGrade.getSelectedItem()   : "All";
        String subject = cbFilterSubject != null ? (String) cbFilterSubject.getSelectedItem() : "All";

        double minScore = 0, maxScore = 100;
        try { minScore = Double.parseDouble(txtMinScore.getText()); } catch (Exception e) {}
        try { maxScore = Double.parseDouble(txtMaxScore.getText()); } catch (Exception e) {}

        try {
            List<Student> students = db.getStudents(search, grade, subject, minScore, maxScore);
            tableModel.setRowCount(0);
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                        s.getId(), s.getName(), s.getAge(),
                        s.getGrade(), s.getSubject(), s.getScore(), s.getEmail()
                });
            }
            int total = db.getTotalCount();
            setStatus("Showing " + students.size() + " of " + total + " students  |  DB: students.db");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data:\n" + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDBInfo() {
        try {
            String stats = db.getStats();
            JOptionPane.showMessageDialog(this, stats, "Database Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export to CSV");
        fc.setSelectedFile(new File("students_export.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.endsWith(".csv")) path += ".csv";

        try {
            int count = db.exportToCSV(path);
            setStatus("Exported " + count + " students to CSV");
            JOptionPane.showMessageDialog(this, "Exported " + count + " students to:\n" + path);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setStatus(String msg) {
        lblStatus.setText("  " + msg);
    }

    private String[] addAllOption(String[] arr) {
        String[] result = new String[arr.length + 1];
        result[0] = "All";
        for (int i = 0; i < arr.length; i++) result[i + 1] = arr[i];
        return result;
    }
}
