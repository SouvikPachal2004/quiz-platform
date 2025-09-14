import java.sql.*;
import java.util.*;

public class AdminUI {
    private User user;

    public AdminUI(User user) {
        this.user = user;
    }

    public void display() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Create Quiz");
            System.out.println("2. Add Question to Quiz");
            System.out.println("3. Logout");
            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try (Connection conn = DBConnection.getConnection()) {
                if (choice == 1) {
                    System.out.print("Enter quiz title: ");
                    String title = sc.nextLine();
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO quizzes (title) VALUES (?)");
                    stmt.setString(1, title);
                    stmt.executeUpdate();
                    System.out.println("Quiz created successfully.");
                } else if (choice == 2) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM quizzes");
                    List<Integer> quizIds = new ArrayList<>();
                    int i = 1;
                    while (rs.next()) {
                        quizIds.add(rs.getInt("id"));
                        System.out.println(i++ + ". " + rs.getString("title"));
                    }
                    System.out.print("Select quiz number: ");
                    int quizChoice = sc.nextInt();
                    sc.nextLine();
                    int quizId = quizIds.get(quizChoice - 1);

                    System.out.print("Enter question text: ");
                    String question = sc.nextLine();
                    System.out.print("Option A: ");
                    String a = sc.nextLine();
                    System.out.print("Option B: ");
                    String b = sc.nextLine();
                    System.out.print("Option C: ");
                    String c = sc.nextLine();
                    System.out.print("Option D: ");
                    String d = sc.nextLine();
                    System.out.print("Correct option (A/B/C/D): ");
                    String correct = sc.nextLine().toUpperCase();

                    PreparedStatement qStmt = conn.prepareStatement(
                            "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)"
                    );
                    qStmt.setInt(1, quizId);
                    qStmt.setString(2, question);
                    qStmt.setString(3, a);
                    qStmt.setString(4, b);
                    qStmt.setString(5, c);
                    qStmt.setString(6, d);
                    qStmt.setString(7, correct);
                    qStmt.executeUpdate();
                    System.out.println("Question added successfully.");
                } else if (choice == 3) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}