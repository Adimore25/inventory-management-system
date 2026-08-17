package com.inventory.UI;

import com.inventory.DAO.CustomerDAO;
import com.inventory.DTO.CustomerDTO;

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
 * Modern CustomerPage for managing customer records and contact directory.
 */
public class CustomerPage extends javax.swing.JPanel {

    private JTextField codeText;
    private JTextField nameText;
    private JTextField locationText;
    private JTextField phoneText;
    private JTextField creditText;
    private JTextField debitText;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable custTable;
    private JScrollPane jScrollPane1;
    private JTextField searchText;
    private JButton refreshButton;

    public CustomerPage() {
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

        JLabel formHeader = new JLabel("Customer Information");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        codeText = new JTextField();
        UITheme.styleTextField(codeText);

        nameText = new JTextField();
        UITheme.styleTextField(nameText);

        locationText = new JTextField();
        UITheme.styleTextField(locationText);

        phoneText = new JTextField();
        UITheme.styleTextField(phoneText);

        creditText = new JTextField();
        UITheme.styleTextField(creditText);

        debitText = new JTextField();
        UITheme.styleTextField(debitText);

        addField(fieldsPanel, "Customer Code", codeText);
        addField(fieldsPanel, "Full Name", nameText);
        addField(fieldsPanel, "Location / City", locationText);
        addField(fieldsPanel, "Contact / Mobile", phoneText);
        addField(fieldsPanel, "Debit Amount", debitText);
        addField(fieldsPanel, "Credit Amount", creditText);

        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formCard.add(formScroll, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        actionPanel.setOpaque(false);

        addButton = UITheme.createSuccessButton("Add Customer");
        editButton = UITheme.createWarningButton("Update");
        deleteButton = UITheme.createDangerButton("Delete");
        clearButton = UITheme.createSecondaryButton("Clear Form");

        addButton.addActionListener(e -> performAdd());
        editButton.addActionListener(e -> performEdit());
        deleteButton.addActionListener(e -> performDelete());
        clearButton.addActionListener(e -> performClear());

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(clearButton);

        formCard.add(actionPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.WEST);

        // ==================== RIGHT TABLE CARD ====================
        JPanel tableCard = UITheme.createCardPanel(16, 16, 16, 16);
        tableCard.setLayout(new BorderLayout(0, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel tableTitle = new JLabel("Customer Directory");
        tableTitle.setFont(UITheme.FONT_HEADER);
        tableTitle.setForeground(UITheme.TEXT_PRIMARY);
        toolbar.add(tableTitle, BorderLayout.WEST);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBox.setOpaque(false);

        JLabel searchIcon = new JLabel("Search:");
        searchIcon.setFont(UITheme.FONT_BOLD);
        searchIcon.setForeground(UITheme.TEXT_MUTED);

        searchText = new JTextField(16);
        UITheme.styleTextField(searchText);
        searchText.setToolTipText("Search by code, name, or phone...");
        searchText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadSearchData(searchText.getText().trim());
            }
        });

        refreshButton = UITheme.createSecondaryButton("Refresh");
        refreshButton.setFont(UITheme.FONT_SMALL);
        refreshButton.addActionListener(e -> {
            loadDataSet();
            performClear();
        });

        searchBox.add(searchIcon);
        searchBox.add(searchText);
        searchBox.add(refreshButton);
        toolbar.add(searchBox, BorderLayout.EAST);

        tableCard.add(toolbar, BorderLayout.NORTH);

        // Table
        custTable = new JTable();
        UITheme.styleTable(custTable);
        custTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        custTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleTableClick();
            }
        });

        jScrollPane1 = new JScrollPane(custTable);
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
        panel.add(Box.createVerticalStrut(10));
    }

    private void performAdd() {
        if (codeText.getText().trim().isEmpty() || nameText.getText().trim().isEmpty()
                || locationText.getText().trim().isEmpty() || phoneText.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required customer details.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setCustCode(codeText.getText().trim());
        dto.setFullName(nameText.getText().trim());
        dto.setLocation(locationText.getText().trim());
        dto.setPhone(phoneText.getText().trim());

        new CustomerDAO().addCustomerDAO(dto);
        loadDataSet();
        performClear();
    }

    private void performEdit() {
        if (custTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer from the table to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (codeText.getText().trim().isEmpty() || nameText.getText().trim().isEmpty()
                || locationText.getText().trim().isEmpty() || phoneText.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all required customer details.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setCustCode(codeText.getText().trim());
        dto.setFullName(nameText.getText().trim());
        dto.setLocation(locationText.getText().trim());
        dto.setPhone(phoneText.getText().trim());

        new CustomerDAO().editCustomerDAO(dto);
        loadDataSet();
        performClear();
    }

    private void performDelete() {
        if (custTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this customer?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {
            String custCode = custTable.getValueAt(custTable.getSelectedRow(), 0).toString();
            new CustomerDAO().deleteCustomerDAO(custCode);
            loadDataSet();
            performClear();
        }
    }

    private void performClear() {
        codeText.setText("");
        nameText.setText("");
        locationText.setText("");
        phoneText.setText("");
        creditText.setText("");
        debitText.setText("");
        searchText.setText("");
    }

    private void handleTableClick() {
        int row = custTable.getSelectedRow();
        if (row < 0) return;
        int col = custTable.getColumnCount();

        Object[] data = new Object[col];
        for (int i = 0; i < col; i++) {
            data[i] = custTable.getValueAt(row, i);
        }

        if (data.length > 0 && data[0] != null) codeText.setText(data[0].toString());
        if (data.length > 1 && data[1] != null) nameText.setText(data[1].toString());
        if (data.length > 2 && data[2] != null) locationText.setText(data[2].toString());
        if (data.length > 3 && data[3] != null) phoneText.setText(data[3].toString());
    }

    public void loadDataSet() {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            custTable.setModel(customerDAO.buildTableModel(customerDAO.getQueryResult()));
            UITheme.styleTable(custTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSearchData(String text) {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            custTable.setModel(customerDAO.buildTableModel(customerDAO.getCustomerSearch(text)));
            UITheme.styleTable(custTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
