import java.security.MessageDigest;
import java.sql.*;

public class AuthService {
    public boolean register(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String hash = hashPassword(password);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password_hash) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, hash);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String hash = hashPassword(password);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password_hash=?");
            stmt.setString(1, username);
            stmt.setString(2, hash);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setAdmin(rs.getBoolean("is_admin"));
                return user;
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b));
        return hex.toString();
    }
}