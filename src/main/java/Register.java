import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;


public class Register extends DBConnect {

    private PreparedStatement regStatement;

    private final Login login = new Login();

    public Register() {
        super.connect();
    }

    //Hash passwords, cause why not?
    public static String hashPassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(passwordToHash.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


    private User insertUser(String name, String email, String password, boolean isInstructor) {
        User existingUser = this.login.getUser(email, password);

        if (existingUser != null) {
            System.out.println("This user already exists, please choose another email");
            return null;
        }

        try {
            String SQL = "INSERT INTO users (name, email, password, isInstructor) " +
                    "VALUES ((?), (?), (?), (?))";

            this.regStatement = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            this.regStatement.setString(1, name);
            this.regStatement.setString(2, email);
            this.regStatement.setString(3, password);
            this.regStatement.setBoolean(4, isInstructor);
            this.regStatement.executeUpdate();
            ResultSet rs = this.regStatement.getGeneratedKeys();
            int userID = 0;
            if (rs.next()) {
                userID = Math.toIntExact(rs.getLong(1));
            }
            return new User(userID, name, email, password, isInstructor);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User registerUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("Name: ");
        String name = in.nextLine();
        System.out.println("Email: ");
        String email = in.nextLine();
        System.out.println("Password: ");
        String password = Register.hashPassword(in.nextLine());
        System.out.println("Instructor: (y/n)");
        boolean isInstructor = in.nextLine().equalsIgnoreCase("y");
        return this.insertUser(name, email, password, isInstructor);
    }
}
