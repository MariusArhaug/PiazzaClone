import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;


public class Register extends DBConnect{
    private PreparedStatement regStatement;

    private User user = null;
    private final Login login = new Login();

    public Register() {
        super.connect();
    }

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
        //System.out.println(generatedPassword);
        return generatedPassword;
    }


    private void startReg() {
        try {
            String SQL = "INSERT INTO users (name, email, password, isInstructor) " +
                        "VALUES ((?), (?), (?), (?))";

            this.regStatement = conn.prepareStatement(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean insertUser(User user) {
        User existingUser = this.login.getUser(user.getEmail(), user.getPassword());
        this.user = user;

        if (existingUser != null) {
            System.out.println("This user already exists, please choose another email");
            return false;
        }

        this.startReg();
        try {
            this.regStatement.setString(1, this.user.getName());
            this.regStatement.setString(2, this.user.getEmail());
            this.regStatement.setString(3, this.user.getPassword());
            this.regStatement.setBoolean(4, this.user.isInstructor()); //Need to convert into tiny int
            this.regStatement.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User registerUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("Name: ");
        String name = in.nextLine();
        System.out.println("Email: ");
        String email = in.nextLine();
        System.out.println("Password: ");
        String password = in.nextLine();
        System.out.println("Instructor: (y/n)");
        boolean isInstructor = in.nextLine().equalsIgnoreCase("y");
        return new User(name, email, password, isInstructor);
    }


    public static void main(String[] args) {
        /* User marius = new User("Marius", "mariuhar@stud.ntnu.no", "124", true);
        // Register register = new Register();
        register.registerUser(marius);*/
    }

}
