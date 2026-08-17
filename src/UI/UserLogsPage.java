package com.inventory.UI;

import com.inventory.DAO.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Modern UserLogsPage for auditing user authentication sessions and timestamp logs.
 */
public class UserLogsPage extends javax.swing.JPanel {

    private JTable logTable;
    private JScrollPane jScrollPane1;
    private JButton refreshButton;
    private JTextField searchText;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public UserLogsPage() {
        initComponents();
        loadDataSet();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_DARK);
        setOpaque(true);

        // Header Panel
        JPanel headerPanel = UITheme.createHeaderPanel("User Activity & Audit Logs", "Track user sign-in and sign-out sessions with precise system timestamps.");
        add(headerPanel, BorderLayout.NORTH);

        // Main Card
        JPanel mainCard = UITheme.createCardPanel(16, 16, 16, 16);
        mainCard.setLayout(new BorderLayout(0, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel tableTitle = new JLabel("Authentication Session Logs");
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
        searchText.setToolTipText("Filter user logs by username or timestamp...");
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

        refreshButton = UITheme.createSecondaryButton("Refresh Logs");
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

        // Table
        logTable = new JTable();
        UITheme.styleTable(logTable);

        jScrollPane1 = new JScrollPane(logTable);
        jScrollPane1.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));
        mainCard.add(jScrollPane1, BorderLayout.CENTER);

        add(mainCard, BorderLayout.CENTER);
    }

    public void loadDataSet() {
        try {
            UserDAO userDAO = new UserDAO();
            DefaultTableModel model = (DefaultTableModel) userDAO.buildTableModel(userDAO.getUserLogsDAO());
            logTable.setModel(model);
            UITheme.styleTable(logTable);

            rowSorter = new TableRowSorter<>(model);
            logTable.setRowSorter(rowSorter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
