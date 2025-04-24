

package panels;

import utils.SQLDB;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class UserMoviesPanel extends JPanel {
    private JPanel movieListPanel;
    private JScrollPane scrollPane;
    private String username;
    
    // Colors
    private final Color bgColor = Color.WHITE;
    private final Color cardColor = Color.WHITE;
    private final Color darkColor = Color.BLACK;
    private final Color accentColor = new Color(228, 67, 40); // #e44328 - red
    private final Font font = new Font("SansSerif", Font.PLAIN, 12);
    
    public UserMoviesPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        setBackground(bgColor);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("🎬 Now Showing", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);
        
        // Add refresh button
        JButton refreshButton = new JButton("🔄");
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        refreshButton.setBackground(new Color(255, 255, 255, 80));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshMovies());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Movie list
        movieListPanel = new JPanel();
        movieListPanel.setLayout(new BoxLayout(movieListPanel, BoxLayout.Y_AXIS));
        movieListPanel.setBackground(bgColor);
        movieListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        scrollPane = new JScrollPane(movieListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        
        add(scrollPane, BorderLayout.CENTER);
        
        loadMovies();
    }
    
    public void refreshMovies() {
        loadMovies();
    }
    
    private void loadMovies() {
        movieListPanel.removeAll();
        
        try (Connection conn = SQLDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movies ORDER BY date_added DESC")) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String cast = rs.getString("cast");
                String genre = rs.getString("genre");
                int screenNum = rs.getInt("screen");
                String screen = "Screen " + screenNum;
                String seats = rs.getString("seats_available");
                String timeSlot = rs.getString("time_slot");
                String addedOn = rs.getString("date_added");

                // Debugging: Print the movie's showtime and current time
                System.out.println("Movie: " + name + " | Showtime: " + timeSlot);
                if (isExpired(timeSlot)) {
                    System.out.println("Movie " + name + " is expired and will not be shown.");
                    continue; // Skip expired movies
                } else {
                    System.out.println("Movie " + name + " is valid and will be shown.");
                }

                movieListPanel.add(createMovieCard(id, name, cast, genre, screen, screenNum, seats, timeSlot, addedOn));
                movieListPanel.add(Box.createVerticalStrut(10));
            }
            
            if (!rs.isBeforeFirst()) { // No data
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setBackground(bgColor);
                
                JLabel emptyLabel = new JLabel("No movies available at the moment", JLabel.CENTER);
                emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
                emptyLabel.setForeground(darkColor);
                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                
                movieListPanel.add(emptyPanel);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JPanel errorPanel = new JPanel();
            errorPanel.setBackground(bgColor);
            JLabel errorLabel = new JLabel("❌ Failed to load: " + e.getMessage());
            errorLabel.setForeground(accentColor);
            errorPanel.add(errorLabel);
            movieListPanel.add(errorPanel);
        }
        
        revalidate();
        repaint();
    }

    // This method checks if the movie's showtime has passed
  // This method checks if the movie's showtime has passed
private boolean isExpired(String timeSlot) {
    try {
        // Get only the start time (e.g., "01:00 AM" from "01:00 AM - 3:00 AM")
        String startTimeStr = timeSlot.split(" - ")[0]; 

        // Add today's date to the start time to ensure proper parsing
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a"); // Add date to time format
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // Get current date
        String startDateTimeStr = currentDate + " " + startTimeStr; // Combine date with time (e.g., "2025-04-22 01:00 AM")

        // Parse the combined date and time
        Date showTime = sdf.parse(startDateTimeStr);
        Date currentTime = new Date(); // Get current system time

        // Debugging: Print current time and showtime for comparison
        System.out.println("Current time: " + currentTime.toString());
        System.out.println("Showtime: " + showTime.toString());

        return showTime.before(currentTime); // If showtime is before current time, it's expired
    } catch (ParseException e) {
        e.printStackTrace();
        return false; // In case of error, assume the movie is not expired
    }
}


    
    private JPanel createMovieCard(String id, String name, String cast, String genre, 
                                  String screen, int screenNum, String seats, 
                                  String timeSlot, String addedOn) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        // Set fixed height to make card compact for mobile view
        card.setMaximumSize(new Dimension(355, 140));
        card.setPreferredSize(new Dimension(355, 140));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Left panel for movie icon (made smaller)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(60, 120));
        leftPanel.setBackground(accentColor);
        leftPanel.setBorder(new LineBorder(accentColor, 1));
        
        JLabel poster = new JLabel("🎬", JLabel.CENTER);
        poster.setFont(new Font("SansSerif", Font.PLAIN, 24));
        poster.setForeground(Color.WHITE);
        leftPanel.add(poster, BorderLayout.CENTER);
        
        card.add(leftPanel, BorderLayout.WEST);
        
        // Center panel for movie details
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(cardColor);
        
        // Movie title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(cardColor);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(darkColor);
        titleRow.add(nameLabel, BorderLayout.WEST);
        
        // Screen tag
        JPanel screenTag = new JPanel();
        screenTag.setBackground(screenNum == 1 ? accentColor : 
                               screenNum == 2 ? new Color(0, 128, 128) : 
                               new Color(106, 90, 205));
        screenTag.setBorder(new EmptyBorder(2, 6, 2, 6));
        
        JLabel screenLabel = new JLabel(screen);
        screenLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        screenLabel.setForeground(Color.WHITE);
        screenTag.add(screenLabel);
        titleRow.add(screenTag, BorderLayout.EAST);
        
        infoPanel.add(titleRow);
        infoPanel.add(Box.createVerticalStrut(5));
        
        // Genre
        JPanel genrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genrePanel.setBackground(cardColor);
        
        JLabel genreLabel = new JLabel("Genre: " + genre);
        genreLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        genreLabel.setForeground(darkColor);
        genrePanel.add(genreLabel);
        
        infoPanel.add(genrePanel);
        infoPanel.add(Box.createVerticalStrut(3));
        // Cast
JPanel castPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
castPanel.setBackground(cardColor);

JLabel castLabel = new JLabel("Cast: " + cast);
castLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
castLabel.setForeground(darkColor);
castPanel.add(castLabel);

infoPanel.add(castPanel);
infoPanel.add(Box.createVerticalStrut(3));

        
        // Show details - simplified for mobile
        JPanel showDetailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        showDetailsPanel.setBackground(cardColor);
        
        // JLabel timeLabel = new JLabel("⏰ " + timeSlot);
        // timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        // timeLabel.setForeground(darkColor);
        // Combine date and time into one label
String showDateTime = "⏰ " + addedOn + " at " + timeSlot;

JLabel timeLabel = new JLabel(showDateTime);
timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
timeLabel.setForeground(darkColor);

        JLabel seatsLabel = new JLabel("🪑 " + seats);
        seatsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        seatsLabel.setForeground(darkColor);
        
        showDetailsPanel.add(timeLabel);
        showDetailsPanel.add(seatsLabel);
        
        infoPanel.add(showDetailsPanel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Book button - highlight with accent color
        JButton bookButton = new JButton("Book Tickets");
        bookButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        bookButton.setBackground(accentColor); // Set background color to accentColor
        bookButton.setForeground(Color.BLACK); // Set text color to white for better contrast
        bookButton.setBorder(BorderFactory.createLineBorder(accentColor, 1)); // Border matching the button color
        bookButton.setFocusPainted(false); // Remove focus border
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        bookButton.addActionListener(e -> bookTicket(id, name, timeSlot, seats, screenNum));

        // Create panel to hold the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardColor); // Set background of the button panel
        buttonPanel.add(bookButton);

        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void bookTicket(String movieId, String movieName, String timeSlot, String availableSeats, int screenNum) {
        int seatsAvailable = Integer.parseInt(availableSeats);
        
        if (seatsAvailable <= 0) {
            JOptionPane.showMessageDialog(this, 
                "Sorry, this show is sold out!", 
                "No Seats Available", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create and show the booking panel
        BookingPanel bookingPanel = new BookingPanel(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            username,
            movieId,
            movieName,
            timeSlot,
            screenNum,
            seatsAvailable
        );
        
        // Add the booking panel to the content panel
        Container contentPanel = getParent();
        if (contentPanel != null) {
            contentPanel.add(bookingPanel, "BOOKING");
            CardLayout layout = (CardLayout) contentPanel.getLayout();
            layout.show(contentPanel, "BOOKING");
        }
    }
}

