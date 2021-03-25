package backend;

import frontend.MainController;
import types.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

//Log user in to program, and update user values in database.
public class Login extends DBConnect {

    private PreparedStatement regStatement;

    public Login() {
        super.connect();
    }

    /**
     * /Get user from database with corresponding email and password
     * @param email user email.
     * @param password user password
     * @return types.User object
     */
    public User getUser(String email, String password) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM users" +
                    "   WHERE email = (?) AND password = (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setString(1, email);
            this.regStatement.setString(2, password);
            ResultSet rs = this.regStatement.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("userID");
                String[] userInfo = {
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
                return new User(userID, userInfo, isInstructor, postCounts);
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    /**
     * Interface to fill in user info.
     * @return types.User object.
     */
    public User loginUser() {
        while (true) {
            Scanner in = new Scanner(System.in);
            System.out.println("""
            |---------------------------------------------------------|
            | It appears that you're not logged in, please log in.    |
            | To register: | Enter blank both times.                  |
            |------------------------Login----------------------------|
            """);
            System.out.println("| Email: ");
            String email = in.nextLine();
            System.out.println("| Password: ");
            String password = Register.hashPassword(in.nextLine());
            User user = this.getUser(email, password);
            if (user == null) {
                System.out.println("""
                |--------------------------------------------------------|
                | This account does not exist!  |                        |
                | Retry to log in               | Press: y               |
                | Register new account?         | Press: any key         |
                |--------------------------------------------------------|
                """);
                if (MainController.yes()) continue;
                break;
            }
            return user;
        }
        return null;
    }

    //Logout user, update user stats in database if he/she has seen/created posts
    public boolean logout(User user) {
        System.out.println("""
        |--------------------------------------------------------|
        | Do you want to log out? (y/n)                          |
        |--------------------------------------------------------|
        """);
        if (MainController.yes()) {
            if (user.hasUpdated()) this.updateUser(user);
            System.out.println("Bye bye " + user.getName() + "!");
            return true;
        }
        return false;
    }

    /**
     * Update user values in database.
     * @param user types.User object.
     */
    public void updateUser(User user) {
        try {
            String SQLQuery = "" +
                    "   UPDATE users " +
                    "   SET postsCreated = (?), postsViewed=(?), postsLiked = (?)" +
                    "   WHERE userID = (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, user.getPostsCreated());
            this.regStatement.setInt(2, user.getPostsViewed());
            this.regStatement.setInt(3, user.getPostsLiked());
            this.regStatement.setInt(4, user.getUserID());
            this.regStatement.executeUpdate();
            System.out.println(" New stats saved!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
