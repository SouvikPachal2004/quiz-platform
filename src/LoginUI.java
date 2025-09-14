import java.util.Scanner;

public class LoginUI {
    public void display() {
        Scanner sc = new Scanner(System.in);
        AuthService auth = new AuthService();
        System.out.println("1. Register\n2. Login");
        int choice = sc.nextInt();
        sc.nextLine();

        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        if (choice == 1) {
            if (auth.register(username, password)) {
                System.out.println("Registered successfully!");
            } else {
                System.out.println("Registration failed.");
            }
        } else if (choice == 2) {
            User user = auth.login(username, password);
            if (user != null) {
                System.out.println("Login successful. Welcome, " + user.getUsername());
                if (user.isAdmin()) {
                    new AdminUI(user).display();
                } else {
                    new QuizUI(user).display();
                }
            } else {
                System.out.println("Login failed.");
            }
        }
    }
}