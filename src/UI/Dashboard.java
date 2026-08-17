package com.inventory.UI;

import com.inventory.DAO.UserDAO;
import com.inventory.DTO.UserDTO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Main Dashboard frame for Inventory Management System.
 */
public class Dashboard extends javax.swing.JFrame {

    private CardLayout layout;
    private String userSelect;
    private String username;
    private UserDTO userDTO;
    private LocalDateTime outTime;

    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel topHeaderPanel;
    private JPanel displayPanel;

    private JLabel pageTitleLabel;
    private JLabel userProfileLabel;
    private JLabel clockLabel;

    private JButton homeButton;
    private JButton prodButton;
    private JButton stockButton;
    private JButton custButton;
    private JButton suppButton;
    private JButton salesButton;
    private JButton purchaseButton;
    private JButton usersButton;
    private JButton logsButton;
    private JButton logoutButton;

    private List<JButton> navButtons = new ArrayList<>();
    private JButton currentActiveButton;

    public Dashboard(String username, String userType, UserDTO userDTO) {
        this.username = username;
        this.userSelect = userType;
        this.userDTO = userDTO;

        UITheme.setupGlobalTheme();
        initComponents();

        if ("EMPLOYEE".equalsIgnoreCase(userType)) {
            notForEmployee();
        }
        currentUserSession();

        // Default to Home page
        setActiveNav(homeButton, "Home", "Overview & Analytics");

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                recordLogout();
                super.windowClosing(e);
            }
        });

        setTitle("Inventory Management System");
        setVisible(true);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_DARK);

        // ==================== SIDEBAR ====================
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(240, 0));
        sidebarPanel.setBackground(UITheme.BG_SIDEBAR);
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBorder(new MatteBorder(0, 0, 0, 1, UITheme.BORDER_COLOR));

        // Brand Area (Top of Sidebar)
        JPanel brandArea = new JPanel();
        brandArea.setLayout(new BoxLayout(brandArea, BoxLayout.Y_AXIS));
        brandArea.setBackground(UITheme.BG_SIDEBAR);
        brandArea.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel brandLogo = new JLabel("IMS PRO");
        brandLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandLogo.setForeground(UITheme.TEXT_PRIMARY);

        JLabel brandSub = new JLabel("INVENTORY MANAGEMENT");
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        brandSub.setForeground(UITheme.ACCENT);

        brandArea.add(brandLogo);
        brandArea.add(Box.createVerticalStrut(4));
        brandArea.add(brandSub);

        sidebarPanel.add(brandArea, BorderLayout.NORTH);

        // Navigation Items List
        JPanel navList = new JPanel();
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));
        navList.setBackground(UITheme.BG_SIDEBAR);
        navList.setBorder(new EmptyBorder(8, 12, 12, 12));

        homeButton = createNavButton("Dashboard", "Home", "Overview & Analytics");
        prodButton = createNavButton("Products", "Products", "Product Inventory");
        stockButton = createNavButton("Current Stock", "Current Stock", "Stock Levels & Alerts");
        custButton = createNavButton("Customers", "Customers", "Customer Directory");
        suppButton = createNavButton("Suppliers", "Suppliers", "Supplier Directory");
        salesButton = createNavButton("Sales Orders", "Sales", "Sales & Invoicing");
        purchaseButton = createNavButton("Purchase Orders", "Purchase", "Supply Procurement");
        usersButton = createNavButton("User Accounts", "Users", "Access Control & Roles");
        logsButton = createNavButton("Audit Logs", "Logs", "User Activity Logs");

        navList.add(homeButton);
        navList.add(Box.createVerticalStrut(4));
        navList.add(prodButton);
        navList.add(Box.createVerticalStrut(4));
        navList.add(stockButton);
        navList.add(Box.createVerticalStrut(4));
        navList.add(custButton);
        navList.add(Box.createVerticalStrut(4));
        navList.add(suppButton);
        navList.add(Box.createVerticalStrut(4));
        navList.add(salesButton);
        navList.add(Box.createVerticalStrut(4));
        navList.add(purchaseButton);
        navList.add(Box.createVerticalStrut(4));
        navList.add(usersButton);
        navList.add(Box.createVerticalStrut(4));
        navList.add(logsButton);

        JScrollPane navScrollPane = new JScrollPane(navList);
        navScrollPane.setBorder(null);
        navScrollPane.setOpaque(false);
        navScrollPane.getViewport().setOpaque(false);
        navScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarPanel.add(navScrollPane, BorderLayout.CENTER);

        // Sidebar Footer
        JPanel sidebarFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        sidebarFooter.setBackground(UITheme.BG_SIDEBAR);
        sidebarFooter.setBorder(new MatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        JLabel verLabel = new JLabel("System Online - v2.0");
        verLabel.setFont(UITheme.FONT_SMALL);
        verLabel.setForeground(UITheme.SUCCESS);
        sidebarFooter.add(verLabel);
        sidebarPanel.add(sidebarFooter, BorderLayout.SOUTH);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // ==================== RIGHT CONTENT CONTAINER ====================
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(UITheme.BG_DARK);

        // ==================== TOP HEADER BAR ====================
        topHeaderPanel = new JPanel(new BorderLayout());
        topHeaderPanel.setPreferredSize(new Dimension(0, 64));
        topHeaderPanel.setBackground(UITheme.BG_SIDEBAR);
        topHeaderPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(12, 24, 12, 24)
        ));

        // Breadcrumb / Page Title
        pageTitleLabel = new JLabel("Overview & Analytics");
        pageTitleLabel.setFont(UITheme.FONT_HEADER);
        pageTitleLabel.setForeground(UITheme.TEXT_PRIMARY);
        topHeaderPanel.add(pageTitleLabel, BorderLayout.WEST);

        // Header Right: Clock + User Chip + Logout
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        headerRight.setOpaque(false);

        // Live Clock / Date
        clockLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")));
        clockLabel.setFont(UITheme.FONT_SMALL);
        clockLabel.setForeground(UITheme.TEXT_MUTED);

        // User profile badge
        userProfileLabel = new JLabel("User: Loading...");
        userProfileLabel.setFont(UITheme.FONT_BOLD);
        userProfileLabel.setForeground(UITheme.TEXT_PRIMARY);
        userProfileLabel.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        // Sign Out Button
        logoutButton = UITheme.createDangerButton("Sign Out");
        logoutButton.setFont(UITheme.FONT_SMALL);
        logoutButton.addActionListener(e -> performLogout());

        headerRight.add(clockLabel);
        headerRight.add(userProfileLabel);
        headerRight.add(logoutButton);

        topHeaderPanel.add(headerRight, BorderLayout.EAST);
        rightContainer.add(topHeaderPanel, BorderLayout.NORTH);

        // ==================== DISPLAY PANEL (CardLayout) ====================
        layout = new CardLayout();
        displayPanel = new JPanel(layout);
        displayPanel.setBackground(UITheme.BG_DARK);
        displayPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Register Pages
        displayPanel.add("Home", new HomePage(username));
        displayPanel.add("Users", new UsersPage());
        displayPanel.add("Customers", new CustomerPage());
        displayPanel.add("Products", new ProductPage(username, this));
        displayPanel.add("Suppliers", new SupplierPage());
        displayPanel.add("Current Stock", new CurrentStockPage(username));
        displayPanel.add("Sales", new SalesPage(username, this));
        displayPanel.add("Purchase", new PurchasePage(this));
        displayPanel.add("Logs", new UserLogsPage());

        rightContainer.add(displayPanel, BorderLayout.CENTER);
        mainPanel.add(rightContainer, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JButton createNavButton(String text, String cardName, String titleName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(UITheme.TEXT_MUTED);
        btn.setBackground(UITheme.BG_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(216, 40));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        btn.addActionListener(e -> setActiveNav(btn, cardName, titleName));

        navButtons.add(btn);
        return btn;
    }

    private void setActiveNav(JButton btn, String cardName, String titleName) {
        for (JButton b : navButtons) {
            b.setBackground(UITheme.BG_SIDEBAR);
            b.setForeground(UITheme.TEXT_MUTED);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            b.setBorder(new EmptyBorder(8, 14, 8, 14));
        }

        btn.setBackground(new Color(30, 41, 59));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new CompoundBorder(
                new MatteBorder(0, 3, 0, 0, UITheme.PRIMARY),
                new EmptyBorder(8, 11, 8, 14)
        ));

        currentActiveButton = btn;
        pageTitleLabel.setText(titleName);
        layout.show(displayPanel, cardName);
    }

    // Public navigation methods for compatibility
    public void addHomePage() {
        setActiveNav(homeButton, "Home", "Overview & Analytics");
    }

    public void addUsersPage() {
        setActiveNav(usersButton, "Users", "User Accounts & Permissions");
    }

    public void addCustPage() {
        setActiveNav(custButton, "Customers", "Customer Management");
    }

    public void addProdPage() {
        setActiveNav(prodButton, "Products", "Product Inventory");
    }

    public void addSuppPage() {
        setActiveNav(suppButton, "Suppliers", "Supplier Directory");
    }

    public void addStockPage() {
        setActiveNav(stockButton, "Current Stock", "Current Stock Levels");
    }

    public void addSalesPage() {
        setActiveNav(salesButton, "Sales", "Sales Orders & Billing");
    }

    public void addPurchasePage() {
        setActiveNav(purchaseButton, "Purchase", "Procurement & Purchase Orders");
    }

    public void addLogsPage() {
        setActiveNav(logsButton, "Logs", "System Audit Logs");
    }

    public void currentUserSession() {
        UserDTO dto = new UserDTO();
        new UserDAO().getFullName(dto, username);
        String name = (dto.getFullName() != null && !dto.getFullName().isEmpty()) ? dto.getFullName() : username;
        userProfileLabel.setText(name + "  |  " + userSelect);
    }

    public void notForEmployee() {
        usersButton.setVisible(false);
        logsButton.setVisible(false);
    }

    private void recordLogout() {
        outTime = LocalDateTime.now();
        userDTO.setOutTime(String.valueOf(outTime));
        userDTO.setUsername(username);
        new UserDAO().addUserLogin(userDTO);
    }

    private void performLogout() {
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out of the system?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {
            recordLogout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        }
    }
}
