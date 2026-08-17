package com.inventory.UI;

import com.inventory.DAO.CustomerDAO;
import com.inventory.DAO.ProductDAO;
import com.inventory.DTO.ProductDTO;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Modern SalesPage for processing customer orders, real-time stock deductions, and sales records.
 */
public class SalesPage extends javax.swing.JPanel {

    private String username;
    private Dashboard dashboard;
    private int quantity;
    private String prodCode;

    private JTextField custCodeText;
    private JLabel custNameLabel;
    private JButton addCustButton;

    private JTextField prodCodeText;
    private JLabel prodNameLabel;

    private JDateChooser jDateChooser1;
    private JTextField priceText;
    private JTextField quantityText;

    private JButton sellButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable salesTable;
    private JScrollPane jScrollPane1;
    private JTextField searchText;
    private JButton refreshButton;

    public SalesPage(String username, Dashboard dashboard) {
        this.username = username;
        this.dashboard = dashboard;
        initComponents();
        custNameLabel.setVisible(false);
        prodNameLabel.setVisible(false);
        loadDataSet();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 0));
        setBackground(UITheme.BG_DARK);
        setOpaque(true);

        // ==================== LEFT FORM CARD (Width ~360) ====================
        JPanel formCard = UITheme.createCardPanel(16, 16, 16, 16);
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setLayout(new BorderLayout(0, 12));

        JLabel formHeader = new JLabel("Process Sales Order");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        // Customer Code Row + Shortcut
        JPanel custRow = new JPanel(new BorderLayout(6, 0));
        custRow.setOpaque(false);
        custCodeText = new JTextField();
        UITheme.styleTextField(custCodeText);
        custCodeText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                handleCustCodeLookup();
            }
        });

        addCustButton = UITheme.createSecondaryButton("+");
        addCustButton.setToolTipText("Add new customer");
        addCustButton.setPreferredSize(new Dimension(36, 32));
        addCustButton.addActionListener(e -> {
            if (dashboard != null) dashboard.addCustPage();
        });

        custRow.add(custCodeText, BorderLayout.CENTER);
        custRow.add(addCustButton, BorderLayout.EAST);

        // Customer Info Label Badge
        custNameLabel = new JLabel();
        custNameLabel.setFont(UITheme.FONT_SMALL);
        custNameLabel.setForeground(UITheme.ACCENT);
        custNameLabel.setBorder(new EmptyBorder(2, 4, 4, 4));

        // Product Code Row
        prodCodeText = new JTextField();
        UITheme.styleTextField(prodCodeText);
        prodCodeText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                handleProdCodeLookup();
            }
        });

        prodNameLabel = new JLabel();
        prodNameLabel.setFont(UITheme.FONT_SMALL);
        prodNameLabel.setForeground(UITheme.ACCENT);
        prodNameLabel.setBorder(new EmptyBorder(2, 4, 4, 4));

        jDateChooser1 = new JDateChooser();
        jDateChooser1.setBackground(UITheme.BG_INPUT);
        jDateChooser1.setForeground(UITheme.TEXT_PRIMARY);
        jDateChooser1.setFont(UITheme.FONT_BODY);

        priceText = new JTextField();
        UITheme.styleTextField(priceText);

        quantityText = new JTextField();
        UITheme.styleTextField(quantityText);

        // Assemble Fields
        addField(fieldsPanel, "Customer Code", custRow);
        fieldsPanel.add(custNameLabel);
        fieldsPanel.add(Box.createVerticalStrut(6));

        addField(fieldsPanel, "Product Code", prodCodeText);
        fieldsPanel.add(prodNameLabel);
        fieldsPanel.add(Box.createVerticalStrut(6));

        addField(fieldsPanel, "Order Date", jDateChooser1);
        addField(fieldsPanel, "Unit Price", priceText);
        addField(fieldsPanel, "Quantity to Sell", quantityText);

        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formCard.add(formScroll, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        actionPanel.setOpaque(false);

        sellButton = UITheme.createSuccessButton("COMPLETE SALE");
        sellButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sellButton.addActionListener(e -> performSell());

        JPanel subActionRow = new JPanel(new GridLayout(1, 2, 8, 0));
        subActionRow.setOpaque(false);
        deleteButton = UITheme.createDangerButton("Delete Sale");
        clearButton = UITheme.createSecondaryButton("Clear Form");

        deleteButton.addActionListener(e -> performDelete());
        clearButton.addActionListener(e -> performClear());

        subActionRow.add(deleteButton);
        subActionRow.add(clearButton);

        actionPanel.add(sellButton);
        actionPanel.add(subActionRow);

        formCard.add(actionPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.WEST);

        // ==================== RIGHT TABLE CARD ====================
        JPanel tableCard = UITheme.createCardPanel(16, 16, 16, 16);
        tableCard.setLayout(new BorderLayout(0, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel tableTitle = new JLabel("Sales Transaction History");
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
        searchText.setToolTipText("Search sales records...");
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
        salesTable = new JTable();
        UITheme.styleTable(salesTable);
        salesTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleTableClick();
            }
        });

        jScrollPane1 = new JScrollPane(salesTable);
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

    private void handleCustCodeLookup() {
        String code = custCodeText.getText().trim();
        if (code.isEmpty()) {
            custNameLabel.setVisible(false);
            return;
        }

        try {
            ResultSet rs = new CustomerDAO().getCustName(code);
            if (rs != null && rs.next()) {
                custNameLabel.setText("[OK] " + rs.getString("fullname") + " (" + rs.getString("location") + ")");
                custNameLabel.setForeground(UITheme.SUCCESS);
            } else {
                custNameLabel.setText("[!] Customer not found");
                custNameLabel.setForeground(UITheme.DANGER);
            }
            custNameLabel.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleProdCodeLookup() {
        String code = prodCodeText.getText().trim();
        if (code.isEmpty()) {
            prodNameLabel.setVisible(false);
            return;
        }

        try {
            ResultSet rs = new CustomerDAO().getProdName(code);
            if (rs != null && rs.next()) {
                prodNameLabel.setText("[OK] " + rs.getString("productname") + "  |  In Stock: " + rs.getString("quantity"));
                prodNameLabel.setForeground(UITheme.SUCCESS);
                Double sellPrice = new ProductDAO().getProdSell(code);
                if (sellPrice != null) {
                    priceText.setText(sellPrice.toString());
                }
            } else {
                prodNameLabel.setText("[!] Product not found in catalog");
                prodNameLabel.setForeground(UITheme.DANGER);
            }
            prodNameLabel.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void performSell() {
        if (custCodeText.getText().trim().isEmpty() || prodCodeText.getText().trim().isEmpty()
                || jDateChooser1.getDate() == null || quantityText.getText().trim().isEmpty()
                || priceText.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all required sales fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ResultSet rs = new CustomerDAO().getCustName(custCodeText.getText().trim());
            if (rs != null && rs.next()) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setCustCode(custCodeText.getText().trim());
                productDTO.setDate(jDateChooser1.getDate().toString());
                productDTO.setProdCode(prodCodeText.getText().trim());
                double sellPrice = Double.parseDouble(priceText.getText().trim());
                int qty = Integer.parseInt(quantityText.getText().trim());
                productDTO.setTotalRevenue(sellPrice * qty);
                productDTO.setQuantity(qty);

                new ProductDAO().sellProductDAO(productDTO, username);
                loadDataSet();
                performClear();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Customer does not exist. Please enter a valid customer code.",
                        "Invalid Customer",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for price and quantity.",
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void performDelete() {
        if (salesTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a sales record from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this sale record and restore stock?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {
            int saleId = Integer.parseInt(salesTable.getValueAt(salesTable.getSelectedRow(), 0).toString());
            new ProductDAO().deleteSaleDAO(saleId);
            new ProductDAO().editSoldStock(salesTable.getValueAt(salesTable.getSelectedRow(), 1).toString(), quantity);
            loadDataSet();
            performClear();
        }
    }

    private void performClear() {
        custCodeText.setText("");
        custNameLabel.setText("");
        custNameLabel.setVisible(false);
        prodCodeText.setText("");
        prodNameLabel.setText("");
        prodNameLabel.setVisible(false);
        if (jDateChooser1 != null) jDateChooser1.setDate(null);
        priceText.setText("");
        quantityText.setText("");
        searchText.setText("");
        loadDataSet();
    }

    private void handleTableClick() {
        int row = salesTable.getSelectedRow();
        if (row < 0) return;
        int col = salesTable.getColumnCount();

        Object[] data = new Object[col];
        for (int i = 0; i < col; i++) {
            data[i] = salesTable.getValueAt(row, i);
        }

        if (data.length > 3 && data[3] != null) {
            try {
                quantity = Integer.parseInt(data[3].toString());
            } catch (NumberFormatException ignored) {}
        }
        if (data.length > 1 && data[1] != null) prodCode = data[1].toString();
    }

    public void loadDataSet() {
        try {
            ProductDAO productDAO = new ProductDAO();
            salesTable.setModel(productDAO.buildTableModel(productDAO.getSalesInfo()));
            UITheme.styleTable(salesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSearchData(String text) {
        try {
            ProductDAO productDAO = new ProductDAO();
            salesTable.setModel(productDAO.buildTableModel(productDAO.getSalesSearch(text)));
            UITheme.styleTable(salesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
