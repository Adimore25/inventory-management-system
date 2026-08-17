package com.inventory.UI;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UITheme: Centralized Design System and Theme Utilities for IMS.
 * Provides modern color schemes, typography, card containers, and styled components.
 */
public class UITheme {

    // --- Modern Palette ---
    public static final Color BG_DARK = new Color(15, 23, 42);         // #0F172A - Deep slate
    public static final Color BG_SIDEBAR = new Color(15, 23, 42);      // #0F172A - Dark Sidebar
    public static final Color BG_CARD = new Color(30, 41, 59);         // #1E293B - Slate Card
    public static final Color BG_CARD_ALT = new Color(24, 33, 47);     // Slightly darker card
    public static final Color BG_INPUT = new Color(15, 23, 42);        // #0F172A - Input background
    public static final Color BG_HOVER = new Color(51, 65, 85);        // #334155 - Hover

    public static final Color BORDER_COLOR = new Color(51, 65, 85);    // #334155 - Slate border
    public static final Color BORDER_LIGHT = new Color(71, 85, 105);   // #475569

    public static final Color PRIMARY = new Color(99, 102, 241);       // #6366F1 - Indigo
    public static final Color PRIMARY_HOVER = new Color(79, 70, 229); // #4F46E5
    public static final Color ACCENT = new Color(6, 182, 212);         // #06B6D4 - Cyan
    public static final Color SUCCESS = new Color(16, 185, 129);       // #10B981 - Emerald Green
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);
    public static final Color DANGER = new Color(239, 68, 68);         // #EF4444 - Rose Red
    public static final Color DANGER_HOVER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(245, 158, 11);       // #F59E0B - Amber
    public static final Color WARNING_HOVER = new Color(217, 119, 6);
    public static final Color SECONDARY = new Color(51, 65, 85);       // #334155 - Slate
    public static final Color SECONDARY_HOVER = new Color(71, 85, 105);

    public static final Color TEXT_PRIMARY = new Color(248, 250, 252); // #F8FAFC
    public static final Color TEXT_MUTED = new Color(148, 163, 184);   // #94A3B8
    public static final Color TEXT_DARK = new Color(15, 23, 42);

    // --- Typography (Clean Segoe UI hierarchy) ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_PAGE_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT_VAL = new Font("Segoe UI", Font.BOLD, 26);

    /**
     * Initializes global FlatLaf Look and Feel and UI manager defaults
     */
    public static void setupGlobalTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMaterialDarkerIJTheme());
            
            // Customize FlatLaf properties
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("Table.rowHeight", 34);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.gridColor", new Color(40, 52, 71));
            UIManager.put("Table.selectionBackground", new Color(79, 70, 229, 180));
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("TableHeader.height", 38);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
            UIManager.put("TableHeader.background", new Color(20, 29, 44));
            UIManager.put("TableHeader.foreground", TEXT_MUTED);
            UIManager.put("Panel.background", BG_DARK);
            UIManager.put("OptionPane.background", BG_CARD);
            UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a styled card container panel
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static JPanel createCardPanel(int padTop, int padLeft, int padBottom, int padRight) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(padTop, padLeft, padBottom, padRight)
        ));
        return panel;
    }

    /**
     * Standard Styled Button Builder with smooth hover effects
     */
    public static JButton createStyledButton(String text, Color bg, Color hoverBg, Color fg, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font != null ? font : FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                }
            }
        });

        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_HOVER, Color.WHITE, FONT_BOLD);
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS, SUCCESS_HOVER, Color.WHITE, FONT_BOLD);
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER, DANGER_HOVER, Color.WHITE, FONT_BOLD);
    }

    public static JButton createWarningButton(String text) {
        return createStyledButton(text, WARNING, WARNING_HOVER, Color.WHITE, FONT_BOLD);
    }

    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, SECONDARY, SECONDARY_HOVER, TEXT_PRIMARY, FONT_BOLD);
    }

    /**
     * Styles text field with clean padding and border
     */
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    /**
     * Styles password field
     */
    public static void stylePasswordField(JPasswordField field) {
        field.setFont(FONT_BODY);
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    /**
     * Styles combo box
     */
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FONT_BODY);
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new LineBorder(BORDER_COLOR, 1, true));
    }

    /**
     * Styles standard form labels
     */
    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    /**
     * Creates a consistent Page Header Panel
     */
    public static JPanel createHeaderPanel(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout(10, 2));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_PAGE_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(FONT_SUBTITLE);
        subLabel.setForeground(TEXT_MUTED);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(subLabel);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    /**
     * Configures modern styling for a JTable
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setRowHeight(34);
        table.setSelectionBackground(new Color(99, 102, 241, 160));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setGridColor(new Color(45, 58, 79));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(20, 29, 44));
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Custom Cell Renderer with left/right padding and zebra striping
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : BG_CARD_ALT);
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(new Color(99, 102, 241, 190));
                    c.setForeground(Color.WHITE);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }
    }

    /**
     * Creates a KPI Stat Card Panel for the Home/Analytics page
     */
    public static JPanel createStatCard(String title, String value, String subtitle, String iconSymbol, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(12, 8));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        // Left color accent bar
        JPanel leftBar = new JPanel();
        leftBar.setBackground(accentColor);
        leftBar.setPreferredSize(new Dimension(4, 0));
        card.add(leftBar, BorderLayout.WEST);

        // Main content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(TEXT_MUTED);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(FONT_STAT_VAL);
        valLbl.setForeground(TEXT_PRIMARY);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(accentColor);

        content.add(titleLbl);
        content.add(Box.createVerticalStrut(4));
        content.add(valLbl);
        content.add(Box.createVerticalStrut(2));
        content.add(subLbl);

        card.add(content, BorderLayout.CENTER);

        // Right Icon / Symbol
        if (iconSymbol != null && !iconSymbol.isEmpty()) {
            JLabel iconLbl = new JLabel(iconSymbol);
            iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            iconLbl.setForeground(TEXT_MUTED);
            card.add(iconLbl, BorderLayout.EAST);
        }

        return card;
    }
}
