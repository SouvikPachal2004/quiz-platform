import java.sql.*;
import java.util.*;

public class QuizUI {
    private User user;

    public QuizUI(User user) {
        this.user = user;
    }

    public void display() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. Take Quiz");
            System.out.println("2. View Past Results");
            System.out.println("3. View Leaderboard");
            System.out.println("4. Logout");
            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                takeQuiz(sc);
            } else if (choice == 2) {
                ResultViewer.showUserResults(user);
            } else if (choice == 3) {
                showLeaderboard();
            } else {
                break;
            }
        }
    }

    private void takeQuiz(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM quizzes");
            List<Integer> quizIds = new ArrayList<>();
            int i = 1;
            while (rs.next()) {
                quizIds.add(rs.getInt("id"));
                System.out.println(i++ + ". " + rs.getString("title"));
            }

            if (quizIds.isEmpty()) {
                System.out.println("No quizzes available. Please contact admin.");
                return;
            }

            System.out.print("Select quiz number: ");
            int quizChoice = sc.nextInt();
            sc.nextLine();
            int quizId = quizIds.get(quizChoice - 1);

            PreparedStatement qStmt = conn.prepareStatement("SELECT * FROM questions WHERE quiz_id = ?");
            qStmt.setInt(1, quizId);
            ResultSet qrs = qStmt.executeQuery();

            int score = 0;
            int total = 0;
            long startTime = System.currentTimeMillis();

            while (qrs.next()) {
                total++;
                System.out.println("\n" + qrs.getString("question_text"));
                System.out.println("A. " + qrs.getString("option_a"));
                System.out.println("B. " + qrs.getString("option_b"));
                System.out.println("C. " + qrs.getString("option_c"));
                System.out.println("D. " + qrs.getString("option_d"));
                System.out.print("Your answer: ");
                String ans = sc.nextLine().toUpperCase();
                if (ans.equals(qrs.getString("correct_option"))) {
                    score++;
                    System.out.println("Correct!");
                } else {
                    System.out.println("Incorrect! Correct answer was: " + qrs.getString("correct_option"));
                }
            }

            long endTime = System.currentTimeMillis();
            int timeTaken = (int) ((endTime - startTime) / 1000);

            System.out.println("\nQuiz completed. Score: " + score + "/" + total);
            System.out.println("Time taken: " + timeTaken + " seconds");

            PreparedStatement rStmt = conn.prepareStatement(
                    "INSERT INTO results (user_id, quiz_id, score, time_taken_seconds) VALUES (?, ?, ?, ?)"
            );
            rStmt.setInt(1, user.getId());
            rStmt.setInt(2, quizId);
            rStmt.setInt(3, score);
            rStmt.setInt(4, timeTaken);
            rStmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error during quiz: " + e.getMessage());
        }
    }

    private void showLeaderboard() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT u.username, SUM(r.score) AS total_score FROM results r " +
                            "JOIN users u ON r.user_id = u.id " +
                            "GROUP BY r.user_id ORDER BY total_score DESC LIMIT 10"
            );

            System.out.println("\nTop Users Leaderboard:");
            int rank = 1;
            while (rs.next()) {
                System.out.printf("%d. %s - %d points\n", rank++, rs.getString("username"), rs.getInt("total_score"));
            }
        } catch (Exception e) {
            System.out.println("Error loading leaderboard: " + e.getMessage());
        }
    }
}