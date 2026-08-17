package com.inventory.UI;

import com.inventory.DAO.ProductDAO;
import com.inventory.DAO.SupplierDAO;
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
 * Modern PurchasePage for procurement, supplier orders, and incoming shipment logs.
 */
public class PurchasePage extends javax.swing.JPanel {

    private ProductDTO productDTO;
    private String username = null;
    private String supplier = null;
    private Dashboard dashboard;
    private int quantity;
    private String prodCode = null;

    private JComboBox<String> suppCombo;
    private JButton addSuppButton;
    private JTextField codeText;
    private JTextField nameText;
    private JDateChooser jDateChooser1;
    private JTextField quantityText;
    private JTextField costText;
    private JTextField sellText;
    private JTextField brandText;

    private JButton purchaseButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable purchaseTable;
    private JScrollPane jScrollPane1;
    private JTextField searchText;
    private JButton refreshButton;

    public PurchasePage(Dashboard dashboard) {
        this.dashboard = dashboard;
        initComponents();
        loadComboBox();
        loadDataSet();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 0));
        setBackground(UITheme.BG_DARK);
        setOpaque(true);

        // ==================== LEFT FORM CARD (Width ~350) ====================
        JPanel formCard = UITheme.createCardPanel(16, 16, 16, 16);
        formCard.setPreferredSize(new Dimension(350, 0));
        formCard.setLayout(new BorderLayout(0, 12));

        JLabel formHeader = new JLabel("Purchase Order Entry");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        // Supplier row with Add Supplier shortcut
        JPanel suppRow = new JPanel(new BorderLayout(6, 0));
        suppRow.setOpaque(false);
        suppCombo = new JComboBox<>(new String[]{"Select a supplier"});
        UITheme.styleComboBox(suppCombo);
        addSuppButton = UITheme.createSecondaryButton("+");
        addSuppButton.setToolTipText("Add new supplier");
        addSuppButton.setPreferredSize(new Dimension(36, 32));
        addSuppButton.addActionListener(e -> {
            if (dashboard != null) dashboard.addSuppPage();
        });
        suppRow.add(suppCombo, BorderLayout.CENTER);
        suppRow.add(addSuppButton, BorderLayout.EAST);

        codeText = new JTextField();
        UITheme.styleTextField(codeText);
        codeText.setToolTipText("Enter product code to autofill details");
        codeText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                handleCodeLookup();
            }
        });

        nameText = new JTextField();
        UITheme.styleTextField(nameText);

        jDateChooser1 = new JDateChooser();
        jDateChooser1.setBackground(UITheme.BG_INPUT);
        jDateChooser1.setForeground(UITheme.TEXT_PRIMARY);
        jDateChooser1.setFont(UITheme.FONT_BODY);

        quantityText = new JTextField();
        UITheme.styleTextField(quantityText);

        costText = new JTextField();
        UITheme.styleTextField(costText);

        sellText = new JTextField();
        UITheme.styleTextField(sellText);

        brandText = new JTextField();
        UITheme.styleTextField(brandText);

        // Assemble Fields
        addField(fieldsPanel, "Supplier", suppRow);
        addField(fieldsPanel, "Product Code", codeText);
        addField(fieldsPanel, "Product Name", nameText);
        addField(fieldsPanel, "Purchase Date", jDateChooser1);
        addField(fieldsPanel, "Quantity Ordered", quantityText);
        addField(fieldsPanel, "Cost Price", costText);
        addField(fieldsPanel, "Selling Price", sellText);
        addField(fieldsPanel, "Brand / Manufacturer", brandText);

        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formCard.add(formScroll, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        actionPanel.setOpaque(false);

        purchaseButton = UITheme.createSuccessButton("SUBMIT PURCHASE");
        purchaseButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        purchaseButton.addActionListener(e -> performPurchase());

        JPanel subActionRow = new JPanel(new GridLayout(1, 2, 8, 0));
        subActionRow.setOpaque(false);
        deleteButton = UITheme.createDangerButton("Delete Order");
        clearButton = UITheme.createSecondaryButton("Clear Form");

        deleteButton.addActionListener(e -> performDelete());
        clearButton.addActionListener(e -> performClear());

        subActionRow.add(deleteButton);
        subActionRow.add(clearButton);

        actionPanel.add(purchaseButton);
        actionPanel.add(subActionRow);

        formCard.add(actionPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.WEST);

        // ==================== RIGHT TABLE CARD ====================
        JPanel tableCard = UITheme.createCardPanel(16, 16, 16, 16);
        tableCard.setLayout(new BorderLayout(0, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel tableTitle = new JLabel("Purchase Orders Log");
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
        searchText.setToolTipText("Search purchase orders...");
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
            loadComboBox();
            performClear();
        });

        searchBox.add(searchIcon);
        searchBox.add(searchText);
        searchBox.add(refreshButton);
        toolbar.add(searchBox, BorderLayout.EAST);

        tableCard.add(toolbar, BorderLayout.NORTH);

        // Table
        purchaseTable = new JTable();
        UITheme.styleTable(purchaseTable);
        purchaseTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        purchaseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleTableClick();
            }
        });

        jScrollPane1 = new JScrollPane(purchaseTable);
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

    private void handleCodeLookup() {
        String code = codeText.getText().trim();
        if (code.isEmpty()) return;

        try {
            ResultSet rs = new ProductDAO().getProdFromCode(code);
            if (rs != null && rs.next()) {
                nameText.setText(rs.getString("productname"));
                costText.setText(String.valueOf(rs.getDouble("costprice")));
                sellText.setText(String.valueOf(rs.getDouble("sellprice")));
                brandText.setText(rs.getString("brand"));
            } else {
                nameText.setText("");
                costText.setText("");
                sellText.setText("");
                brandText.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void performPurchase() {
        if (codeText.getText().trim().isEmpty() || jDateChooser1.getDate() == null
                || quantityText.getText().trim().isEmpty() || costText.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all required purchase details.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        productDTO = new ProductDTO();
        String selectedSupp = (suppCombo.getSelectedItem() != null) ? suppCombo.getSelectedItem().toString() : "";
        productDTO.setSuppCode(new ProductDAO().getSuppCode(selectedSupp));
        productDTO.setProdCode(codeText.getText().trim());

        try {
            ResultSet rs = new ProductDAO().getProdName(codeText.getText().trim());
            if (rs != null && rs.next()) {
                productDTO.setDate(jDateChooser1.getDate().toString());
                int qty = Integer.parseInt(quantityText.getText().trim());
                productDTO.setQuantity(qty);
                double cost = Double.parseDouble(costText.getText().trim());
                productDTO.setTotalCost(cost * qty);

                new ProductDAO().addPurchaseDAO(productDTO);
                loadDataSet();
                performClear();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Product code was not found in Catalog.\nPlease add the product in the 'Products' section first.",
                        "Unknown Product",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity and Cost Price must be valid numbers.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void performDelete() {
        if (purchaseTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a purchase order from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this purchase order?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {
            int purchaseId = (int) purchaseTable.getValueAt(purchaseTable.getSelectedRow(), 0);
            new ProductDAO().deletePurchaseDAO(purchaseId);
            new ProductDAO().editPurchaseStock(prodCode, quantity);
            loadDataSet();
            performClear();
        }
    }

    private void performClear() {
        codeText.setText("");
        nameText.setText("");
        if (jDateChooser1 != null) jDateChooser1.setDate(null);
        quantityText.setText("");
        costText.setText("");
        sellText.setText("");
        brandText.setText("");
        searchText.setText("");
    }

    private void handleTableClick() {
        int row = purchaseTable.getSelectedRow();
        if (row < 0) return;
        int col = purchaseTable.getColumnCount();

        Object[] data = new Object[col];
        for (int i = 0; i < col; i++) {
            data[i] = purchaseTable.getValueAt(row, i);
        }

        if (data.length > 3 && data[3] != null) {
            try {
                quantity = Integer.parseInt(data[3].toString());
            } catch (NumberFormatException ignored) {}
        }
        if (data.length > 1 && data[1] != null) prodCode = data[1].toString();
    }

    public void loadComboBox() {
        try {
            SupplierDAO supplierDAO = new SupplierDAO();
            suppCombo.setModel(supplierDAO.setComboItems(supplierDAO.getQueryResult()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadDataSet() {
        try {
            ProductDAO productDAO = new ProductDAO();
            purchaseTable.setModel(productDAO.buildTableModel(productDAO.getPurchaseInfo()));
            UITheme.styleTable(purchaseTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSearchData(String text) {
        try {
            ProductDAO productDAO = new ProductDAO();
            purchaseTable.setModel(productDAO.buildTableModel(productDAO.getPurchaseSearch(text)));
            UITheme.styleTable(purchaseTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
