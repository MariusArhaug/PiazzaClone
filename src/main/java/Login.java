
import java.sql.*;
import java.util.Scanner;

public class Login extends DBConnect {

    private PreparedStatement regStatement;

    public Login() {
        super.connect();
    }

    private void startLogin() {
        try {
            String SQL = "SELECT * " +
                    "FROM users" +
                    "   WHERE email = (?) AND password = (?)";

            this.regStatement = conn.prepareStatement(SQL);
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public User loginUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("Email: ");
        String email = in.nextLine();
        System.out.println("Password: ");
        String password = in.nextLine();
        this.startLogin();
        try {
            this.regStatement.setString(1, email);
            this.regStatement.setString(2, password);
            //this.regStatement.executeQuery();
            ResultSet rs = this.regStatement.executeQuery();
            User user = null;
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String[] strings  = {
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                };
                boolean isInstructor = rs.getBoolean("isInstructor");
                int[] postCounts = {
                        rs.getInt("postsCreated"),
                        rs.getInt("postsViewed"),
                        rs.getInt("postsLiked")

                };
                user = new User(userID, strings, isInstructor, postCounts);
            }
            System.out.println(user);
            return user;

        } catch(Exception e) {
            return null;
        }
        //String hashedPassword = Register.hashPassword(in.nextLine());

    }
    public static void main(String[] args) {
        Login login = new Login();
        login.loginUser();
    }

}
