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
     * @return User object
     */
    public User getUser(String email, String password) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM users" +
                    "   WHERE email = (?) AND password = (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);

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
                return new User(userID, strings, isInstructor, postCounts);
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
        return null;
    }


    /**
     * Interface to fill in user info.
     * @return User object.
     */
    public User loginUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("| It appears that you're not logged in, please log in.    |");
        System.out.println("|------------------------Login----------------------------|");
        System.out.println("| Email: ");
        String email = in.nextLine();
        System.out.println("| Password: ");
        String password = in.nextLine();
        return this.getUser(email, password);
    }

    /**
     * Update user values in database.
     * @param user User object.
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
