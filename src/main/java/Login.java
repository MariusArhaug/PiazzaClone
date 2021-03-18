
import java.sql.*;
import java.util.Scanner;

public class Login extends DBConnect {

    private PreparedStatement regStatement;

    public Login() {
        super.connect();
    }

    //Prepare statement
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

    //get user from database
    public User getUser(String email, String password) {
        this.startLogin();
        try {
            this.regStatement.setString(1, email);
            this.regStatement.setString(2, Register.hashPassword(password));
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
                //Return as a user object.
                return new User(userID, strings, isInstructor, postCounts);
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
        return null;
    }
    //Interface to fill in user info.
    public User loginUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("It appears that you're not logged in, please log in.");
        System.out.println("----------Login--------");
        System.out.println("Email: ");
        String email = in.nextLine();
        System.out.println("Password: ");
        String password = in.nextLine();
        return this.getUser(email, password);
    }
}
