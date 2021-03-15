import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;


public class Register extends DBConnect{
    private PreparedStatement regStatement;

    private User user = null;

    public Register() {
        super.connect();
    }

    private void startReg() {
        try {
            String SQL = "INSERT INTO users (name, email, password, isInstructor) " +
                        "VALUES ((?), (?), (?), (?))";

            this.regStatement = conn.prepareStatement(SQL);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void registerUser(User user) {
        this.user = user;
        this.startReg();
        try {
            this.regStatement.setString(1, this.user.getName());
            this.regStatement.setString(2, this.user.getEmail());
            this.regStatement.setString(3, this.user.getPassword());
            this.regStatement.setInt(4, this.user.isInstructor() ? 1 : 0); //Need to convert into tiny int
            this.regStatement.execute();
        } catch (Exception e) {
            System.out.print(e);
        }
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
            return generatedPassword;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println(generatedPassword);
        return null;
    }

    public static void main(String[] args) {
        User marius = new User("Marius", "mariuhar@stud.ntnu.no", "123", true);
        Register register = new Register();
        register.registerUser(marius);
    }

}
