import java.sql.*;


public class Register extends DBConnect{
    private PreparedStatement regStatement;

    private User user = null;

    public Register() {
        super.connect();
    }

    private void startReg() {
        try {

            this.regStatement = conn.prepareStatement("" +
                    "INSERT INTO users (name, email, password, isInstructor) " +
                    "VALUES ((?), (?), (?), (?))"
            );
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

    public static void main(String[] args) {
        User marius = new User("Marius", "mariuhar@stud.ntnu.no", "123", true);
        Register register = new Register();
        register.registerUser(marius);
    }

}
