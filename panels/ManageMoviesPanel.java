
package panels;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import utils.SQLDB;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;


public class ManageMoviesPanel extends JPanel {
    private JPanel movieListPanel;
    private JScrollPane scrollPane;

    // Updated colors as requested
    private final Color bgColor = Color.WHITE; // White background
    private final Color cardColor = Color.WHITE;
    private final Color darkColor = Color.BLACK;  // Black for text
    private final Color accentColor = new Color(228, 67, 40); // #e44328 - red
    private final Font font = new Font("SansSerif", Font.PLAIN, 12);
 private void showError(String errorMessage) {
    JPanel errorPanel = new JPanel();
    errorPanel.setBackground(bgColor);
    JLabel errorLabel = new JLabel(errorMessage);
    errorLabel.setForeground(accentColor);  // Set the color for the error message
    errorPanel.add(errorLabel);
    movieListPanel.add(errorPanel);
    revalidate();  // Revalidate the panel after adding the error
    repaint();     // Repaint the panel to reflect the changes
}

    public ManageMoviesPanel() {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setPreferredSize(new Dimension(375, 607)); // Mobile dimensions minus header

        // Updated header with new colors and styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel title = new JLabel("🎬 All Movies", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Movie list with updated styling
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

        refreshMovies();
    }

    public void refreshMovies() {
        movieListPanel.removeAll();

        try (Connection conn = SQLDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movies ORDER BY date_added DESC, start_time ASC")) {

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

                movieListPanel.add(createMovieCard(id, name, cast, genre, screen, screenNum, seats, timeSlot, addedOn));
                movieListPanel.add(Box.createVerticalStrut(10));
            }

            if (!rs.isBeforeFirst()) { // No data
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setBackground(bgColor);
                
                JLabel emptyLabel = new JLabel("No movies found. Add a new movie to get started!", JLabel.CENTER);
                emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
                emptyLabel.setForeground(darkColor);
                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                
                movieListPanel.add(emptyPanel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JPanel errorPanel = new JPanel();
            errorPanel.setBackground(bgColor);
            JLabel errorLabel = new JLabel("❌ Failed to load movies: " + e.getMessage());
            errorLabel.setForeground(accentColor);
            errorPanel.add(errorLabel);
            movieListPanel.add(errorPanel);
        }

        revalidate();
        repaint();
    }

// This method checks if the movie's showtime has passed
private boolean isExpired(String timeSlot) {
    try {
        // Assuming timeSlot format is like "HH:mm a" (e.g., "03:00 PM")
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Date showTime = sdf.parse(timeSlot);
        Date currentTime = new Date(); // Get current system time
        return showTime.before(currentTime); // If showtime is before current time, it's expired
    } catch (ParseException e) {
        e.printStackTrace();
        return false; // In case of error, assume the movie is not expired
    }
}
// This method deletes expired movies from the database
private void deleteExpiredMovie(String movieId) {
    try (Connection conn = SQLDB.connect()) {
        String deleteQuery = "DELETE FROM movies WHERE id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            deleteStmt.setString(1, movieId);
            deleteStmt.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showError("Failed to delete expired movie: " + e.getMessage());
    }
}


    private JPanel createMovieCard(String id, String name, String cast, String genre, 
                                  String screen, int screenNum, String seats, 
                                  String timeSlot, String addedOn) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(345, 200)); // Increased height to accommodate longer text
        card.setMaximumSize(new Dimension(345, 200));   // Increased height for more space
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accentColor, 1, true),
            new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(50, 200)); 
        leftPanel.setBackground(accentColor);
        
        JLabel poster = new JLabel("🎞️", JLabel.CENTER);
        poster.setFont(new Font("SansSerif", Font.PLAIN, 24));
        poster.setForeground(Color.WHITE);
        leftPanel.add(poster, BorderLayout.CENTER);
        
        card.add(leftPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);

        JPanel mainInfo = new JPanel();
        mainInfo.setLayout(new BoxLayout(mainInfo, BoxLayout.Y_AXIS));
        mainInfo.setBackground(Color.WHITE);
        mainInfo.setBorder(new EmptyBorder(0, 10, 5, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(accentColor);
        topPanel.add(nameLabel, BorderLayout.WEST);
        
        JPanel screenTag = new JPanel();
        screenTag.setBackground(screenNum == 1 ? accentColor : 
                               screenNum == 2 ? new Color(0, 128, 128) : 
                               new Color(106, 90, 205));
        screenTag.setBorder(new EmptyBorder(2, 6, 2, 6));
        
        JLabel screenLabel = new JLabel(screen);
        screenLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        screenLabel.setForeground(Color.WHITE);
        screenTag.add(screenLabel);
        topPanel.add(screenTag, BorderLayout.EAST);
        
        mainInfo.add(topPanel);
        mainInfo.add(Box.createVerticalStrut(5));
        
        // Cast - Using JTextArea to allow multiline text
        JPanel castPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        castPanel.setBackground(Color.WHITE);
        JTextArea castTextArea = new JTextArea(cast);
        castTextArea.setFont(font);
        castTextArea.setLineWrap(true);
        castTextArea.setWrapStyleWord(true);
        castTextArea.setEditable(false);
        castTextArea.setBackground(Color.WHITE);
        castTextArea.setPreferredSize(new Dimension(275, 40)); // Increased height for multiple lines
        castPanel.add(castTextArea);
        mainInfo.add(castPanel);
        mainInfo.add(Box.createVerticalStrut(3));
        
        JPanel genrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genrePanel.setBackground(Color.WHITE);
        JLabel genreLabel = new JLabel("Genre: " + genre);
        genreLabel.setFont(font);
        genrePanel.add(genreLabel);
        mainInfo.add(genrePanel);
        mainInfo.add(Box.createVerticalStrut(3));
        
        JPanel seatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        seatsPanel.setBackground(Color.WHITE);
        JLabel seatsLabel = new JLabel("Available Seats: " + seats);
        seatsLabel.setFont(font);
        seatsPanel.add(seatsLabel);
        mainInfo.add(seatsPanel);
        mainInfo.add(Box.createVerticalStrut(3));
        
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timePanel.setBackground(Color.WHITE);
        JLabel timeSlotLabel = new JLabel("Showtime: " + timeSlot);
        timeSlotLabel.setFont(font);
        timePanel.add(timeSlotLabel);
        mainInfo.add(timePanel);
        mainInfo.add(Box.createVerticalStrut(3));
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setBackground(new Color(245, 245, 245)); 
        datePanel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        
        JLabel dateLabel = new JLabel("Date: " + addedOn);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        dateLabel.setForeground(new Color(50, 50, 50));
        datePanel.add(dateLabel);
        
        mainInfo.add(datePanel);
        
        infoPanel.add(mainInfo, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.CENTER);

        // BUTTONS - with fully colored backgrounds as requested
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(1, 2, 10, 0));
        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        actionPanel.setBackground(Color.WHITE);
        
        JButton editBtn = new JButton("✏️ Edit");
        editBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        editBtn.setBackground(accentColor); 
        editBtn.setForeground(Color.WHITE);
        editBtn.setOpaque(true);
        editBtn.setBorderPainted(false);
        editBtn.setFocusPainted(false);
        editBtn.addActionListener(e -> editMovie(id, name, cast, genre, seats, timeSlot, addedOn));  
        
        JButton deleteBtn = new JButton("🗑️ Delete");
        deleteBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        deleteBtn.setBackground(accentColor); 
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(true); 
        deleteBtn.setBorderPainted(false); 
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> deleteMovie(id));
        
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        
        card.add(actionPanel, BorderLayout.SOUTH);

        return card;
    }

    private void editMovie(String id, String name, String cast, String genre, String seats, String timeSlot, String addedOn) {
        JTextField nameField = new JTextField(name);
        JTextField castField = new JTextField(cast);
        JTextField genreField = new JTextField(genre);
        JTextField seatsField = new JTextField(seats);
        JTextField timeSlotField = new JTextField(timeSlot);
        JTextField dateField = new JTextField(addedOn);

        JPanel editPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        editPanel.setBackground(Color.WHITE);
        
        editPanel.add(new JLabel("Movie Name:"));
        editPanel.add(nameField);
        
        editPanel.add(new JLabel("Cast:"));
        editPanel.add(castField);
        
        editPanel.add(new JLabel("Genre:"));
        editPanel.add(genreField);
        
        editPanel.add(new JLabel("Available Seats:"));
        editPanel.add(seatsField);
        
        editPanel.add(new JLabel("Showtime:"));
        editPanel.add(timeSlotField);
        
        editPanel.add(new JLabel("Date Added:"));
        editPanel.add(dateField);
        
        int confirm = JOptionPane.showConfirmDialog(this, editPanel, "Edit Movie", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (confirm == JOptionPane.OK_OPTION) {
            try (Connection conn = SQLDB.connect()) {
                String updateQuery = "UPDATE movies SET name = ?, cast = ?, genre = ?, seats_available = ?, time_slot = ?, date_added = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, nameField.getText());
                updateStmt.setString(2, castField.getText());
                updateStmt.setString(3, genreField.getText());
                updateStmt.setString(4, seatsField.getText());
                updateStmt.setString(5, timeSlotField.getText());
                updateStmt.setString(6, dateField.getText());
                updateStmt.setString(7, id);
                int result = updateStmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Movie updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshMovies();
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating movie. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMovie(String id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the movie with ID: " + id + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = SQLDB.connect()) {
                String deleteQuery = "DELETE FROM movies WHERE id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                deleteStmt.setString(1, id);
                int result = deleteStmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Movie deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshMovies(); // Refresh the movie list
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting movie. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
