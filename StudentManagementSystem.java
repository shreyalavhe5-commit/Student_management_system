import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentManagementSystem extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASSWORD = "YOUR_MYSQL_PASSWORD"; // Change this
    private Connection connection;

    private JTextField idField, nameField, departmentField, emailField, phoneField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public StudentManagementSystem() {
        setTitle("Student Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        connectDatabase();
        buildGUI();
        loadStudents();
    }

    private void connectDatabase() {
        try {
            // Modern MySQL Connector/J automatically registers its driver.
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database connection failed. Check MySQL, database name, username/password, and JDBC driver.\n\n"
                + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildGUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("STUDENT MANAGEMENT SYSTEM", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 5));
        add(heading, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));

        idField = new JTextField();
        nameField = new JTextField();
        departmentField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        form.add(new JLabel("Student ID (for update/delete):"));
        form.add(idField);
        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Department:"));
        form.add(departmentField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadStudents());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(form, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Students"));
        searchField = new JTextField(22);
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        searchButton.addActionListener(e -> searchStudents());
        showAllButton.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });
        searchPanel.add(new JLabel("ID or name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Department", "Email", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                idField.setText(tableModel.getValueAt(row, 0).toString());
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                departmentField.setText(tableModel.getValueAt(row, 2).toString());
                emailField.setText(tableModel.getValueAt(row, 3).toString());
                phoneField.setText(tableModel.getValueAt(row, 4).toString());
            }
        });

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.40);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private boolean ensureConnection() {
        if (connection != null) return true;
        JOptionPane.showMessageDialog(this,
            "No database connection. Check your MySQL settings and restart the application.",
            "Database Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private boolean validateStudentFields(boolean requireId) {
        if (requireId && idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter/select a Student ID.");
            return false;
        }
        if (nameField.getText().trim().isEmpty()
                || departmentField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()
                || phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all student details.");
            return false;
        }
        if (!emailField.getText().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return false;
        }
        return true;
    }

    private void addStudent() {
        if (!ensureConnection() || !validateStudentFields(false)) return;
        String sql = "INSERT INTO students (name, department, email, phone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, departmentField.getText().trim());
            ps.setString(3, emailField.getText().trim());
            ps.setString(4, phoneField.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully.");
            clearFields();
            loadStudents();
        } catch (SQLException ex) {
            showSqlError(ex);
        }
    }

    private void updateStudent() {
        if (!ensureConnection() || !validateStudentFields(true)) return;
        String sql = "UPDATE students SET name=?, department=?, email=?, phone=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, departmentField.getText().trim());
            ps.setString(3, emailField.getText().trim());
            ps.setString(4, phoneField.getText().trim());
            ps.setInt(5, Integer.parseInt(idField.getText().trim()));
            int changed = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, changed > 0
                ? "Student updated successfully." : "No student found with that ID.");
            loadStudents();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.");
        } catch (SQLException ex) {
            showSqlError(ex);
        }
    }

    private void deleteStudent() {
        if (!ensureConnection()) return;
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student or enter a Student ID.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete student ID " + idField.getText().trim() + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            int deleted = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, deleted > 0
                ? "Student deleted successfully." : "No student found with that ID.");
            clearFields();
            loadStudents();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.");
        } catch (SQLException ex) {
            showSqlError(ex);
        }
    }

    private void loadStudents() {
        if (!ensureConnection()) return;
        tableModel.setRowCount(0);
        String sql = "SELECT id, name, department, email, phone FROM students ORDER BY id";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("department"),
                    rs.getString("email"), rs.getString("phone")
                });
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        }
    }

    private void searchStudents() {
        if (!ensureConnection()) return;
        String term = searchField.getText().trim();
        tableModel.setRowCount(0);
        String sql = "SELECT id, name, department, email, phone FROM students "
                   + "WHERE CAST(id AS CHAR) = ? OR name LIKE ? ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, term);
            ps.setString(2, "%" + term + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("name"), rs.getString("department"),
                        rs.getString("email"), rs.getString("phone")
                    });
                }
            }
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No matching students found.");
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        departmentField.setText("");
        emailField.setText("");
        phoneField.setText("");
        table.clearSelection();
    }

    private void showSqlError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database operation failed:\n" + ex.getMessage(),
            "SQL Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementSystem().setVisible(true));
    }
}
