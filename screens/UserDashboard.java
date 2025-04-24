

package screens;

import utils.SQLDB;
import panels.UserMoviesPanel;
import panels.UserBookingsPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
public class UserDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private boolean menuVisible = false;
    
    private String username;
    
    // Colors
    private final Color bgColor = Color.WHITE;
    private final Color menuColor = new Color(228,67,40);  // Red color #e44328
    private final Color accentColor = new Color(228, 67, 40);  // Red color #e44328
    private final Color selectedColor = new Color(228, 67, 40); // Red color #e44328

    // Panels
    private UserMoviesPanel moviesPanel;
    private UserBookingsPanel bookingsPanel;

    public UserDashboard(String username) {
        this.username = username;
        
        setTitle("CineVerse");
        setSize(375, 667); // Mobile-friendly size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initUI();
        setVisible(true);
    }
    
    private void initUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        setContentPane(mainPanel);
        
        // Create header with hamburger menu
        setupHeader();
        
        // Create slide-out menu
        setupMenu();
        
        // Create content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(bgColor);
        
        // Initialize panels
        moviesPanel = new UserMoviesPanel(username);
        bookingsPanel = new UserBookingsPanel(username);
        
        // Add panels to card layout
        contentPanel.add(moviesPanel, "MOVIES");
        contentPanel.add(bookingsPanel, "BOOKINGS");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Show movies panel by default
        cardLayout.show(contentPanel, "MOVIES");
        
        // Add auto-refresh timer (refresh every 30 seconds)
        Timer refreshTimer = new Timer(30000, e -> {
            refreshCurrentPanel();
        });
        refreshTimer.start();
    }
    
    private void refreshCurrentPanel() {
        if (contentPanel.isVisible()) {
            String currentCard = getCurrentCardName();
            if ("MOVIES".equals(currentCard)) {
                moviesPanel.refreshMovies();
            } else if ("BOOKINGS".equals(currentCard)) {
                bookingsPanel.loadBookings();
            }
        }
    }
    
    private String getCurrentCardName() {
        // This is a helper method to determine which card is currently showing
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) {
                if (comp == moviesPanel) return "MOVIES";
                if (comp == bookingsPanel) return "BOOKINGS";
            }
        }
        return "MOVIES"; // Default
    }
    
    private void setupHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);  // Red color #e44328
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Hamburger menu button
        JButton menuButton = new JButton("☰");
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        menuButton.setForeground(Color.WHITE);
        menuButton.setBackground(accentColor);  // Red color #e44328
        menuButton.setBorderPainted(false);
        menuButton.setFocusPainted(false);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.addActionListener(e -> toggleMenu());
        
        headerPanel.add(menuButton, BorderLayout.WEST);
        
        // App title
        JLabel appTitle = new JLabel("CINEVERSE");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        appTitle.setForeground(Color.WHITE);
        headerPanel.add(appTitle, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }
    
    private void setupMenu() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(menuColor);  // Red color #e44328
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // User welcome
        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        
        menuPanel.add(welcomeLabel);
        
        // Menu buttons
        addMenuButton("🎬 Browse Movies", "MOVIES");
        addMenuButton("🎟️ My Bookings", "BOOKINGS");
        
        // Spacer
        menuPanel.add(Box.createVerticalGlue());
        
        // Logout button
        JButton logoutBtn = createMenuButton("🚪 Logout", null);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });
        menuPanel.add(logoutBtn);
        
        // Initially hidden
        menuPanel.setVisible(false);
        mainPanel.add(menuPanel, BorderLayout.WEST);
    }
    
    private void toggleMenu() {
        menuVisible = !menuVisible;
        menuPanel.setVisible(menuVisible);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void addMenuButton(String text, String cardName) {
        JButton button = createMenuButton(text, cardName);
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            toggleMenu(); // Hide menu after selection
        });
        menuPanel.add(button);
    }
    
    private JButton createMenuButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(menuColor);  // Red color #e44328
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(50, 50, 50));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (cardName != null && cardLayout.toString().contains(cardName)) {
                    button.setBackground(selectedColor);  // Red color #e44328
                } else {
                    button.setBackground(menuColor);  // Red color #e44328
                }
            }
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard("testuser"));
    }
}