package app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_HOST = getEnv("DB_HOST", "localhost");
    private static final String DB_PORT = getEnv("DB_PORT", "5432");
    private static final String DB_NAME = getEnv("DB_NAME", "tododb");
    private static final String DB_USER = getEnv("DB_USER", "postgres");
    private static final String DB_PASSWORD = getEnv("DB_PASSWORD", "postgres");

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    private static String getUrl() {
        return "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), DB_USER, DB_PASSWORD);
    }

    public static void init() {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to PostgreSQL database!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    // Get all tasks
    public static List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, completed, created_at FROM tasks ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getBoolean("completed"),
                    rs.getTimestamp("created_at").toString()
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error getting tasks: " + e.getMessage());
        }
        return tasks;
    }

    // Add a new task
    public static Task addTask(String title) {
        String sql = "INSERT INTO tasks (title) VALUES (?) RETURNING id, title, completed, created_at";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getBoolean("completed"),
                    rs.getTimestamp("created_at").toString()
                );
            }
        } catch (SQLException e) {
            System.out.println("Error adding task: " + e.getMessage());
        }
        return null;
    }

    // Toggle task completion
    public static boolean toggleTask(int id) {
        String sql = "UPDATE tasks SET completed = NOT completed WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error toggling task: " + e.getMessage());
        }
        return false;
    }

    // Delete a task
    public static boolean deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
        }
        return false;
    }
}
