import java.sql.*;
import java.util.*;

public class Register extends DBConnect{
    private PreparedStatement regStatement;

    private User user;

    public Register(User user) {
        this.user = user;
    }

    public void startReg() {
        try {
            regStatement = conn.prepareStatement("INSERT INTO Posts VALUES ((?), (?), (?))");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
