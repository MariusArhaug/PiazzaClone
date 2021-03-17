
import java.sql.*;
import java.util.Scanner;

public class Login extends DBConnect {

    private PreparedStatement regStatement;

    public Login() {
        super.connect();
    }

    private void startLogin() {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM users" +
                    "   WHERE email = (?) AND password = (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser(String email, String password) {
        this.startLogin();
        try {
            this.regStatement.setString(1, email);
            this.regStatement.setString(2, password);
            ResultSet rs = this.regStatement.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("userID");
                String[] strings = {
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                };
                boolean isInstructor = rs.getBoolean("isInstructor");
                int[] postCounts = {
                        rs.getInt("postsCreated"),
                        rs.getInt("postsViewed"),
                        rs.getInt("postsLiked")

                };
                return new User(userID, strings, isInstructor, postCounts);
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
        return null;
        //String hashedPassword = Register.hashPassword(in.nextLine());

    }
    public static void main(String[] args) {
        Login login = new Login();
        //User loggedInUser = login.loginUser();
        //System.out.println("Success! Logged in as: " + loggedInUser);
    }

}
