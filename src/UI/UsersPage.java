package com.inventory.UI;

import com.inventory.DAO.UserDAO;
import com.inventory.DTO.UserDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * Modern UsersPage for managing system user accounts, roles, and administrative permissions.
 */
public class UsersPage extends javax.swing.JPanel {

    private JTextField nameText;
    private JTextField locationText;
    private JTextField phoneText;
    private JTextField usernameText;
    private JPasswordField passText;
    private JComboBox<String> userTypeCombo;

    private JButton addButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable userTable;
    private JScrollPane jScrollPane1;
    private JTextField searchText;
    private JButton refreshButton;

    public UsersPage() {
        initComponents();
        loadDataSet();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 0));
        setBackground(UITheme.BG_DARK);
        setOpaque(true);

        // ==================== LEFT FORM CARD (Width ~340) ====================
        JPanel formCard = UITheme.createCardPanel(16, 16, 16, 16);
        formCard.setPreferredSize(new Dimension(340, 0));
        formCard.setLayout(new BorderLayout(0, 12));

        JLabel formHeader = new JLabel("User Account Details");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        nameText = new JTextField();
        UITheme.styleTextField(nameText);

        locationText = new JTextField();
        UITheme.styleTextField(locationText);

        phoneText = new JTextField();
        UITheme.styleTextField(phoneText);

        usernameText = new JTextField();
        UITheme.styleTextField(usernameText);

        passText = new JPasswordField();
        UITheme.stylePasswordField(passText);

        userTypeCombo = new JComboBox<>(new String[]{"ADMINISTRATOR", "EMPLOYEE"});
        UITheme.styleComboBox(userTypeCombo);

        addField(fieldsPanel, "Full Name", nameText);
        addField(fieldsPanel, "Location / Branch", locationText);
        addField(fieldsPanel, "Contact Number", phoneText);
        addField(fieldsPanel, "Username", usernameText);
        addField(fieldsPanel, "Password", passText);
        addField(fieldsPanel, "Role / Permission Level", userTypeCombo);

        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formCard.add(formScroll, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        actionPanel.setOpaque(false);

        addButton = UITheme.createSuccessButton("CREATE USER");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addButton.addActionListener(e -> performAdd());

        JPanel subActionRow = new JPanel(new GridLayout(1, 2, 8, 0));
        subActionRow.setOpaque(false);
        deleteButton = UITheme.createDangerButton("Delete User");
        clearButton = UITheme.createSecondaryButton("Clear Form");

        deleteButton.addActionListener(e -> performDelete());
        clearButton.addActionListener(e -> performClear());

        subActionRow.add(deleteButton);
        subActionRow.add(clearButton);

        actionPanel.add(addButton);
        actionPanel.add(subActionRow);

        formCard.add(actionPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.WEST);

        // ==================== RIGHT TABLE CARD ====================
        JPanel tableCard = UITheme.createCardPanel(16, 16, 16, 16);
        tableCard.setLayout(new BorderLayout(0, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel tableTitle = new JLabel("System User Directory");
        tableTitle.setFont(UITheme.FONT_HEADER);
        tableTitle.setForeground(UITheme.TEXT_PRIMARY);
        toolbar.add(tableTitle, BorderLayout.WEST);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBox.setOpaque(false);

        refreshButton = UITheme.createSecondaryButton("Refresh");
        refreshButton.setFont(UITheme.FONT_SMALL);
        refreshButton.addActionListener(e -> {
            loadDataSet();
            performClear();
        });

        searchBox.add(refreshButton);
        toolbar.add(searchBox, BorderLayout.EAST);

        tableCard.add(toolbar, BorderLayout.NORTH);

        // Table
        userTable = new JTable();
        UITheme.styleTable(userTable);
        userTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleTableClick();
            }
        });

        jScrollPane1 = new JScrollPane(userTable);
        jScrollPane1.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));
        tableCard.add(jScrollPane1, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, String labelText, Component comp) {
        JLabel label = UITheme.createFieldLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (comp instanceof JComponent) {
            ((JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
            comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 32));
            comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        }

        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(comp);
        panel.add(Box.createVerticalStrut(8));
    }

    private void performAdd() {
        if (nameText.getText().trim().isEmpty() || locationText.getText().trim().isEmpty()
                || phoneText.getText().trim().isEmpty() || usernameText.getText().trim().isEmpty()
                || new String(passText.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all required user fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDTO userDTO = new UserDTO();
        String userType = (String) userTypeCombo.getSelectedItem();
        userDTO.setFullName(nameText.getText().trim());
        userDTO.setLocation(locationText.getText().trim());
        userDTO.setPhone(phoneText.getText().trim());
        userDTO.setUsername(usernameText.getText().trim());
        userDTO.setPassword(new String(passText.getPassword()).trim());
        userDTO.setUserType(userType);

        new UserDAO().addUserDAO(userDTO, userType);
        loadDataSet();
        performClear();
    }

    private void performDelete() {
        if (userTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user account?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {
            String usernameToDelete = String.valueOf(userTable.getValueAt(userTable.getSelectedRow(), 4));
            new UserDAO().deleteUserDAO(usernameToDelete);
            loadDataSet();
            performClear();
        }
    }

    private void performClear() {
        nameText.setText("");
        locationText.setText("");
        phoneText.setText("");
        usernameText.setText("");
        passText.setText("");
    }

    private void handleTableClick() {
        int row = userTable.getSelectedRow();
        if (row < 0) return;
        int col = userTable.getColumnCount();

        Object[] val = new Object[col];
        for (int i = 0; i < col; i++) {
            val[i] = userTable.getValueAt(row, i);
        }

        if (val.length > 1 && val[1] != null) nameText.setText(val[1].toString());
        if (val.length > 2 && val[2] != null) locationText.setText(val[2].toString());
        if (val.length > 3 && val[3] != null) phoneText.setText(val[3].toString());
        if (val.length > 4 && val[4] != null) usernameText.setText(val[4].toString());
        if (val.length > 6 && val[6] != null) userTypeCombo.setSelectedItem(val[6].toString());
    }

    public void loadDataSet() {
        try {
            UserDAO userDAO = new UserDAO();
            userTable.setModel(userDAO.buildTableModel(userDAO.getQueryResult()));
            UITheme.styleTable(userTable);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
