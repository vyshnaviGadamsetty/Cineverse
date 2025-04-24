

package panels;

import utils.SQLDB;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class UserBookingsPanel extends JPanel {
    private JPanel bookingsListPanel;
    private JScrollPane scrollPane;
    private String username;
    
    // Colors
    private final Color bgColor = Color.WHITE;
    private final Color cardColor = Color.WHITE;
    private final Color darkColor = Color.BLACK;
    private final Color accentColor = new Color(228, 67, 40); // #e44328 - red
    private final Color successColor = new Color(46, 139, 87); // ForestGreen
    private final Font font = new Font("SansSerif", Font.PLAIN, 12);
    
    public UserBookingsPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        setBackground(bgColor);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("🎟️ My Bookings", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("🔄");
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        refreshButton.setBackground(new Color(255, 255, 255, 80));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadBookings());
        
        headerPanel.add(refreshButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Bookings list
        bookingsListPanel = new JPanel();
        bookingsListPanel.setLayout(new BoxLayout(bookingsListPanel, BoxLayout.Y_AXIS));
        bookingsListPanel.setBackground(bgColor);
        bookingsListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        scrollPane = new JScrollPane(bookingsListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        
        add(scrollPane, BorderLayout.CENTER);
        
        loadBookings();
    }
    
    public void loadBookings() {
        bookingsListPanel.removeAll();
        
        try (Connection conn = SQLDB.connect()) {
            String query = "SELECT b.id, b.movie_id, m.name AS movie_name, m.screen, b.seats, b.time_slot, " +
                          "b.booking_date, b.seat_numbers FROM bookings b " +
                          "JOIN movies m ON b.movie_id = m.id " +
                          "WHERE b.username = ? ORDER BY b.booking_date DESC, b.time_slot ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            boolean hasBookings = false;
            
            while (rs.next()) {
                hasBookings = true;
                int id = rs.getInt("id");
                String movieId = rs.getString("movie_id");
                String movieName = rs.getString("movie_name");
                int screen = rs.getInt("screen");
                int seats = rs.getInt("seats");
                String timeSlot = rs.getString("time_slot");
                String bookingDate = rs.getString("booking_date");
                String seatNumbers = rs.getString("seat_numbers");
                
                bookingsListPanel.add(createBookingCard(id, movieId, movieName, screen, seats, timeSlot, 
                                                      bookingDate, seatNumbers));
                bookingsListPanel.add(Box.createVerticalStrut(10));
            }
            
            if (!hasBookings) {
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setBackground(bgColor);
                
                JLabel emptyLabel = new JLabel("You haven't booked any tickets yet!", JLabel.CENTER);
                emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
                emptyLabel.setForeground(darkColor);
                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                
                bookingsListPanel.add(emptyPanel);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JPanel errorPanel = new JPanel();
            errorPanel.setBackground(bgColor);
            JLabel errorLabel = new JLabel("❌ Failed to load bookings: " + e.getMessage());
            errorLabel.setForeground(accentColor);
            errorPanel.add(errorLabel);
            bookingsListPanel.add(errorPanel);
        }
        
        revalidate();
        repaint();
    }
    
    private JPanel createBookingCard(int id, String movieId, String movieName, 
                                   int screen, int seats, String timeSlot, 
                                   String bookingDate, String seatNumbers) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        // Set max width for mobile-friendly display
        card.setMaximumSize(new Dimension(355, 140));
        card.setPreferredSize(new Dimension(355, 140));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Left panel for ticket icon
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(60, 120));
        leftPanel.setBackground(successColor);
        leftPanel.setBorder(new LineBorder(successColor, 1));
        
        JLabel ticketIcon = new JLabel("🎟️", JLabel.CENTER);
        ticketIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        ticketIcon.setForeground(Color.WHITE);
        leftPanel.add(ticketIcon, BorderLayout.CENTER);
        
        card.add(leftPanel, BorderLayout.WEST);
        
        // Center panel for booking details
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(cardColor);
        
        // Movie title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(cardColor);
        
        JLabel nameLabel = new JLabel(movieName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(darkColor);
        titleRow.add(nameLabel, BorderLayout.WEST);
        
        // Booking ID
        JLabel idLabel = new JLabel("#" + id);
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        idLabel.setForeground(new Color(100, 100, 100));
        titleRow.add(idLabel, BorderLayout.EAST);
        
        infoPanel.add(titleRow);
        infoPanel.add(Box.createVerticalStrut(5));
        
        // Booking details
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        detailsPanel.setBackground(cardColor);
        
        JLabel screenLabel = new JLabel("🎬 Screen " + screen);
        screenLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // JLabel seatsLabel = new JLabel("🪑 " + seats + (seats > 1 ? " seats" : " seat"));
        // seatsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        JLabel timeLabel = new JLabel("⏰ " + timeSlot);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        detailsPanel.add(screenLabel);
       // detailsPanel.add(seatsLabel);
        detailsPanel.add(timeLabel);
        
        infoPanel.add(detailsPanel);
        infoPanel.add(Box.createVerticalStrut(5));
        
        // Seat numbers
        JPanel seatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        seatsPanel.setBackground(cardColor);
        
        JLabel seatNumbersLabel = new JLabel("Seats: " + seatNumbers);
        seatNumbersLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        seatNumbersLabel.setForeground(new Color(80, 80, 80));
        seatsPanel.add(seatNumbersLabel);
        
        infoPanel.add(seatsPanel);
        
        // Booking date
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setBackground(new Color(245, 245, 245));
        datePanel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        
        JLabel dateLabel = new JLabel("Booked on: " + bookingDate);
        dateLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        dateLabel.setForeground(new Color(80, 80, 80));
        datePanel.add(dateLabel);
        
        infoPanel.add(datePanel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        cancelButton.setBackground(accentColor);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorder(new EmptyBorder(6, 10, 6, 10));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> cancelBooking(id, movieId, seats));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardColor);
        buttonPanel.add(cancelButton);
        
        card.add(buttonPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private void cancelBooking(int bookingId, String movieId, int bookedSeats) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this booking?\nThis action cannot be undone.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = SQLDB.connect()) {
                // Begin transaction
                conn.setAutoCommit(false);
                
                try {
                    // 1. Delete booking record
                    String deleteBookingQuery = "DELETE FROM bookings WHERE id = ?";
                    PreparedStatement deleteBookingStmt = conn.prepareStatement(deleteBookingQuery);
                    deleteBookingStmt.setInt(1, bookingId);
                    deleteBookingStmt.executeUpdate();
                    
                    // 2. Update available seats in movies table
                    String updateMovieQuery = "UPDATE movies SET seats_available = seats_available + ? WHERE id = ?";
                    PreparedStatement updateMovieStmt = conn.prepareStatement(updateMovieQuery);
                    updateMovieStmt.setInt(1, bookedSeats);
                    updateMovieStmt.setString(2, movieId);
                    updateMovieStmt.executeUpdate();
                    
                    // Commit transaction
                    conn.commit();
                    
                    JOptionPane.showMessageDialog(this,
                        "Your booking has been cancelled successfully.",
                        "Booking Cancelled",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the bookings list
                    loadBookings();
                    
                } catch (SQLException e) {
                    // Rollback in case of error
                    conn.rollback();
                    throw e;
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error cancelling your booking: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

