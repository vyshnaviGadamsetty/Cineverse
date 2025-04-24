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

public class RegisterScreen extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton registerBtn;
    private BufferedImage bgImage;

    private final Color yellowPanel = new Color(255, 230, 167, 220); // translucent yellow
    private final Color buttonDark = new Color(31, 31, 31);          // dark black
    private final Color buttonText = new Color(255, 230, 167);       // yellow text

    public RegisterScreen() {
        setTitle("CineVerse - Register");
        setSize(375, 667);
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
        JPanel mainPanel = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        setContentPane(mainPanel);

        JLabel title = new JLabel("CINEVERSE", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(buttonDark);
        title.setBounds(0, 80, 375, 30);
        mainPanel.add(title);

        JLabel subtitle = new JLabel("Create your account", JLabel.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(buttonDark);
        subtitle.setBounds(0, 115, 375, 20);
        mainPanel.add(subtitle);

        JPanel formPanel = createRoundedPanel(yellowPanel, 20);
        formPanel.setLayout(null);
        formPanel.setBounds(20, 170, 335, 230);
        mainPanel.add(formPanel);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setForeground(buttonDark);
        userLabel.setBounds(25, 20, 290, 20);
        formPanel.add(userLabel);

        userField = createTextField();
        userField.setBounds(25, 45, 285, 45);
        formPanel.add(userField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passLabel.setForeground(buttonDark);
        passLabel.setBounds(25, 105, 290, 20);
        formPanel.add(passLabel);

        passField = createPasswordField();
        passField.setBounds(25, 130, 285, 45);
        formPanel.add(passField);

        registerBtn = createButton("Register", buttonDark, buttonText);
        registerBtn.setBounds(20, 420, 335, 50);
        registerBtn.addActionListener(e -> handleRegister());
        mainPanel.add(registerBtn);

        // Back to login link
        JLabel backLabel = new JLabel("← Back to Login", JLabel.CENTER);
        backLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backLabel.setForeground(buttonDark);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setBounds(0, 490, 375, 20);
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginScreen(); // 🔁 Go back to login
            }
        });
        mainPanel.add(backLabel);
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

    private void handleRegister() {
        String uname = userField.getText().trim();
        String pwd = new String(passField.getPassword()).trim();

        if (uname.isEmpty() || pwd.isEmpty()) {
            showMessage("Please enter both fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pwd.length() < 6) {
            showMessage("Password must be at least 6 characters.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = SQLDB.connect();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users(uname, pwd) VALUES (?, ?)")) {
            ps.setString(1, uname);
            ps.setString(2, pwd);
            ps.executeUpdate();
            showMessage("Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginScreen(); // 👈 Redirect to login
            dispose();
        } catch (SQLException ex) {
            showMessage("Username already exists. Try another.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            showMessage("Database error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterScreen::new);
    }
}
