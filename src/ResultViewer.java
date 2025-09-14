import java.sql.*;

public class ResultViewer {
    public static void showUserResults(User user) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT q.title, r.score, r.time_taken_seconds, r.attempt_date FROM results r JOIN quizzes q ON r.quiz_id = q.id WHERE r.user_id = ?"
            );
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            System.out.println("\nYour Quiz History:");
            while (rs.next()) {
                System.out.println("Quiz: " + rs.getString("title") +
                        ", Score: " + rs.getInt("score") +
                        ", Time: " + rs.getInt("time_taken_seconds") + "s" +
                        ", Date: " + rs.getTimestamp("attempt_date"));
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve results: " + e.getMessage());
        }
    }
}