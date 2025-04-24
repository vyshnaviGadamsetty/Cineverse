// package panels;

// import utils.SQLDB;
// import javax.swing.*;
// import javax.swing.border.*;
// import java.awt.*;
// import java.awt.event.*;
// import java.sql.*;
// import java.time.*;
// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;
// import java.util.Locale;

// public class AddMoviePanel extends JPanel implements ActionListener {
//     private JTextField nameField, castField, genreField, seatsField, durationField, dateField;
//     private JComboBox<String> screenBox, hourBox, minuteBox, ampmBox;
//     private JButton submitBtn, clearBtn;
//     private JLabel statusLabel;

//     private ManageMoviesPanel manageMoviesPanel;

//     // Updated colors as requested
//     private final Color bgColor = new Color(236, 181, 125); // #ecb57d - light orange
//     private final Color darkColor = new Color(40, 56, 55);  // #283837 - dark teal
//     private final Color accentColor = new Color(228, 67, 40);  // #e44328 - red
//     private final Color lightColor = new Color(255, 255, 255);  // White
    
//     private final Font labelFont = new Font("SansSerif", Font.BOLD, 14);
//     private final Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);

//     public AddMoviePanel(ManageMoviesPanel panel) {
//         this();
//         this.manageMoviesPanel = panel;
//     }

//     public AddMoviePanel() {
//         setLayout(new BorderLayout());
//         setBackground(bgColor);

//         // Header Panel
//         JPanel headerPanel = new JPanel(new BorderLayout());
//         headerPanel.setBackground(darkColor);
//         headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
//         JLabel heading = new JLabel("➕ Add New Movie", JLabel.LEFT);
//         heading.setFont(new Font("SansSerif", Font.BOLD, 24));
//         heading.setForeground(Color.WHITE);
//         headerPanel.add(heading, BorderLayout.WEST);
//         add(headerPanel, BorderLayout.NORTH);

//         // Form Panel
//         JPanel formPanel = new JPanel(null);
//         formPanel.setBackground(bgColor);
        
//         // Left column
//         int leftX = 200;
//         addLabelAndField(formPanel, "Movie Name*", nameField = createTextField(), leftX, 50);
//         addLabelAndField(formPanel, "Cast*", castField = createTextField(), leftX, 120);
//         addLabelAndField(formPanel, "Genre*", genreField = createTextField(), leftX, 190);

//         // Right column
//         int rightX = 520;
//         addLabelAndField(formPanel, "Seats Available*", seatsField = createTextField(), rightX, 50);
        
//         // Screen selection
//         JLabel screenLabel = createLabel("Screen (1-3)*");
//         screenLabel.setBounds(rightX, 120, 200, 25);
//         formPanel.add(screenLabel);
        
//         screenBox = new JComboBox<>(new String[]{"1", "2", "3"});
//         screenBox.setBounds(rightX, 145, 200, 35);
//         screenBox.setFont(fieldFont);
//         screenBox.setBackground(lightColor);
//         formPanel.add(screenBox);

//         // Date input
//         addLabelAndField(formPanel, "Date (yyyy-MM-dd)*", dateField = createTextField(), leftX, 260);
//         dateField.setToolTipText("Format: 2025-04-19");

//         // Duration input
//         addLabelAndField(formPanel, "Duration (minutes)*", durationField = createTextField(), rightX, 190);
        
//         // Start time inputs
//         JLabel timeLabel = createLabel("Start Time*");
//         timeLabel.setBounds(rightX, 260, 200, 25);
//         formPanel.add(timeLabel);
        
//         hourBox = new JComboBox<>();
//         minuteBox = new JComboBox<>();
//         ampmBox = new JComboBox<>(new String[]{"AM", "PM"});
        
//         // Populate time dropdowns
//         for (int i = 1; i <= 12; i++) hourBox.addItem(String.format("%02d", i));
//         for (int i = 0; i < 60; i += 5) minuteBox.addItem(String.format("%02d", i));
        
//         hourBox.setBounds(rightX, 285, 60, 35);
//         minuteBox.setBounds(rightX + 70, 285, 60, 35);
//         ampmBox.setBounds(rightX + 140, 285, 60, 35);
        
//         hourBox.setFont(fieldFont);
//         minuteBox.setFont(fieldFont);
//         ampmBox.setFont(fieldFont);
        
//         formPanel.add(hourBox);
//         formPanel.add(minuteBox);
//         formPanel.add(ampmBox);

//         // Status label for notifications
//         statusLabel = new JLabel("");
//         statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
//         statusLabel.setHorizontalAlignment(JLabel.CENTER);
//         statusLabel.setBounds(250, 350, 400, 30);
//         formPanel.add(statusLabel);
        
//         // Button panel
//         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
//         buttonPanel.setBackground(bgColor);
//         buttonPanel.setBounds(250, 400, 400, 50);
        
//         submitBtn = createButton("Add Movie", accentColor, Color.WHITE);
//         submitBtn.addActionListener(this);
        
//         clearBtn = createButton("Clear Form", darkColor, Color.WHITE);
//         clearBtn.addActionListener(e -> clearFields());
        
//         buttonPanel.add(submitBtn);
//         buttonPanel.add(clearBtn);
//         formPanel.add(buttonPanel);
        
//         add(formPanel, BorderLayout.CENTER);
//     }
    
//     private JLabel createLabel(String text) {
//         JLabel label = new JLabel(text);
//         label.setFont(labelFont);
//         label.setForeground(darkColor);
//         return label;
//     }
    
//     private JTextField createTextField() {
//         JTextField field = new JTextField();
//         field.setFont(fieldFont);
//         field.setBorder(BorderFactory.createCompoundBorder(
//             new LineBorder(darkColor, 1),
//             new EmptyBorder(2, 8, 2, 8)
//         ));
//         return field;
//     }
    
//     private JButton createButton(String text, Color bg, Color fg) {
//         JButton button = new JButton(text);
//         button.setBackground(bg);
//         button.setForeground(fg);
//         button.setFont(new Font("SansSerif", Font.BOLD, 14));
//         button.setFocusPainted(false);
//         button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//         button.setBorder(new EmptyBorder(10, 20, 10, 20));
//         return button;
//     }
    
//     private void addLabelAndField(JPanel panel, String labelText, JTextField field, int x, int y) {
//         JLabel label = createLabel(labelText);
//         label.setBounds(x, y, 200, 25);
//         panel.add(label);
        
//         field.setBounds(x, y + 25, 200, 35);
//         panel.add(field);
//     }

//     @Override
//     public void actionPerformed(ActionEvent e) {
//         if (e.getSource() == submitBtn) {
//             addMovie();
//         }
//     }
    
//     private void addMovie() {
//         // Clear previous status
//         statusLabel.setText("");
//         statusLabel.setForeground(Color.BLACK);
        
//         // Get all input values
//         String name = nameField.getText().trim();
//         String cast = castField.getText().trim();
//         String genre = genreField.getText().trim();
//         String seatsStr = seatsField.getText().trim();
//         String durationStr = durationField.getText().trim();
//         String dateInput = dateField.getText().trim();
//         int screen = Integer.parseInt((String) screenBox.getSelectedItem());

//         // Validate inputs
//         if (name.isEmpty() || cast.isEmpty() || genre.isEmpty() || seatsStr.isEmpty() || durationStr.isEmpty() || dateInput.isEmpty()) {
//             showStatus("All fields are required.", true);
//             return;
//         }

//         try {
//             // Parse numeric inputs
//             int seats = Integer.parseInt(seatsStr);
//             int duration = Integer.parseInt(durationStr);
            
//             if (seats <= 0) {
//                 showStatus("Seats must be a positive number.", true);
//                 return;
//             }
            
//             if (duration <= 0) {
//                 showStatus("Duration must be a positive number.", true);
//                 return;
//             }

//             // Format time selection
//             String hour = (String) hourBox.getSelectedItem();
//             String minute = (String) minuteBox.getSelectedItem();
//             String ampm = (String) ampmBox.getSelectedItem();
//             String fullTime = hour + ":" + minute + " " + ampm;

//             // Parse time and calculate end time
//             DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
//             LocalTime start = LocalTime.parse(fullTime, inputFormat);
//             LocalTime end = start.plusMinutes(duration);

//             DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("HH:mm");
//             String startFormatted = start.format(dbFormat);
//             String endFormatted = end.format(dbFormat);
            
//             // Parse and validate date
//             LocalDate parsedDate;
//             try {
//                 parsedDate = LocalDate.parse(dateInput);
                
//                 // Validate that date is not in the past
//                 if (parsedDate.isBefore(LocalDate.now())) {
//                     showStatus("Cannot schedule movies for past dates.", true);
//                     return;
//                 }
//             } catch (DateTimeParseException ex) {
//                 showStatus("Please enter a valid date in yyyy-MM-dd format.", true);
//                 return;
//             }

//             // Check for time conflicts
//             Connection conn = SQLDB.connect();
            
//             // Improved query to check for conflicts
//             // This query checks if there's any movie on the same screen and date
//             // that overlaps with our new movie's time slot
//             String query = "SELECT name FROM movies WHERE screen = ? AND date_added = ? AND " +
//                           "((start_time <= ? AND time(start_time, '+' || duration_minutes || ' minutes') > ?) OR " +
//                           "(start_time < ? AND time(start_time, '+' || duration_minutes || ' minutes') >= ?) OR " +
//                           "(start_time >= ? AND start_time < ?))";
                          
//             PreparedStatement ps = conn.prepareStatement(query);
//             ps.setInt(1, screen);                // Screen number
//             ps.setString(2, dateInput);          // Date
//             ps.setString(3, endFormatted);       // New movie's end time
//             ps.setString(4, startFormatted);     // New movie's start time
//             ps.setString(5, endFormatted);       // New movie's end time
//             ps.setString(6, startFormatted);     // New movie's start time
//             ps.setString(7, startFormatted);     // New movie's start time
//             ps.setString(8, endFormatted);       // New movie's end time
            
//             ResultSet rs = ps.executeQuery();

//             if (rs.next()) {
//                 String conflictingMovie = rs.getString("name");
//                 showStatus("❌ Time conflict with movie \"" + conflictingMovie + "\" on Screen " + screen + 
//                           " on " + dateInput, true);
//                 rs.close();
//                 ps.close();
//                 conn.close();
//                 return;
//             }
            
//             rs.close();
//             ps.close();

//             // Insert the new movie
//             PreparedStatement insert = conn.prepareStatement(
//                 "INSERT INTO movies(name, cast, genre, screen, seats_available, start_time, duration_minutes, time_slot, date_added) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
//             );
//             insert.setString(1, name);
//             insert.setString(2, cast);
//             insert.setString(3, genre);
//             insert.setInt(4, screen);
//             insert.setInt(5, seats);
//             insert.setString(6, startFormatted);
//             insert.setInt(7, duration);
//             insert.setString(8, fullTime + " - " + end.format(inputFormat));
//             insert.setString(9, parsedDate.toString());

//             insert.executeUpdate();
//             insert.close();
//             conn.close();

//             showStatus("🎉 Movie added successfully!", false);
//             clearFields();

//             // Refresh manage panel if available
//             if (manageMoviesPanel != null) {
//                 manageMoviesPanel.refreshMovies();
//             }

//         } catch (NumberFormatException ex) {
//             showStatus("Please enter valid numbers for seats and duration.", true);
//         } catch (SQLException ex) {
//             ex.printStackTrace();
//             showStatus("Database error: " + ex.getMessage(), true);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             showStatus("Error: " + ex.getMessage(), true);
//         }
//     }

//     private void showStatus(String msg, boolean isError) {
//         statusLabel.setText(msg);
//         statusLabel.setForeground(isError ? accentColor : new Color(0, 128, 0));
//     }

//     private void clearFields() {
//         nameField.setText("");
//         castField.setText("");
//         genreField.setText("");
//         seatsField.setText("");
//         durationField.setText("");
//         dateField.setText("");
//         hourBox.setSelectedIndex(0);
//         minuteBox.setSelectedIndex(0);
//         ampmBox.setSelectedIndex(0);
//         statusLabel.setText("");
//     }
// // }
package panels;

import utils.SQLDB;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class AddMoviePanel extends JPanel implements ActionListener {
    private JTextField nameField, castField, genreField, seatsField, durationField, dateField;
    private JComboBox<String> screenBox, hourBox, minuteBox, ampmBox;
    private JButton submitBtn, clearBtn;
    private JLabel statusLabel;

    private ManageMoviesPanel manageMoviesPanel;

    // Updated colors as requested
    private final Color bgColor = Color.WHITE; // White background
    private final Color darkColor = Color.BLACK;  // Black text
    private final Color accentColor = new Color(228, 67, 40);  // #e44328 - red
    private final Color lightColor = new Color(245, 245, 245);  // Light gray for input fields
    
    private final Font labelFont = new Font("SansSerif", Font.BOLD, 12);
    private final Font fieldFont = new Font("SansSerif", Font.PLAIN, 12);

    public AddMoviePanel(ManageMoviesPanel panel) {
        this();
        this.manageMoviesPanel = panel;
    }

    public AddMoviePanel() {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setPreferredSize(new Dimension(375, 607)); // Mobile dimensions minus header

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel heading = new JLabel("➕ Add New Movie", JLabel.LEFT);
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(Color.WHITE);
        headerPanel.add(heading, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Create a scroll pane for the form
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(bgColor);
        
        // Form Panel - adjusted for mobile
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(bgColor);
        formPanel.setPreferredSize(new Dimension(375, 800)); // Make it scrollable
        
        // Movie Name
        addLabelAndField(formPanel, "Movie Name*", nameField = createTextField(), 25, 20);
        
        // Cast
        addLabelAndField(formPanel, "Cast*", castField = createTextField(), 25, 90);
        
        // Genre
        addLabelAndField(formPanel, "Genre*", genreField = createTextField(), 25, 160);
        
        // Seats
        addLabelAndField(formPanel, "Seats Available*", seatsField = createTextField(), 25, 230);
        
        // Screen selection
        JLabel screenLabel = createLabel("Screen (1-3)*");
        screenLabel.setBounds(25, 300, 325, 20);
        formPanel.add(screenLabel);
        
        screenBox = new JComboBox<>(new String[]{"1", "2", "3"});
        screenBox.setBounds(25, 320, 325, 35);
        screenBox.setFont(fieldFont);
        screenBox.setBackground(lightColor);
        formPanel.add(screenBox);

        // Date input
        addLabelAndField(formPanel, "Date (yyyy-MM-dd)*", dateField = createTextField(), 25, 370);
        dateField.setToolTipText("Format: 2025-04-19");

        // Duration input
        addLabelAndField(formPanel, "Duration (minutes)*", durationField = createTextField(), 25, 440);
        
        // Start time inputs
        JLabel timeLabel = createLabel("Start Time*");
        timeLabel.setBounds(25, 510, 325, 20);
        formPanel.add(timeLabel);
        
        hourBox = new JComboBox<>();
        minuteBox = new JComboBox<>();
        ampmBox = new JComboBox<>(new String[]{"AM", "PM"});
        
        // Populate time dropdowns
        for (int i = 1; i <= 12; i++) hourBox.addItem(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5) minuteBox.addItem(String.format("%02d", i));
        
        hourBox.setBounds(25, 530, 100, 35);
        minuteBox.setBounds(135, 530, 100, 35);
        ampmBox.setBounds(245, 530, 100, 35);
        
        hourBox.setFont(fieldFont);
        minuteBox.setFont(fieldFont);
        ampmBox.setFont(fieldFont);
        
        formPanel.add(hourBox);
        formPanel.add(minuteBox);
        formPanel.add(ampmBox);

        // Status label for notifications
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBounds(25, 580, 325, 30);
        formPanel.add(statusLabel);
        
        // Button panel - Mobile optimized buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBounds(25, 620, 325, 40);
        
        submitBtn = createButton("Add Movie", accentColor, Color.BLACK);
        submitBtn.addActionListener(this);
        
        clearBtn = createButton("Clear Form", accentColor, Color.BLACK);
        clearBtn.addActionListener(e -> clearFields());
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(clearBtn);
        formPanel.add(buttonPanel);
        
        // Add form to scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formWrapper.add(scrollPane, BorderLayout.CENTER);
        
        add(formWrapper, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(darkColor);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(fieldFont);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accentColor, 1),
            new EmptyBorder(2, 8, 2, 8)
        ));
        field.setBackground(lightColor);
        return field;
    }
    
    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        return button;
    }
    
    private void addLabelAndField(JPanel panel, String labelText, JTextField field, int x, int y) {
        JLabel label = createLabel(labelText);
        label.setBounds(x, y, 325, 20);
        panel.add(label);
        
        field.setBounds(x, y + 20, 325, 35);
        panel.add(field);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            addMovie();
        }
    }
    
    private void addMovie() {
        // Clear previous status
        statusLabel.setText("");
        statusLabel.setForeground(Color.BLACK);
        
        // Get all input values
        String name = nameField.getText().trim();
        String cast = castField.getText().trim();
        String genre = genreField.getText().trim();
        String seatsStr = seatsField.getText().trim();
        String durationStr = durationField.getText().trim();
        String dateInput = dateField.getText().trim();
        int screen = Integer.parseInt((String) screenBox.getSelectedItem());

        // Validate inputs
        if (name.isEmpty() || cast.isEmpty() || genre.isEmpty() || seatsStr.isEmpty() || durationStr.isEmpty() || dateInput.isEmpty()) {
            showStatus("All fields are required.", true);
            return;
        }

        try {
            // Parse numeric inputs
            int seats = Integer.parseInt(seatsStr);
            int duration = Integer.parseInt(durationStr);
            
            if (seats <= 0) {
                showStatus("Seats must be a positive number.", true);
                return;
            }
            
            if (duration <= 0) {
                showStatus("Duration must be a positive number.", true);
                return;
            }

            // Format time selection
            String hour = (String) hourBox.getSelectedItem();
            String minute = (String) minuteBox.getSelectedItem();
            String ampm = (String) ampmBox.getSelectedItem();
            String fullTime = hour + ":" + minute + " " + ampm;

            // Parse time and calculate end time
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            LocalTime start = LocalTime.parse(fullTime, inputFormat);
            LocalTime end = start.plusMinutes(duration);

            DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("HH:mm");
            String startFormatted = start.format(dbFormat);
            String endFormatted = end.format(dbFormat);
            
            // Parse and validate date
            LocalDate parsedDate;
            try {
                parsedDate = LocalDate.parse(dateInput);
                
                // Validate that date is not in the past
                if (parsedDate.isBefore(LocalDate.now())) {
                    showStatus("Cannot schedule movies for past dates.", true);
                    return;
                }
            } catch (DateTimeParseException ex) {
                showStatus("Please enter a valid date in yyyy-MM-dd format.", true);
                return;
            }

            // Check for time conflicts
            Connection conn = SQLDB.connect();
            
            // Improved query to check for conflicts
            // This query checks if there's any movie on the same screen and date
            // that overlaps with our new movie's time slot
            String query = "SELECT name FROM movies WHERE screen = ? AND date_added = ? AND " +
                          "((start_time <= ? AND time(start_time, '+' || duration_minutes || ' minutes') > ?) OR " +
                          "(start_time < ? AND time(start_time, '+' || duration_minutes || ' minutes') >= ?) OR " +
                          "(start_time >= ? AND start_time < ?))";
                          
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, screen);                // Screen number
            ps.setString(2, dateInput);          // Date
            ps.setString(3, endFormatted);       // New movie's end time
            ps.setString(4, startFormatted);     // New movie's start time
            ps.setString(5, endFormatted);       // New movie's end time
            ps.setString(6, startFormatted);     // New movie's start time
            ps.setString(7, startFormatted);     // New movie's start time
            ps.setString(8, endFormatted);       // New movie's end time
            
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String conflictingMovie = rs.getString("name");
                showStatus("❌ Time conflict with movie \"" + conflictingMovie + "\" on Screen " + screen + 
                          " on " + dateInput, true);
                rs.close();
                ps.close();
                conn.close();
                return;
            }
            
            rs.close();
            ps.close();

            // Insert the new movie
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO movies(name, cast, genre, screen, seats_available, start_time, duration_minutes, time_slot, date_added) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            insert.setString(1, name);
            insert.setString(2, cast);
            insert.setString(3, genre);
            insert.setInt(4, screen);
            insert.setInt(5, seats);
            insert.setString(6, startFormatted);
            insert.setInt(7, duration);
            insert.setString(8, fullTime + " - " + end.format(inputFormat));
            insert.setString(9, parsedDate.toString());

            insert.executeUpdate();
            insert.close();
            conn.close();

            showStatus("🎉 Movie added successfully!", false);
            clearFields();

            // Refresh manage panel if available
            if (manageMoviesPanel != null) {
                manageMoviesPanel.refreshMovies();
            }

        } catch (NumberFormatException ex) {
            showStatus("Please enter valid numbers for seats and duration.", true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showStatus("Database error: " + ex.getMessage(), true);
        } catch (Exception ex) {
            ex.printStackTrace();
            showStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? accentColor : new Color(0, 128, 0));
    }

    private void clearFields() {
        nameField.setText("");
        castField.setText("");
        genreField.setText("");
        seatsField.setText("");
        durationField.setText("");
        dateField.setText("");
        hourBox.setSelectedIndex(0);
        minuteBox.setSelectedIndex(0);
        ampmBox.setSelectedIndex(0);
        statusLabel.setText("");
    }
}

