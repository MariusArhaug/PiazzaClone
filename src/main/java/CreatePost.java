import java.sql.*;
import java.util.Scanner;

public class CreatePost extends DBConnect {


    //private final Post post;
    private PreparedStatement regStatment;
    public CreatePost(Post post) {
        //this.post = post;
        super.connect();
    }

    public void startPost() {
        try {
            String SQLQuery = "" +
                    "INSERT INTO posts (type, summary, content, allowAnonymous, userID, courseID) " +
                    "VALUES ((?), (?), (?), (?), (?), (?))";
            this.regStatment = conn.prepareStatement(SQLQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getFolder(String folderName, int courseID) {
        if (courseID == 0) {
            throw new IllegalArgumentException("Course Not found");
        }
        if (folderName.equals("")) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        try {
            String SQLQuery = "SELECT * " +
                    "FROM folders   " +
                    "WHERE name= (?) AND courseID = (?)";
            this.regStatment = conn.prepareStatement(SQLQuery);
            this.regStatment.setString(1, folderName);
            this.regStatment.setInt(2, courseID);
            return this.regStatment.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getCourse(String courseName, String term) {
        if (!term.equals("V") && !term.equals("H")) {
            throw new IllegalArgumentException("Term must be either \"V\" or \"H\"");
        }
        if (courseName.equals("")) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        try {
            String SQLQuery = "SELECT * " +
                    "FROM course   " +
                    "WHERE name= (?)";
            this.regStatment = conn.prepareStatement(SQLQuery);
            this.regStatment.setString(1, courseName);
            return this.regStatment.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public void sendPost() {
        Scanner in = new Scanner(System.in);
        System.out.println("-------Make new post------");

        System.out.println("Post type: (Question, Note, Poll) ");
        String type = in.nextLine().toLowerCase();
        System.out.println("Select folder: (Question, Note, Poll) ");
        String folderName = in.nextLine().toLowerCase();
        System.out.println("Summary: ");
        String summary = in.nextLine();
        System.out.println("Your question:  ");
        String content = in.nextLine();
        System.out.println("Anonymous? (y/n):  ");
        boolean allowAnonymous = in.nextLine().equalsIgnoreCase("y");
        Post newPost = new Post(type, summary, content, allowAnonymous);
        this.startPost();
        try {
            this.regStatment.setString(1, newPost.getType());
        } catch(Exception e) {
            System.out.println();
        }


        startPost();

    }

}
