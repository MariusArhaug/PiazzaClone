import java.sql.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CreatePost extends DBConnect {

    //Global field for the new post we create
    private final View view = new View();
    private PreparedStatement regStatement;
    public CreatePost() {
        super.connect();
    }

    //Select course based on user input
    public Course selectCourse(int courseID) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM course   " +
                    "WHERE courseID= (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, courseID);
            ResultSet rs = this.regStatement.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                char term = rs.getString("term").charAt(0);
                boolean allowAnonymous = rs.getBoolean("allowAnonymous");
                return new Course(courseID, name, term, allowAnonymous);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Interface for user to create a post
    public Post createPost(int courseID, int userID) {
        Scanner in = new Scanner(System.in);
        System.out.println("-------Make new post------");

        System.out.println("Post type: (Question, Note, Poll) ");
        String type = in.nextLine().toLowerCase();
        System.out.println("Folders:");
        List<Folder> courseFolders = this.view.viewCourseFolders(courseID);
        System.out.println(courseFolders
                .stream()
                .map(Folder::toString)
                .collect(Collectors.joining(" ")));
        System.out.println("Select a folder: [" + courseFolders
                .stream()
                .map(e -> Integer.toString(e.getFolderID()))
                .collect(Collectors.joining(", ")) + "]");
        int folderID = Integer.parseInt(in.nextLine());

        System.out.println("Summary: ");
        String summary = in.nextLine();
        System.out.println("Your question:  ");

        String content = in.nextLine();
        System.out.println("Anonymous? (y/n):  ");
        boolean allowAnonymous = in.nextLine().equalsIgnoreCase("y");
        return this.insertPost(type, summary, content, allowAnonymous, folderID, courseID, userID);
    }

    //insert post into database
    private Post insertPost(String type, String summary, String content, boolean allowAnonymous, int folderID, int courseID, int userID) {
        try {
            String SQLQuery = "" +
                    "INSERT INTO posts (type, summary, content, allowAnonymous, userID, courseID) " +
                    "VALUES ((?), (?), (?), (?), (?), (?))";
            this.regStatement = conn.prepareStatement(SQLQuery, Statement.RETURN_GENERATED_KEYS);

            this.regStatement.setString(1, type);
            this.regStatement.setString(2, summary);
            this.regStatement.setString(3, content);
            this.regStatement.setBoolean(4, allowAnonymous);
            this.regStatement.setInt(5, courseID);
            this.regStatement.setInt(6, userID);
            this.regStatement.executeUpdate();
            ResultSet rs = regStatement.getGeneratedKeys();
            int postID = 0;
            if (rs.next())  {
                postID = Math.toIntExact(rs.getLong(1));
            }
            this.addPostToFolder(postID, folderID);
            return new Post(postID, type, summary, content, 0, allowAnonymous, courseID, userID);
        } catch (Exception e) {
            System.out.println();
        }
        return null;
    }

    //Add post to selected folder
    private void addPostToFolder(int postID, int folderID) {
        try {
            String SQLQuery = "" +
                    "INSERT INTO postFolder (postID, folderID) " +
                    "VALUES ((?) , (?))";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, postID);
            this.regStatement.setInt(2, folderID);
            this.regStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
