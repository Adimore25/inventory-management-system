package com.inventory.UI;

import com.inventory.DTO.UserDTO;
import com.inventory.Database.ConnectionFactory;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;

/**
 * Modern LoginPage for Inventory Management System.
 */
public class LoginPage extends javax.swing.JFrame {

    UserDTO userDTO;
    LocalDateTime inTime;
    String userType;

    private JTextField userText;
    private JPasswordField passText;
    private JComboBox<String> jComboBox1;
    private JButton loginButton;
    private JButton clearButton;

    public LoginPage() {
        UITheme.setupGlobalTheme();
        initComponents();
        userDTO = new UserDTO();
    }

    private void initComponents() {
        setTitle("Inventory Management System - Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(780, 480);
        setResizable(false);
        setLocationRelativeTo(null);

        // Root container with split layout
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.BG_DARK);

        // --- Left Brand Banner Panel ---
        JPanel brandPanel = new JPanel();
        brandPanel.setPreferredSize(new Dimension(320, 480));
        brandPanel.setBackground(new Color(17, 24, 39));
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(new EmptyBorder(50, 36, 40, 36));

        // Brand Icon / Badge
        JLabel badgeLabel = new JLabel("IMS PRO");
        badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badgeLabel.setForeground(UITheme.ACCENT);
        badgeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandTitle = new JLabel("<html>Store<br>Inventory<br>Manager</html>");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        brandTitle.setForeground(UITheme.TEXT_PRIMARY);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandDesc = new JLabel("<html>Streamline your inventory tracking, supply orders, customer sales, and analytics in real time.</html>");
        brandDesc.setFont(UITheme.FONT_SUBTITLE);
        brandDesc.setForeground(UITheme.TEXT_MUTED);
        brandDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel securityBadge = new JLabel("Enterprise Role-Based Access");
        securityBadge.setFont(UITheme.FONT_SMALL);
        securityBadge.setForeground(UITheme.TEXT_MUTED);
        securityBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(badgeLabel);
        brandPanel.add(Box.createVerticalStrut(16));
        brandPanel.add(brandTitle);
        brandPanel.add(Box.createVerticalStrut(18));
        brandPanel.add(brandDesc);
        brandPanel.add(Box.createVerticalGlue());
        brandPanel.add(securityBadge);

        rootPanel.add(brandPanel, BorderLayout.WEST);

        // --- Right Form Panel ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(UITheme.BG_CARD);
        formContainer.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setOpaque(false);
        formBox.setPreferredSize(new Dimension(340, 380));

        JLabel loginTitle = new JLabel("Sign In");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel loginSubtitle = new JLabel("Enter your credentials to access your account");
        loginSubtitle.setFont(UITheme.FONT_SUBTITLE);
        loginSubtitle.setForeground(UITheme.TEXT_MUTED);

        // Username Field
        JLabel userLabel = UITheme.createFieldLabel("Username");
        userText = new JTextField();
        UITheme.styleTextField(userText);
        userText.setPreferredSize(new Dimension(340, 36));
        userText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Password Field
        JLabel passLabel = UITheme.createFieldLabel("Password");
        passText = new JPasswordField();
        UITheme.stylePasswordField(passText);
        passText.setPreferredSize(new Dimension(340, 36));
        passText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // User Type Field
        JLabel roleLabel = UITheme.createFieldLabel("Role / User Type");
        jComboBox1 = new JComboBox<>(new String[]{"ADMINISTRATOR", "EMPLOYEE"});
        UITheme.styleComboBox(jComboBox1);
        jComboBox1.setPreferredSize(new Dimension(340, 36));
        jComboBox1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Action Buttons
        loginButton = UITheme.createPrimaryButton("SIGN IN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(340, 40));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        clearButton = UITheme.createSecondaryButton("CLEAR");
        clearButton.setPreferredSize(new Dimension(340, 34));
        clearButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        // Key Listeners for Enter key
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        userText.addKeyListener(enterKeyListener);
        passText.addKeyListener(enterKeyListener);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userText.setText("");
                passText.setText("");
                userText.requestFocus();
            }
        });

        // Assemble Form
        formBox.add(loginTitle);
        formBox.add(Box.createVerticalStrut(4));
        formBox.add(loginSubtitle);
        formBox.add(Box.createVerticalStrut(22));

        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userText.setAlignmentX(Component.LEFT_ALIGNMENT);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passText.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        jComboBox1.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        formBox.add(userLabel);
        formBox.add(Box.createVerticalStrut(6));
        formBox.add(userText);
        formBox.add(Box.createVerticalStrut(14));

        formBox.add(passLabel);
        formBox.add(Box.createVerticalStrut(6));
        formBox.add(passText);
        formBox.add(Box.createVerticalStrut(14));

        formBox.add(roleLabel);
        formBox.add(Box.createVerticalStrut(6));
        formBox.add(jComboBox1);
        formBox.add(Box.createVerticalStrut(24));

        formBox.add(loginButton);
        formBox.add(Box.createVerticalStrut(8));
        formBox.add(clearButton);

        formContainer.add(formBox);
        rootPanel.add(formContainer, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private void performLogin() {
        String username = userText.getText().trim();
        String password = new String(passText.getPassword());
        userType = (String) jComboBox1.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Verifying...");

        SwingUtilities.invokeLater(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            if (!factory.isConnected()) {
                JOptionPane.showMessageDialog(this,
                        "<html><b>Database Connection Failed!</b><br><br>" +
                        "Could not connect to the MySQL database at <code>localhost:3306/inventory</code>.<br><br>" +
                        "<b>Please verify:</b><br>" +
                        "1. Your MySQL server (or XAMPP/WAMP MySQL) is started and running.<br>" +
                        "2. The <code>inventory</code> database has been created or imported from <code>SQL/InventoryDB.sql</code>.<br>" +
                        "3. The credentials in <code>lib/DBCredentials.xml</code> match your MySQL root password.</html>",
                        "Database Offline",
                        JOptionPane.ERROR_MESSAGE);
                loginButton.setEnabled(true);
                loginButton.setText("SIGN IN");
                return;
            }

            boolean success = factory.checkLogin(username, password, userType);
            if (success) {
                inTime = LocalDateTime.now();
                userDTO.setInTime(String.valueOf(inTime));
                dispose();
                new Dashboard(username, userType, userDTO);
            } else {
                JOptionPane.showMessageDialog(this,
                        "<html><b>Invalid Credentials</b><br><br>" +
                        "The username, password, or role does not match any record in the database.<br>" +
                        "Default admin: <code>User1</code> / <code>Aditya1</code> or <code>root</code> / <code>root</code></html>",
                        "Authentication Failed",
                        JOptionPane.WARNING_MESSAGE);
                loginButton.setEnabled(true);
                loginButton.setText("SIGN IN");
            }
        });
    }

    public static void main(String[] args) {
        UITheme.setupGlobalTheme();
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
