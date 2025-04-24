package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLDB {
    private static Connection conn = null;

    public static Connection connect() {
        try {
            if (conn != null && !conn.isClosed()) return conn;

            // ✅ Step 1: Register driver manually
            Class.forName("org.sqlite.JDBC");

            // ✅ Step 2: Connect to database
            String dbPath = System.getProperty("user.home") + "/Desktop/Movie_App_Pro/javaapp.db";
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            Statement stmt = conn.createStatement();
            stmt.execute("PRAGMA journal_mode=WAL;");
            stmt.execute("PRAGMA busy_timeout=5000;");
            stmt.close();

            return conn;
        } catch (Exception e) {
            System.err.println("❌ DB connection error: " + e.getMessage());
            e.printStackTrace(); // FULL ERROR
            return null;
        }
    }
}
