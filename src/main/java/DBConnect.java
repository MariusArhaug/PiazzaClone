import java.sql.*;

//Connect us to our mySQL database hosted at NTNU's server, we need VPN to make this connection work.
public class DBConnect {
    protected Connection conn;

    //Start connection
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://mysql.stud.ntnu.no/fs_tdt4145_1_gruppe116_piazzadb";
            String username = "fs_tdt4145_1_gruppe116";
            String password = "MarAniLin1337420XD";
            conn = DriverManager.getConnection(url, username, password);
            //System.out.println("success!");
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect", e);
        }
    }
}
