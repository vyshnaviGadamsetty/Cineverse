
# 🎟️ CineVerse – Movie Ticket Booking App

A Java Swing-based movie ticket booking system with a stylish UI and seamless SQLite integration. Supports user and admin functionalities including ticket booking, movie management, and reports.

---

## 🚀 Features

- 🔐 User Registration & Login  
- 🏠 Role-based Dashboards (Admin & User)  
- 🎬 Movie Listings with Seat Booking System  
- 🧾 Booking History Tracking  
- 🛠️ Admin Tools to Add, Edit, Delete Movies
- ❌Shows conflict dialogue whenever we are trying to add a movie to already given slot for another movie
- 🎥Only shows availabe movies to users which are available, Once a movie has started it will be not be shown neither will be available for booking
- 📊 Booking & Showtime Management  
- 💾 SQLite Database Integration  

---

## 🖼️ Screenshots

### 🔐 Login Screen
![Login Screen](https://github.com/vyshnaviGadamsetty/Cineverse/blob/1465de934cbf364949ae8bd3d848f00fb59194d1/assets/login.png)

### 🏠 User Dashboard – Now Showing
![User Panel](https://github.com/vyshnaviGadamsetty/Cineverse/blob/1465de934cbf364949ae8bd3d848f00fb59194d1/assets/user_pannel.png)

### 🎬 Add New Movie (Admin)
![Add Movie](https://github.com/vyshnaviGadamsetty/Cineverse/blob/1465de934cbf364949ae8bd3d848f00fb59194d1/assets/addmovie.png)

### 🎞️ All Movies – Edit/Delete Options (Admin)
![Edit Movies](https://github.com/vyshnaviGadamsetty/Cineverse/blob/1465de934cbf364949ae8bd3d848f00fb59194d1/assets/editmovie.png)

### 🎟️ Booking Panel – Seat Selection
![Booking Panel](https://github.com/vyshnaviGadamsetty/Cineverse/blob/1465de934cbf364949ae8bd3d848f00fb59194d1/assets/bookingpanel.png)

### 🧾 Booking History
![Booking History](https://github.com/vyshnaviGadamsetty/Cineverse/blob/1465de934cbf364949ae8bd3d848f00fb59194d1/assets/bookinghistory.png)

---

## 🗂️ Project Structure

```
Movie_App_Pro/
├── screens/            # Login, Register, Dashboard Screens
├── panels/             # Booking & Movie Management Panels
├── utils/              # DB Connections & Helpers
├── assets/             # UI Assets and Screenshots
├── javaapp.db          # SQLite Database File
└── sqlite-jdbc-3.49.1.0.jar  # JDBC Driver
```

---

## 🛠️ Technologies Used

- Java (Swing GUI)  
- SQLite (via JDBC)  
- IntelliJ / Eclipse IDE  
- MVC-style separation  

---

## 🏃‍♂️ Getting Started

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/Movie_App_Pro.git
   cd Movie_App_Pro
   ```

2. Open the project in your IDE and:
   - Set up JDK
   - Add `sqlite-jdbc-3.49.1.0.jar` to your classpath

3. Run `LoginScreen.java` to launch the app.

---

## 👨‍💻 Authors

- G. Sri Vyshnavi  
- Team Members from SRM University, AP

---
```

Let me know if you want to add badges, a demo video link, or GitHub Actions in the future!
