package com.inventory.UI;

import com.inventory.DAO.ProductDAO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Modern CurrentStockPage for monitoring live inventory stock levels and threshold alerts.
 */
public class CurrentStockPage extends javax.swing.JPanel {

    private String username;
    private JTable stockTable;
    private JScrollPane jScrollPane1;
    private JButton refreshButton;
    private JTextField searchText;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public CurrentStockPage(String username) {
        this.username = username;
        initComponents();
        loadDataSet();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_DARK);
        setOpaque(true);

        // Header Panel with title and subtitle
        JPanel headerPanel = UITheme.createHeaderPanel("Current Stock Levels", "Monitor warehouse stock quantities, low-stock alerts, and product availability.");
        add(headerPanel, BorderLayout.NORTH);

        // Main Card containing Table and Toolbar
        JPanel mainCard = UITheme.createCardPanel(16, 16, 16, 16);
        mainCard.setLayout(new BorderLayout(0, 12));

        // Toolbar: Search + Quick stats + Refresh
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel tableTitle = new JLabel("Live Warehouse Inventory");
        tableTitle.setFont(UITheme.FONT_HEADER);
        tableTitle.setForeground(UITheme.TEXT_PRIMARY);
        toolbar.add(tableTitle, BorderLayout.WEST);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        JLabel searchIcon = new JLabel("Search:");
        searchIcon.setFont(UITheme.FONT_BOLD);
        searchIcon.setForeground(UITheme.TEXT_MUTED);

        searchText = new JTextField(16);
        UITheme.styleTextField(searchText);
        searchText.setToolTipText("Filter current stock...");
        searchText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchText.getText().trim();
                if (rowSorter != null) {
                    if (text.isEmpty()) {
                        rowSorter.setRowFilter(null);
                    } else {
                        rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    }
                }
            }
        });

        refreshButton = UITheme.createSecondaryButton("Refresh Stock");
        refreshButton.setFont(UITheme.FONT_SMALL);
        refreshButton.addActionListener(e -> {
            loadDataSet();
            searchText.setText("");
        });

        rightControls.add(searchIcon);
        rightControls.add(searchText);
        rightControls.add(refreshButton);
        toolbar.add(rightControls, BorderLayout.EAST);

        mainCard.add(toolbar, BorderLayout.NORTH);

        // Table setup
        stockTable = new JTable();
        UITheme.styleTable(stockTable);

        jScrollPane1 = new JScrollPane(stockTable);
        jScrollPane1.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));
        mainCard.add(jScrollPane1, BorderLayout.CENTER);

        add(mainCard, BorderLayout.CENTER);
    }

    public void loadDataSet() {
        try {
            ProductDAO productDAO = new ProductDAO();
            DefaultTableModel model = (DefaultTableModel) productDAO.buildTableModel(productDAO.getCurrentStockInfo());
            stockTable.setModel(model);
            UITheme.styleTable(stockTable);

            // Setup custom stock level renderer
            DefaultTableCellRenderer stockRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        c.setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_CARD_ALT);
                        c.setForeground(UITheme.TEXT_PRIMARY);
                    }

                    // If quantity column, apply color highlighting
                    if (value != null) {
                        try {
                            int qty = Integer.parseInt(value.toString().trim());
                            if (qty < 15) {
                                c.setForeground(new Color(239, 68, 68)); // Low stock red
                                setFont(new Font("Segoe UI", Font.BOLD, 13));
                            } else if (qty < 30) {
                                c.setForeground(new Color(245, 158, 11)); // Medium stock amber
                            } else {
                                c.setForeground(new Color(16, 185, 129)); // Good stock green
                            }
                        } catch (NumberFormatException ignored) {}
                    }

                    setBorder(new EmptyBorder(0, 10, 0, 10));
                    return c;
                }
            };

            for (int i = 0; i < stockTable.getColumnCount(); i++) {
                stockTable.getColumnModel().getColumn(i).setCellRenderer(stockRenderer);
            }

            rowSorter = new TableRowSorter<>(model);
            stockTable.setRowSorter(rowSorter);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
