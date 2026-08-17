package com.inventory.UI;

import com.inventory.DAO.CustomerDAO;
import com.inventory.DAO.ProductDAO;
import com.inventory.DAO.SupplierDAO;
import com.inventory.DTO.UserDTO;
import com.inventory.DAO.UserDAO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modern Analytics & Overview Home Page for Inventory Management System.
 */
public class HomePage extends javax.swing.JPanel {

    private String username;

    public HomePage(String username) {
        this.username = username;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_DARK);
        setOpaque(true);

        UserDTO userDTO = new UserDTO();
        new UserDAO().getFullName(userDTO, username);
        String displayName = (userDTO.getFullName() != null && !userDTO.getFullName().isEmpty())
                ? userDTO.getFullName()
                : username;

        // --- Top Welcome Card ---
        JPanel welcomeCard = UITheme.createCardPanel(20, 24, 20, 24);
        welcomeCard.setLayout(new BorderLayout());

        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));
        welcomeTextPanel.setOpaque(false);

        JLabel greetingLabel = new JLabel("Welcome back, " + displayName);
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        greetingLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel dateLabel = new JLabel("Today is " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")) + "  |  System is operating normally");
        dateLabel.setFont(UITheme.FONT_SUBTITLE);
        dateLabel.setForeground(UITheme.TEXT_MUTED);

        welcomeTextPanel.add(greetingLabel);
        welcomeTextPanel.add(Box.createVerticalStrut(4));
        welcomeTextPanel.add(dateLabel);

        welcomeCard.add(welcomeTextPanel, BorderLayout.CENTER);

        // Status badge on right
        JLabel statusPill = new JLabel("ACTIVE SESSION");
        statusPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusPill.setForeground(UITheme.SUCCESS);
        statusPill.setBorder(new CompoundBorder(
                new LineBorder(new Color(16, 185, 129, 80), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        welcomeCard.add(statusPill, BorderLayout.EAST);

        add(welcomeCard, BorderLayout.NORTH);

        // --- Center Content: KPI Stats + Quick Actions ---
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        // Stat Grid (4 KPI Cards)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        statsGrid.setPreferredSize(new Dimension(0, 110));

        int prodCount = fetchProductCount();
        int suppCount = fetchSupplierCount();
        int custCount = fetchCustomerCount();
        int salesCount = fetchSalesCount();

        JPanel card1 = UITheme.createStatCard("Total Products", String.valueOf(prodCount), "Active Catalog items", "", UITheme.PRIMARY);
        JPanel card2 = UITheme.createStatCard("Suppliers", String.valueOf(suppCount), "Verified vendors", "", UITheme.ACCENT);
        JPanel card3 = UITheme.createStatCard("Customers", String.valueOf(custCount), "Registered clients", "", UITheme.SUCCESS);
        JPanel card4 = UITheme.createStatCard("Sales Records", String.valueOf(salesCount), "Completed orders", "", UITheme.WARNING);

        statsGrid.add(card1);
        statsGrid.add(card2);
        statsGrid.add(card3);
        statsGrid.add(card4);

        centerContent.add(statsGrid);
        centerContent.add(Box.createVerticalStrut(20));

        // Two Column Section: Quick Guidelines & System Features
        JPanel infoGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        infoGrid.setOpaque(false);

        // Left Panel: Quick Workflow Guide
        JPanel guideCard = UITheme.createCardPanel(20, 20, 20, 20);
        guideCard.setLayout(new BoxLayout(guideCard, BoxLayout.Y_AXIS));

        JLabel guideTitle = new JLabel("Quick Navigation Guide");
        guideTitle.setFont(UITheme.FONT_HEADER);
        guideTitle.setForeground(UITheme.TEXT_PRIMARY);

        guideCard.add(guideTitle);
        guideCard.add(Box.createVerticalStrut(14));

        guideCard.add(createBulletItem("Products", "Add, edit, search, and update product catalog prices."));
        guideCard.add(Box.createVerticalStrut(8));
        guideCard.add(createBulletItem("Current Stock", "Monitor live warehouse stock quantities and alerts."));
        guideCard.add(Box.createVerticalStrut(8));
        guideCard.add(createBulletItem("Sales Orders", "Process sales transactions and calculate revenue in real time."));
        guideCard.add(Box.createVerticalStrut(8));
        guideCard.add(createBulletItem("Purchase Orders", "Procure new batches from verified suppliers."));

        // Right Panel: Security & Tips
        JPanel tipsCard = UITheme.createCardPanel(20, 20, 20, 20);
        tipsCard.setLayout(new BoxLayout(tipsCard, BoxLayout.Y_AXIS));

        JLabel tipsTitle = new JLabel("System Information & Tips");
        tipsTitle.setFont(UITheme.FONT_HEADER);
        tipsTitle.setForeground(UITheme.TEXT_PRIMARY);

        tipsCard.add(tipsTitle);
        tipsCard.add(Box.createVerticalStrut(14));

        tipsCard.add(createBulletItem("Instant Search", "Type in any search bar to filter records in real-time."));
        tipsCard.add(Box.createVerticalStrut(8));
        tipsCard.add(createBulletItem("Role Protection", "Administrator and Employee roles have distinct access controls."));
        tipsCard.add(Box.createVerticalStrut(8));
        tipsCard.add(createBulletItem("Audit Logs", "All logins and sign-out timestamps are tracked automatically."));
        tipsCard.add(Box.createVerticalStrut(8));
        tipsCard.add(createBulletItem("Auto Sync", "Database updates are committed immediately on each action."));

        infoGrid.add(guideCard);
        infoGrid.add(tipsCard);

        centerContent.add(infoGrid);
        add(centerContent, BorderLayout.CENTER);
    }

    private JPanel createBulletItem(String title, String desc) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BOLD);
        titleLbl.setForeground(UITheme.ACCENT);
        titleLbl.setPreferredSize(new Dimension(140, 20));

        JLabel descLbl = new JLabel("<html>" + desc + "</html>");
        descLbl.setFont(UITheme.FONT_BODY);
        descLbl.setForeground(UITheme.TEXT_MUTED);

        p.add(titleLbl, BorderLayout.WEST);
        p.add(descLbl, BorderLayout.CENTER);
        return p;
    }

    private int fetchProductCount() {
        try {
            ResultSet rs = new ProductDAO().getQueryResult();
            int count = 0;
            while (rs != null && rs.next()) count++;
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    private int fetchSupplierCount() {
        try {
            ResultSet rs = new SupplierDAO().getQueryResult();
            int count = 0;
            while (rs != null && rs.next()) count++;
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    private int fetchCustomerCount() {
        try {
            ResultSet rs = new CustomerDAO().getQueryResult();
            int count = 0;
            while (rs != null && rs.next()) count++;
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    private int fetchSalesCount() {
        try {
            ResultSet rs = new ProductDAO().getSalesInfo();
            int count = 0;
            while (rs != null && rs.next()) count++;
            return count;
        } catch (Exception e) {
            return 0;
        }
    }
}
