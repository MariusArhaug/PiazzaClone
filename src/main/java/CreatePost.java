import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//Lets users createPosts, add them to folders, and create folders.
public class CreatePost extends DBConnect {

    //Initialize class
    private final View view = new View();
    private PreparedStatement regStatement;
    public CreatePost() {
        super.connect();
    }

    //Select course based on user input and return as a new Course object
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
    public Post createPost(int courseID, int userID, boolean allowAnonymous) {
        Scanner in = new Scanner(System.in);
        System.out.println("-------Make new post------");

        System.out.println("Post type: (Question, Note, Poll) ");
        String type = in.nextLine().toLowerCase();
        System.out.println("Folders: ");
        int folderID = this.selectFolder(courseID);
        System.out.println("Summary: ");
        String summary = in.nextLine();
        System.out.println("Your question:  ");

        String content = in.nextLine();
        boolean isAnonymous = false;
        if (allowAnonymous) {
            System.out.println("Anonymous? (y/n):  ");
            isAnonymous = in.nextLine().equalsIgnoreCase("y");
        }
        //Once all the info about the post is gathered, create new post
        return this.insertPost(type, summary, content, isAnonymous, folderID, courseID, userID);
    }

    //Insert new post into database and return the post as a Post object.
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
                //We also want the generated primary key value
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

    //Interface for users to create a folder with required fieldss
    public Folder createFolder(int courseID) {
        Scanner in = new Scanner(System.in);
        System.out.println("Folder name: ");
        String folderName = in.nextLine();
        System.out.println("Parent folder (select 0 for none) :  ");
        int superFolderID = this.selectFolder(courseID);
        return this.insertFolder(courseID, folderName, superFolderID);
    }

    //Select a folder to corresponding course with courseID
    public int selectFolder(int courseID) {
        Scanner in = new Scanner(System.in);
        List<Folder> courseFolders = this.view.viewCourseFolders(courseID);
        System.out.println(courseFolders
                .stream()
                .map(Folder::toString)
                .collect(Collectors.joining(" ")));
        while (true) {
            System.out.println("Select a folder: [" + courseFolders
                    .stream()
                    .map(e -> Integer.toString(e.getFolderID()))
                    .collect(Collectors.joining(", ")) + "]");
            int folderID = Integer.parseInt(in.nextLine());
            if (courseFolders.stream().anyMatch(e -> e.getFolderID() == folderID)) {
                return folderID;
            }
            System.out.println("You must choose correct folder ID!");
        }
    }

    //Insert new folder into database and return the folder as a folder object.
    private Folder insertFolder(int courseID, String folderName, int superFolderID) {
        try {
            String SQLQuery = "" +
                    "INSERT INTO folders (name, courseID, superFolderID)" +
                    "VALUES ((?), (?), (?))";

            this.regStatement = this.conn.prepareStatement(SQLQuery, Statement.RETURN_GENERATED_KEYS);
            this.regStatement.setString(1, folderName);
            this.regStatement.setInt(2, courseID);
            if (superFolderID == 0) {
                this.regStatement.setNull(3, Types.NULL);
            } else {
                this.regStatement.setInt(3, superFolderID);
            }
            this.regStatement.executeUpdate();
            ResultSet rs = this.regStatement.getGeneratedKeys();
            if (rs.next()) {
                return new Folder(Math.toIntExact(rs.getLong(1)), folderName, courseID, superFolderID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
