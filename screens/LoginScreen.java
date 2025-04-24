


package screens;

import utils.SQLDB;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class LoginScreen extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JPanel mainPanel;
    private BufferedImage bgImage;

    private final Color yellowPanel = new Color(255, 230, 167, 220); // translucent yellow
    private final Color buttonDark = new Color(31, 31, 31);          // dark black
    private final Color buttonText = new Color(255, 230, 167);       // yellow text

    public LoginScreen() {
        setTitle("CineVerse");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            bgImage = ImageIO.read(getClass().getResource("/assets/bg.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        initUI();
        setVisible(true);
    }

    private void initUI() {
        mainPanel = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("CINEVERSE", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(buttonDark);
        titleLabel.setBounds(0, 80, 400, 30);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Your ticket to blockbusters, every day.", JLabel.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(buttonDark);
        subtitleLabel.setBounds(0, 115, 400, 20);
        mainPanel.add(subtitleLabel);

        JPanel loginCard = createRoundedPanel(yellowPanel, 20);
        loginCard.setLayout(null);
        loginCard.setBounds(30, 170, 340, 250);
        mainPanel.add(loginCard);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setForeground(buttonDark);
        userLabel.setBounds(25, 25, 290, 20);
        loginCard.add(userLabel);

        userField = createTextField();
        userField.setBounds(25, 50, 290, 45);
        loginCard.add(userField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passLabel.setForeground(buttonDark);
        passLabel.setBounds(25, 110, 290, 20);
        loginCard.add(passLabel);

        passField = createPasswordField();
        passField.setBounds(25, 135, 290, 45);
        loginCard.add(passField);

        loginBtn = createButton("Login", buttonDark, buttonText);
        loginBtn.setBounds(30, 450, 340, 50);
        loginBtn.addActionListener(e -> handleLogin());
        mainPanel.add(loginBtn);

        // Register link
        JLabel registerLink = new JLabel("Don't have an account? Register here", JLabel.CENTER);
        registerLink.setFont(new Font("SansSerif", Font.PLAIN, 14));
        registerLink.setForeground(buttonDark);
        registerLink.setBounds(0, 520, 400, 30);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterScreen();
            }
        });
        mainPanel.add(registerLink);

        // Footer
        JLabel footer = new JLabel("© 2025 CineVerse • All Rights Reserved", JLabel.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setForeground(buttonDark);
        footer.setBounds(0, 630, 400, 20);
        mainPanel.add(footer);
    }

    private JPanel createRoundedPanel(Color background, int radius) {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(background);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }

    private JButton createButton(String text, Color bgColor, Color txtColor) {
        JButton button = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(txtColor);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(text, g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Replace the handleLogin method in LoginScreen.java with this updated version:

private void handleLogin() {
    String uname = userField.getText().trim();
    String pwd = new String(passField.getPassword()).trim();

    if (uname.isEmpty() || pwd.isEmpty()) {
        showMessage("Please enter both fields.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try (Connection conn = SQLDB.connect();
         PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE uname=? AND pwd=?")) {
        ps.setString(1, uname);
        ps.setString(2, pwd);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String username = rs.getString("uname");
            if (username.equalsIgnoreCase("admin")) {
                new AdminDashboard();
            } else {
                new UserDashboard(username);  // Redirect to the UserDashboard
            }
            dispose();
        } else {
            showMessage("Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        showMessage("Database error.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void showMessage(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}