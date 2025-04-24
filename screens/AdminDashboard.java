
package screens;

import panels.ManageMoviesPanel;
import panels.AddMoviePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import screens.LoginScreen; // Add this import if LoginScreen is in the screens package

public class AdminDashboard extends JFrame implements ActionListener {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton manageMoviesBtn;
    private JButton addMovieBtn;
    private JButton activeButton;
    private JButton toggleSidebarBtn;
    private JButton logoutBtn;  // Logout button
    private JPanel sidebar;
    private JPanel hamburgerPanel;
    private boolean sidebarVisible = false;  // Start with sidebar hidden
    
    // Updated color scheme
    private final Color bgColor = Color.WHITE;  // White background
    private final Color darkColor = Color.BLACK;   // Black text
    private final Color accentColor = new Color(228, 67, 40); // #e44328 - red
    private final Color hoverColor = new Color(236, 181, 125);  // #ecb57d - light orange
    private final Color textColor = Color.WHITE;
    
    // ManageMoviesPanel reference for refreshing
    private ManageMoviesPanel manageMoviesPanel;

    public AdminDashboard() {
        setTitle("CineVerse - Admin");
        setSize(375, 667);  // Mobile dimensions
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create sidebar with new styling (hidden by default)
        sidebar = createSidebar();
        
        // Create hamburger panel that's always visible
        hamburgerPanel = createHamburgerPanel();
        add(hamburgerPanel, BorderLayout.WEST);
        
        // Create content panels with proper linking
        setupContentPanels();
        
        setVisible(true);
    }
    
    private JPanel createHamburgerPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(accentColor);
        panel.setPreferredSize(new Dimension(40, getHeight()));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Create a button with three stacked lines, reduced space
        toggleSidebarBtn = new JButton("<html>―<br>―<br>―</html>");
        toggleSidebarBtn.setBackground(accentColor);
        toggleSidebarBtn.setForeground(textColor);
        toggleSidebarBtn.setBorderPainted(false);
        toggleSidebarBtn.setFocusPainted(false);
        toggleSidebarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleSidebarBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        toggleSidebarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleSidebarBtn.addActionListener(e -> toggleSidebar());
        toggleSidebarBtn.setMargin(new Insets(5, 0, 5, 0));  // Reduced space between lines
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(toggleSidebarBtn);
        
        return panel;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(accentColor);
        sidebar.setPreferredSize(new Dimension(200, getHeight()));  // Wider sidebar for full text
        sidebar.setLayout(null);
        
        // Toggle button at the top of sidebar too
        JButton sidebarToggleBtn = new JButton("<html>―<br>―<br>―</html>");
        sidebarToggleBtn.setBounds(5, 5, 30, 40);
        sidebarToggleBtn.setBackground(accentColor);
        sidebarToggleBtn.setForeground(textColor);
        sidebarToggleBtn.setBorderPainted(false);
        sidebarToggleBtn.setFocusPainted(false);
        sidebarToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sidebarToggleBtn.addActionListener(e -> toggleSidebar());
        sidebar.add(sidebarToggleBtn);
        
        // Logo/title section
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(accentColor.darker());
        logoPanel.setBounds(0, 0, 200, 50);
        
        JLabel logo = new JLabel("CineVerse", JLabel.CENTER);
        logo.setForeground(textColor);
        logo.setFont(new Font("SansSerif", Font.BOLD, 14));
        logoPanel.add(logo, BorderLayout.CENTER);
        sidebar.add(logoPanel);
        
        // Admin info section
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBackground(accentColor);
        adminPanel.setBounds(0, 50, 200, 50);
        adminPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(250, 250, 250, 100)));
        
        JLabel adminLabel = new JLabel("👤 Admin", JLabel.CENTER);
        adminLabel.setForeground(new Color(250, 250, 250));
        adminLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        adminPanel.add(adminLabel, BorderLayout.CENTER);
        sidebar.add(adminPanel);
        
        // Navigation buttons - with full text
        manageMoviesBtn = createSidebarButton("🎬 Manage Movies", 120);
        addMovieBtn = createSidebarButton("➕ Add New Movie", 170);
        
        // Logout button
        logoutBtn = createSidebarButton("🚪 Logout", 220);
        
        sidebar.add(manageMoviesBtn);
        sidebar.add(addMovieBtn);
        sidebar.add(logoutBtn);  // Add logout button
        
        // Set initial active button
        setActiveButton(manageMoviesBtn);
        
        return sidebar;
    }
    
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        if (sidebarVisible) {
            // Show the sidebar
            remove(hamburgerPanel);
            add(sidebar, BorderLayout.WEST);
        } else {
            // Hide the sidebar
            remove(sidebar);
            add(hamburgerPanel, BorderLayout.WEST);
        }
        revalidate();
        repaint();
    }
    
    private void setupContentPanels() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(bgColor);
        
        // Create panels with proper linking
        manageMoviesPanel = new ManageMoviesPanel();
        AddMoviePanel addMoviePanel = new AddMoviePanel(manageMoviesPanel);
        
        contentPanel.add(manageMoviesPanel, "manageMovies");
        contentPanel.add(addMoviePanel, "addMovie");
        
        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(5, y, 190, 40);  // Wider buttons for full text
        btn.setBackground(accentColor);
        btn.setForeground(textColor);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);  // Left-aligned text
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        
        return btn;
    }
    
    private void setActiveButton(JButton button) {
        // Reset previous active button
        if (activeButton != null) {
            activeButton.setBackground(accentColor);
            activeButton.setForeground(textColor);
        }
        
        // Set new active button
        activeButton = button;
        activeButton.setBackground(hoverColor);
        activeButton.setForeground(darkColor);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == manageMoviesBtn) {
            setActiveButton(manageMoviesBtn);
            // Refresh movie list before showing the panel
            manageMoviesPanel.refreshMovies();
            cardLayout.show(contentPanel, "manageMovies");
            // Hide sidebar automatically after selecting a panel
            sidebarVisible = false;
            remove(sidebar);
            add(hamburgerPanel, BorderLayout.WEST);
            revalidate();
            repaint();
        } else if (e.getSource() == addMovieBtn) {
            setActiveButton(addMovieBtn);
            cardLayout.show(contentPanel, "addMovie");
            // Hide sidebar automatically after selecting a panel
            sidebarVisible = false;
            remove(sidebar);
            add(hamburgerPanel, BorderLayout.WEST);
            revalidate();
            repaint();
        } else if (e.getSource() == logoutBtn) {
            // Perform logout action
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                // Clear session/token and redirect to login screen
                dispose(); // Close Admin Dashboard
                new LoginScreen(); // Open Login Screen
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}

