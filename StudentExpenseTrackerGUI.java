package techTitans.mt.rh.sn.kv.bk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class StudentExpenseTrackerGUI extends JFrame {

    private JTextField txtID, txtName, txtSurname;
    private JTextField txtFood, txtTransport, txtOther;
    private JTextArea txtResult;
    private JRadioButton rToday, rSelectDate;
    private JComboBox<String> cbDay, cbMonth;
    private JComboBox<Integer> cbYear;
    private FileModifier fileModifier;

    public StudentExpenseTrackerGUI() {
        setTitle("Student Expense Tracker");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        fileModifier = new FileModifier();

        // 1. Student Account Panel
        JPanel accountPanel = new JPanel(new BorderLayout());
        accountPanel.setBorder(BorderFactory.createTitledBorder("Student Account"));

        txtName = new JTextField(15);
        txtSurname = new JTextField(15);
        txtID = new JTextField(15);

        accountPanel.add(createInputPanel("Name:", txtName), BorderLayout.NORTH);
        accountPanel.add(createInputPanel("Surname:", txtSurname), BorderLayout.CENTER);
        accountPanel.add(createInputPanel("ID:", txtID), BorderLayout.SOUTH);

        // 2. Expenses Panel
        JPanel expensePanel = new JPanel(new BorderLayout());
        expensePanel.setBorder(BorderFactory.createTitledBorder("Expenses"));

        txtFood = new JTextField(15);
        txtTransport = new JTextField(15);
        txtOther = new JTextField(15);

        expensePanel.add(createInputPanel("Food:", txtFood), BorderLayout.NORTH);
        expensePanel.add(createInputPanel("Transport:", txtTransport), BorderLayout.CENTER);
        expensePanel.add(createInputPanel("Other:", txtOther), BorderLayout.SOUTH);

        // 3. Info Panel (Combining Account + Expenses)
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(accountPanel, BorderLayout.NORTH);
        infoPanel.add(expensePanel, BorderLayout.CENTER);

        // 4. Date Panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBorder(BorderFactory.createTitledBorder("Date Selection"));

        rToday = new JRadioButton("Today", true);
        rSelectDate = new JRadioButton("Select Date");
        ButtonGroup dateGroup = new ButtonGroup();
        dateGroup.add(rToday);
        dateGroup.add(rSelectDate);

        cbDay = new JComboBox<>(generateDays());
        cbMonth = new JComboBox<>(new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        });

        int currentYear = LocalDate.now().getYear();
        cbYear = new JComboBox<>();
        for (int y = 2000; y <= currentYear; y++) cbYear.addItem(y);

        cbDay.setEnabled(false);
        cbMonth.setEnabled(false);
        cbYear.setEnabled(false);

        rToday.addActionListener(e -> setDateControlsEnabled(false));
        rSelectDate.addActionListener(e -> setDateControlsEnabled(true));

        datePanel.add(rToday);
        datePanel.add(rSelectDate);
        datePanel.add(new JLabel("Day:"));
        datePanel.add(cbDay);
        datePanel.add(new JLabel("Month:"));
        datePanel.add(cbMonth);
        datePanel.add(new JLabel("Year:"));
        datePanel.add(cbYear);

        // 5. Result Panel
        txtResult = new JTextArea(8, 40);
        txtResult.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtResult);
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Result"));
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // 6. Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSave = new JButton("Save");
        JButton btnLoad = new JButton("Load");

        btnSave.addActionListener(e -> saveData());
        btnLoad.addActionListener(e -> loadData());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnLoad);

        // 7. Bottom Panel (BTD Panel)
        JPanel btdPanel = new JPanel(new BorderLayout());
        btdPanel.add(datePanel, BorderLayout.NORTH);
        btdPanel.add(resultPanel, BorderLayout.CENTER);
        btdPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add everything to frame
        add(infoPanel, BorderLayout.NORTH);
        add(btdPanel, BorderLayout.CENTER);
    }

    private JPanel createInputPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(field);
        return panel;
    }

    private String[] generateDays() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) days[i - 1] = String.valueOf(i);
        return days;
    }

    private void setDateControlsEnabled(boolean enabled) {
        cbDay.setEnabled(enabled);
        cbMonth.setEnabled(enabled);
        cbYear.setEnabled(enabled);
    }

    private void saveData() {
    try {
        String id = txtID.getText().trim();

        // Validate ID
        if (!id.matches("\\d{13}")) {
            txtResult.setText("Error: ID number must be exactly 13 digits.");
            return;
        }

        String name = txtName.getText().trim();
        String surname = txtSurname.getText().trim();

        double[] expenses = {
            Double.parseDouble(txtFood.getText().trim()),
            Double.parseDouble(txtTransport.getText().trim()),
            Double.parseDouble(txtOther.getText().trim())
        };

        LocalDate date;
        if (rToday.isSelected()) {
            date = LocalDate.now();
        } else {
            int day = Integer.parseInt((String) cbDay.getSelectedItem());
            int month = cbMonth.getSelectedIndex() + 1;
            int year = (int) cbYear.getSelectedItem();
            date = LocalDate.of(year, month, day);
        }

        StudentProfile profile = new StudentProfile(id, name, surname, date);
        profile.upDateAmount(expenses, date);
        profile.setDate(date);

        boolean saved = fileModifier.fileUpdater(profile);
        txtResult.setText(saved ? "Data saved for " + date : "Failed to save data.");

    } catch (NumberFormatException ex) {
        txtResult.setText("Error: Please enter valid numbers for expenses.");
    } catch (Exception ex) {
        txtResult.setText("Error: " + ex.getMessage());
    }
}

    private void loadData() {
        try {
            String id = txtID.getText();
            LocalDate date;
            if (rToday.isSelected()) {
                date = LocalDate.now();
            } else {
                int day = Integer.parseInt((String) cbDay.getSelectedItem());
                int month = cbMonth.getSelectedIndex() + 1;
                int year = (int) cbYear.getSelectedItem();
                date = LocalDate.of(year, month, day);
            }

            StudentProfile profile = new StudentProfile(id, "", "", date);
            profile.setDate(date);
            String result = fileModifier.getAmount(3, profile);
            result = decryptData(result);
            txtResult.setText(result.isEmpty() ? "No data found." : result);
        } catch (Exception ex) {
            txtResult.setText("Error loading data: " + ex.getMessage());
        }
    }
    
    

public String decryptData(String encodedLine) {
    StringBuilder result = new StringBuilder();
    DecimalFormat formatter = new DecimalFormat("R#,##0.00");

    String[] parts = encodedLine.split("#");

    if (parts.length < 5) {
        return "Invalid data format.";
    }

    String date = parts[0];
    String id = parts[1];
    String name = parts[2];
    String surname = parts[3];
    String expenseData = parts[4];

    result.append("Date Modified: ").append(date).append("\n");
    result.append("Student ID: ").append(id).append("\n");
    result.append("Name: ").append(name).append(" ").append(surname).append("\n\n");
    result.append("=== Expenses ===\n");

    String[] monthRecords = expenseData.split("@");
    for (String record : monthRecords) {
        if (record.trim().isEmpty()) continue;

        String[] tokens = record.split("\\^");
        String recordDate = tokens[0];

        double food = tokens.length > 1 ? Double.parseDouble(tokens[1]) : 0.0;
        double transport = tokens.length > 2 ? Double.parseDouble(tokens[2]) : 0.0;
        double other = tokens.length > 3 ? Double.parseDouble(tokens[3]) : 0.0;

        result.append("Date: ").append(recordDate).append("\n");
        result.append("  Food: ").append(formatter.format(food)).append("\n");
        result.append("  Transport: ").append(formatter.format(transport)).append("\n");
        result.append("  Other: ").append(formatter.format(other)).append("\n\n");
    }

    return result.toString();
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentExpenseTrackerGUI().setVisible(true));
    }
}
