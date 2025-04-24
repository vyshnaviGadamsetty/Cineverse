
package panels;

import utils.SQLDB;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingPanel extends JPanel {
    private String username;
    private String movieId;
    private String movieName;
    private String timeSlot;
    private int screenNum;
    private int availableSeats;
    private JFrame parentFrame;

    // UI Components
    private JPanel seatSelectionPanel;
    private JPanel infoPanel;
    private List<JToggleButton> seatButtons;
    private JLabel totalPriceLabel;
    private JButton confirmButton;
    private JLabel seatsValueLabel; // For updating available seats count
    
    // Colors and fonts
    private final Color bgColor = Color.WHITE;
    private final Color accentColor = new Color(228, 67, 40);
    private final Color selectedSeatColor = new Color(46, 139, 87); // Selected by user
    private final Color bookedSeatColor = new Color(228, 67, 40);   // Booked by others (red)
    private final Color availableSeatColor = new Color(220, 220, 220);
    private final Font headerFont = new Font("SansSerif", Font.BOLD, 16);
    private final Font regularFont = new Font("SansSerif", Font.PLAIN, 13);
    
    // Constants
    private final int TICKET_PRICE = 195;
    // Update to 9 rows and 10 columns (90 seats)
    private final int ROWS = 9;  // 9 rows
    private final int COLS = 10; // 10 columns
    private final int TOTAL_SEATS = ROWS * COLS; // 90 seats

    // Track selected seats
    private List<Integer> selectedSeatsList;
    private boolean[] bookedSeats;
    
    // Timer for auto-refresh
    private Timer refreshTimer;
    private boolean isUserSelecting = false;

    public BookingPanel(JFrame parentFrame, String username, String movieId, String movieName, 
                       String timeSlot, int screenNum, int availableSeats) {
        this.parentFrame = parentFrame;
        this.username = username;
        this.movieId = movieId;
        this.movieName = movieName;
        this.timeSlot = timeSlot;
        this.screenNum = screenNum;
        this.availableSeats = availableSeats;
        
        this.selectedSeatsList = new ArrayList<>();
        this.bookedSeats = new boolean[TOTAL_SEATS];
        
        setLayout(new BorderLayout(0, 10));
        setBackground(bgColor);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        initializeUI();
        loadBookedSeats();
        
        // Setup auto-refresh timer (every 15 seconds)
        refreshTimer = new Timer(15000, e -> {
            if (!isUserSelecting) {
                refreshSeatStatus();
            }
        });
        refreshTimer.start();
    }
    
    private void initializeUI() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Booking: " + movieName, JLabel.LEFT);
        titleLabel.setFont(headerFont);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 255, 255, 80));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> goBack());
        headerPanel.add(backButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(bgColor);
        
        // Movie info panel (top)
        infoPanel = createInfoPanel();
        contentPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Seat selection panel (center)
        JPanel seatPanelContainer = new JPanel(new BorderLayout());
        seatPanelContainer.setBackground(bgColor);
        
        seatSelectionPanel = createSeatSelectionPanel();
        
        JScrollPane scrollPane = new JScrollPane(seatSelectionPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        seatPanelContainer.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(seatPanelContainer, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Bottom panel for booking button
        // Bottom panel for booking button
JPanel bottomPanel = new JPanel(new BorderLayout());
bottomPanel.setBackground(bgColor);
bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

totalPriceLabel = new JLabel("Total: Rs.0.00");
totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
bottomPanel.add(totalPriceLabel, BorderLayout.WEST);

confirmButton = new JButton("Confirm Booking");
confirmButton.setFont(new Font("SansSerif", Font.BOLD, 14));
confirmButton.setBackground(new Color(46, 139, 87)); // Green color (ForestGreen)
confirmButton.setForeground(Color.BLACK); // White text
confirmButton.setEnabled(false);
confirmButton.setBorder(new EmptyBorder(8, 15, 8, 15));
confirmButton.setFocusPainted(false);
confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
confirmButton.addActionListener(e -> processBooking());

bottomPanel.add(confirmButton, BorderLayout.EAST);

add(bottomPanel, BorderLayout.SOUTH);

            }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Movie title
        JLabel movieNameLabel = new JLabel(movieName);
        movieNameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        movieNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(movieNameLabel);
        panel.add(Box.createVerticalStrut(8));
        
        // Info grid
        JPanel infoGrid = new JPanel(new GridLayout(3, 1, 2, 2));
        infoGrid.setBackground(bgColor);
        infoGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Screen info
        JPanel screenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        screenPanel.setBackground(bgColor);
        JLabel screenLabel = new JLabel("Screen: ");
        screenLabel.setFont(regularFont);
        JLabel screenValueLabel = new JLabel(String.valueOf(screenNum));
        screenValueLabel.setFont(regularFont);
        screenPanel.add(screenLabel);
        screenPanel.add(screenValueLabel);
        infoGrid.add(screenPanel);
        
        // Time info
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timePanel.setBackground(bgColor);
        JLabel timeLabel = new JLabel("Time: ");
        timeLabel.setFont(regularFont);
        JLabel timeValueLabel = new JLabel(timeSlot);
        timeValueLabel.setFont(regularFont);
        timePanel.add(timeLabel);
        timePanel.add(timeValueLabel);
        infoGrid.add(timePanel);
        
        // Available seats
        JPanel seatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        seatsPanel.setBackground(bgColor);
        JLabel seatsLabel = new JLabel("Available: ");
        seatsLabel.setFont(regularFont);
        seatsValueLabel = new JLabel(String.valueOf(availableSeats));
        seatsValueLabel.setFont(regularFont);
        seatsPanel.add(seatsLabel);
        seatsPanel.add(seatsValueLabel);
        infoGrid.add(seatsPanel);
        
        panel.add(infoGrid);
        
        return panel;
    }
    
    private JPanel createSeatSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(bgColor);
        
        // Title for seat selection
        JLabel seatLabel = new JLabel("Select Your Seats");
        seatLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(seatLabel, BorderLayout.NORTH);
        
        // Legend panel for seat colors
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        legendPanel.setBackground(bgColor);
        
        // Available legend
        JPanel availableLegend = new JPanel();
        availableLegend.setBackground(availableSeatColor);
        availableLegend.setPreferredSize(new Dimension(15, 15));
        availableLegend.setBorder(new LineBorder(Color.GRAY, 1));
        JLabel availableLabel = new JLabel("Available");
        availableLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        legendPanel.add(availableLegend);
        legendPanel.add(availableLabel);
        
        // Selected legend
        JPanel selectedLegend = new JPanel();
        selectedLegend.setBackground(selectedSeatColor);
        selectedLegend.setPreferredSize(new Dimension(15, 15));
        selectedLegend.setBorder(new LineBorder(Color.GRAY, 1));
        JLabel selectedLabel = new JLabel("Selected");
        selectedLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        legendPanel.add(selectedLegend);
        legendPanel.add(selectedLabel);
        
        // Booked legend
        JPanel bookedLegend = new JPanel();
        bookedLegend.setBackground(bookedSeatColor);
        bookedLegend.setPreferredSize(new Dimension(15, 15));
        bookedLegend.setBorder(new LineBorder(Color.GRAY, 1));
        JLabel bookedLabel = new JLabel("Booked");
        bookedLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        legendPanel.add(bookedLegend);
        legendPanel.add(bookedLabel);
        
        panel.add(legendPanel, BorderLayout.SOUTH);
        
        // Screen indicator
        JPanel screenIndicator = new JPanel();
        screenIndicator.setBackground(new Color(200, 200, 200));
        screenIndicator.setPreferredSize(new Dimension(200, 15));
        screenIndicator.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.GRAY, 1),
            new EmptyBorder(2, 0, 2, 0)
        ));
        
        JLabel screenText = new JLabel("SCREEN", JLabel.CENTER);
        screenText.setFont(new Font("SansSerif", Font.BOLD, 10));
        screenText.setForeground(Color.DARK_GRAY);
        screenIndicator.add(screenText);
        
        JPanel screenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        screenPanel.setBackground(bgColor);
        screenPanel.add(screenIndicator);
        
        // Create seat grid
        JPanel seatGrid = new JPanel(new GridLayout(ROWS, COLS, 4, 4));
        seatGrid.setBackground(bgColor);
        seatGrid.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        seatButtons = new ArrayList<>();
        
        // Create all seat buttons with custom UI
        for (int i = 0; i < TOTAL_SEATS; i++) {
            final int seatNumber = i + 1;
            final JToggleButton seatButton = new JToggleButton(String.valueOf(seatNumber));
            seatButton.setFont(new Font("SansSerif", Font.BOLD, 10));
            seatButton.setBackground(availableSeatColor);
            seatButton.setForeground(Color.BLACK);
            seatButton.setFocusPainted(false);
            seatButton.setBorderPainted(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            seatButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seatButton.setPreferredSize(new Dimension(30, 25));
            
            // FIXED: Use a custom UI to ensure colors are properly applied
            seatButton.setUI(new BasicToggleButtonUI() {
                @Override
                public void update(Graphics g, JComponent c) {
                    if (c != null) {
                        JToggleButton b = (JToggleButton)c;
                        if (bookedSeats[seatNumber - 1]) {
                            b.setBackground(bookedSeatColor);
                            b.setForeground(Color.WHITE);
                        } else if (b.isSelected()) {
                            b.setBackground(selectedSeatColor);
                            b.setForeground(Color.WHITE);
                        } else {
                            b.setBackground(availableSeatColor);
                            b.setForeground(Color.BLACK);
                        }
                    }
                    super.update(g, c);
                }
            });
            
            // FIXED: Combined ChangeListener and ActionListener for more reliable color updates
            seatButton.addActionListener(e -> {
                isUserSelecting = true;
                
                if (bookedSeats[seatNumber - 1]) {
                    // If seat is booked, reset selection state
                    seatButton.setSelected(false);
                    seatButton.setBackground(bookedSeatColor);
                    seatButton.setForeground(Color.WHITE);
                } else if (seatButton.isSelected()) {
                    // If seat is now selected
                    selectedSeatsList.add(seatNumber);
                    seatButton.setBackground(selectedSeatColor);
                    seatButton.setForeground(Color.WHITE);
                } else {
                    // If seat is now unselected
                    selectedSeatsList.remove(Integer.valueOf(seatNumber));
                    seatButton.setBackground(availableSeatColor);
                    seatButton.setForeground(Color.BLACK);
                }
                
                updateTotalPrice();
                confirmButton.setEnabled(!selectedSeatsList.isEmpty());
                
                // Force repaint
                seatButton.repaint();
                
                isUserSelecting = false;
            });
            
            seatButtons.add(seatButton);
            seatGrid.add(seatButton);
        }
        
        // Add screen panel and seat grid to a container
        JPanel seatSelectionContainer = new JPanel();
        seatSelectionContainer.setLayout(new BoxLayout(seatSelectionContainer, BoxLayout.Y_AXIS));
        seatSelectionContainer.setBackground(bgColor);
        seatSelectionContainer.add(screenPanel);
        seatSelectionContainer.add(seatGrid);
        
        panel.add(seatSelectionContainer, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadBookedSeats() {
        try (Connection conn = SQLDB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT seat_numbers FROM bookings WHERE movie_id = ? AND time_slot = ?")) {
            
            stmt.setString(1, movieId);
            stmt.setString(2, timeSlot);
            
            try (ResultSet rs = stmt.executeQuery()) {
                // Reset all seats to unbooked first
                for (int i = 0; i < TOTAL_SEATS; i++) {
                    bookedSeats[i] = false;
                }
                
                while (rs.next()) {
                    String seatNumbersStr = rs.getString("seat_numbers");
                    
                    // Check if seat_numbers is not null or empty
                    if (seatNumbersStr != null && !seatNumbersStr.isEmpty()) {
                        String[] seatNumbers = seatNumbersStr.split(",");
                        for (String seatNum : seatNumbers) {
                            try {
                                int seat = Integer.parseInt(seatNum.trim());
                                if (seat > 0 && seat <= TOTAL_SEATS) {
                                    bookedSeats[seat - 1] = true; // Mark booked
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid seat number: " + seatNum);
                            }
                        }
                    }
                }
            }
            
            // Update the seat grid UI based on booked seats
            updateSeatGrid();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading booked seats: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSeatGrid() {
        for (int i = 0; i < TOTAL_SEATS; i++) {
            JToggleButton button = seatButtons.get(i);
            
            if (bookedSeats[i]) {
                button.setSelected(false);
                button.setBackground(bookedSeatColor);
                button.setForeground(Color.WHITE);
                button.setEnabled(false); // Disable button if booked
            } else {
                button.setEnabled(true); // Enable button if available
                
                // Only reset the color if it's not selected by the user
                if (!selectedSeatsList.contains(i + 1)) {
                    button.setBackground(availableSeatColor);
                    button.setForeground(Color.BLACK);
                    button.setSelected(false);
                } else {
                    button.setBackground(selectedSeatColor);
                    button.setForeground(Color.WHITE);
                    button.setSelected(true);
                }
            }
            // Force repaint of each button
            button.repaint();
        }

        // Force UI to revalidate and repaint
        seatSelectionPanel.revalidate();
        seatSelectionPanel.repaint();
    }

    private void refreshSeatStatus() {
        // Store currently selected seats
        List<Integer> tempSelectedSeats = new ArrayList<>(selectedSeatsList);
        
        // Clear selected seats list
        selectedSeatsList.clear();
        
        // Reload booked seats from database
        loadBookedSeats();
        
        // Re-select previously selected seats if they're still available
        for (Integer seat : tempSelectedSeats) {
            if (seat > 0 && seat <= TOTAL_SEATS && !bookedSeats[seat - 1]) {
                JToggleButton button = seatButtons.get(seat - 1);
                button.setSelected(true);
                button.setBackground(selectedSeatColor);
                button.setForeground(Color.WHITE);
                selectedSeatsList.add(seat);
                // Force individual button repaint
                button.repaint();
            }
        }
        
        updateTotalPrice();
        confirmButton.setEnabled(!selectedSeatsList.isEmpty());
        
        // Force UI to revalidate and repaint after status update
        SwingUtilities.invokeLater(() -> {
            seatSelectionPanel.revalidate();
            seatSelectionPanel.repaint();
        });
    }

    private void updateTotalPrice() {
        double total = selectedSeatsList.size() * TICKET_PRICE;
        totalPriceLabel.setText(String.format("Total: Rs.%.2f", total));
    }
    
    private void processBooking() {
        if (selectedSeatsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one seat", 
                "No Seats Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder seatsStr = new StringBuilder();
        for (int i = 0; i < selectedSeatsList.size(); i++) {
            seatsStr.append(selectedSeatsList.get(i));
            if (i < selectedSeatsList.size() - 1) {
                seatsStr.append(", ");
            }
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Do you want to book the following seats?\n\n" +
            "Movie: " + movieName + "\n" +
            "Time: " + timeSlot + "\n" +
            "Seats: " + seatsStr.toString() + "\n" +
            "Total Price: Rs." + (selectedSeatsList.size() * TICKET_PRICE) + ".00",
            "Confirm Booking",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            saveBooking(seatsStr.toString());
        }
    }
    
    private void saveBooking(String seatsStr) {
        try (Connection conn = SQLDB.connect()) {
            conn.setAutoCommit(false);
            
            try {
                // 1. Insert into bookings table
                try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO bookings (username, movie_id, time_slot, seat_numbers, booking_date) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)")) {
                    
                    stmt.setString(1, username);
                    stmt.setString(2, movieId);
                    stmt.setString(3, timeSlot);
                    stmt.setString(4, seatsStr);
                    stmt.executeUpdate();
                }
                
                // 2. Update available seats in movies table
                try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE movies SET seats_available = seats_available - ? WHERE id = ?")) {
                    
                    stmt.setInt(1, selectedSeatsList.size());
                    stmt.setString(2, movieId);
                    stmt.executeUpdate();
                }
                
                conn.commit();
                
                JOptionPane.showMessageDialog(this,
                    "Booking confirmed!\n\n" +
                    "Movie: " + movieName + "\n" +
                    "Time: " + timeSlot + "\n" +
                    "Seats: " + seatsStr,
                    "Booking Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                goBack();
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error processing booking: " + e.getMessage(),
                "Booking Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
        
        Container contentPanel = getParent();
        if (contentPanel != null) {
            CardLayout layout = (CardLayout) contentPanel.getLayout();
            layout.show(contentPanel, "MOVIES");
            
            contentPanel.remove(this);
        }
    }
}
