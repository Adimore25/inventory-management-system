package com.inventory.UI;

import com.inventory.DAO.ProductDAO;
import com.inventory.DAO.SupplierDAO;
import com.inventory.DTO.ProductDTO;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * Modern ProductPage for managing product catalog and stock.
 */
public class ProductPage extends javax.swing.JPanel {

    private ProductDTO productDTO;
    private String username = null;
    private String supplier = null;
    private int userID;
    private Dashboard dashboard;

    private JComboBox<String> suppCombo;
    private JButton addSuppButton;
    private JTextField codeText;
    private JTextField nameText;
    private JDateChooser jDateChooser1;
    private JTextField quantityText;
    private JTextField costText;
    private JTextField sellText;
    private JTextField brandText;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable productTable;
    private JScrollPane jScrollPane1;
    private JTextField searchText;
    private JButton refreshButton;

    public ProductPage() {
        initComponents();
    }

    public ProductPage(String username, Dashboard dashboard) {
        this.username = username;
        this.dashboard = dashboard;
        initComponents();
        loadComboBox();
        loadDataSet();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 0));
        setBackground(UITheme.BG_DARK);
        setOpaque(true);

        // ==================== LEFT FORM CARD (Width ~340) ====================
        JPanel formCard = UITheme.createCardPanel(16, 16, 16, 16);
        formCard.setPreferredSize(new Dimension(350, 0));
        formCard.setLayout(new BorderLayout(0, 12));

        // Form Title
        JLabel formHeader = new JLabel("Product Details");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        // Form Fields Container
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        // Supplier row with Add Supplier shortcut
        JPanel suppRow = new JPanel(new BorderLayout(6, 0));
        suppRow.setOpaque(false);
        suppCombo = new JComboBox<>(new String[]{"Select a supplier"});
        UITheme.styleComboBox(suppCombo);
        addSuppButton = UITheme.createSecondaryButton("+");
        addSuppButton.setToolTipText("Add a new supplier");
        addSuppButton.setPreferredSize(new Dimension(36, 32));
        addSuppButton.addActionListener(e -> {
            if (dashboard != null) dashboard.addSuppPage();
        });
        suppRow.add(suppCombo, BorderLayout.CENTER);
        suppRow.add(addSuppButton, BorderLayout.EAST);

        codeText = new JTextField();
        UITheme.styleTextField(codeText);

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
        addField(fieldsPanel, "Date Received", jDateChooser1);
        addField(fieldsPanel, "Quantity", quantityText);
        addField(fieldsPanel, "Cost Price", costText);
        addField(fieldsPanel, "Selling Price", sellText);
        addField(fieldsPanel, "Brand / Manufacturer", brandText);

        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formCard.add(formScroll, BorderLayout.CENTER);

        // Form Action Buttons Panel
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        actionPanel.setOpaque(false);

        addButton = UITheme.createSuccessButton("Add Product");
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

        // Top Toolbar: Search + Refresh
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel catalogTitle = new JLabel("Product Catalog");
        catalogTitle.setFont(UITheme.FONT_HEADER);
        catalogTitle.setForeground(UITheme.TEXT_PRIMARY);
        toolbar.add(catalogTitle, BorderLayout.WEST);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBox.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UITheme.FONT_BOLD);
        searchLabel.setForeground(UITheme.TEXT_MUTED);

        searchText = new JTextField(16);
        UITheme.styleTextField(searchText);
        searchText.setToolTipText("Search by product code, name or brand...");
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

        searchBox.add(searchLabel);
        searchBox.add(searchText);
        searchBox.add(refreshButton);
        toolbar.add(searchBox, BorderLayout.EAST);

        tableCard.add(toolbar, BorderLayout.NORTH);

        // Data Table
        productTable = new JTable();
        UITheme.styleTable(productTable);
        productTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleTableClick();
            }
        });

        jScrollPane1 = new JScrollPane(productTable);
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
        productDTO = new ProductDTO();
        if (nameText.getText().trim().isEmpty() || costText.getText().trim().isEmpty()
                || sellText.getText().trim().isEmpty() || brandText.getText().trim().isEmpty()
                || codeText.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all required product details.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            productDTO.setProdCode(codeText.getText().trim());
            productDTO.setProdName(nameText.getText().trim());
            productDTO.setDate(jDateChooser1.getDate() != null ? jDateChooser1.getDate().toString() : "");
            productDTO.setQuantity(quantityText.getText().trim().isEmpty() ? 0 : Integer.parseInt(quantityText.getText().trim()));
            productDTO.setCostPrice(Double.parseDouble(costText.getText().trim()));
            productDTO.setSellPrice(Double.parseDouble(sellText.getText().trim()));
            productDTO.setBrand(brandText.getText().trim());
            productDTO.setUserID(userID);

            new ProductDAO().addProductDAO(productDTO);
            loadDataSet();
            performClear();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity, Cost Price, and Selling Price must be valid numbers.",
                    "Input Format Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performEdit() {
        if (productTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table to edit.",
                    "No Product Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nameText.getText().trim().isEmpty() || costText.getText().trim().isEmpty()
                || sellText.getText().trim().isEmpty() || brandText.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            productDTO = new ProductDTO();
            productDTO.setProdCode(codeText.getText().trim());
            productDTO.setProdName(nameText.getText().trim());
            productDTO.setDate(jDateChooser1.getDate() != null ? jDateChooser1.getDate().toString() : "");
            productDTO.setQuantity(quantityText.getText().trim().isEmpty() ? 0 : Integer.parseInt(quantityText.getText().trim()));
            productDTO.setCostPrice(Double.parseDouble(costText.getText().trim()));
            productDTO.setSellPrice(Double.parseDouble(sellText.getText().trim()));
            productDTO.setBrand(brandText.getText().trim());
            productDTO.setUserID(userID);

            new ProductDAO().editProdDAO(productDTO);
            loadDataSet();
            performClear();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid number entered in numerical fields.",
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDelete() {
        if (productTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table to delete.",
                    "No Product Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?\nThis cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {
            String prodCode = productTable.getValueAt(productTable.getSelectedRow(), 0).toString();
            new ProductDAO().deleteProductDAO(prodCode);
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
        int row = productTable.getSelectedRow();
        if (row < 0) return;
        int col = productTable.getColumnCount();

        Object[] data = new Object[col];
        for (int i = 0; i < col; i++) {
            data[i] = productTable.getValueAt(row, i);
        }

        if (data.length > 0 && data[0] != null) codeText.setText(data[0].toString());
        if (data.length > 1 && data[1] != null) nameText.setText(data[1].toString());
        if (data.length > 2 && data[2] != null) costText.setText(data[2].toString());
        if (data.length > 3 && data[3] != null) sellText.setText(data[3].toString());
        if (data.length > 4 && data[4] != null) brandText.setText(data[4].toString());
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
            productTable.setModel(productDAO.buildTableModel(productDAO.getQueryResult()));
            UITheme.styleTable(productTable);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadSearchData(String text) {
        try {
            ProductDAO productDAO = new ProductDAO();
            productTable.setModel(productDAO.buildTableModel(productDAO.getProductSearch(text)));
            UITheme.styleTable(productTable);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
