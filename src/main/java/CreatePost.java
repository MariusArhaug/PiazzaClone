import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//Lets users createPosts, add them to folders, and create folders.
public class CreatePost extends DBConnect {

    private final View view = new View();
    private PreparedStatement regStatement;
    public CreatePost() {
        super.connect();
    }

    /**
     * Select course based on user input and return as a new Course object
     * @param courseID the ID of the course we want
     * @return Course object
     */
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

    /**
     * User TUI for filling in form
     * @param course which course the post belongs to
     * @param user who created the post
     * @return Post object
     */
    public Post createPost(Course course, User user) {
        Scanner in = new Scanner(System.in);
        System.out.println("-------Make new post------");

        System.out.println("Post type: (Question, Note, Poll) ");
        String type = in.nextLine().toLowerCase();
        System.out.println("Folders: ");
        int folderID = this.selectFolder(course.getCourseID());
        System.out.println("Summary: ");
        String summary = in.nextLine();
        System.out.println("Details:  ");

        String content = in.nextLine();
        boolean isAnonymous = false;
        if (course.allowAnonymous() && !user.isInstructor()) {
            System.out.println("Anonymous? (y/n):  ");
            isAnonymous = MainController.yes();
        }

        //Once all the info about the post is gathered, create new post
        Post newPost = this.insertPost(type, summary, content, isAnonymous, folderID, course.getCourseID(), user.getUserID());
        System.out.println("You created a new post: ");
        System.out.println(newPost);
        user.increasePostsCreated();
        return newPost;
    }

    /**
     * Insert new post into database and return the post as a Post object.
     * @param type type of post (Question, Note, Poll)
     * @param summary Short summary
     * @param content details
     * @param isAnonymous is anonymous or not
     * @param folderID ID the folder it belongs to
     * @param courseID ID of the course it belongs to
     * @param userID ID of creator.
     * @return Post object.
     */
    private Post insertPost(String type, String summary, String content, boolean isAnonymous, int folderID, int courseID, int userID) {
        try {
            String SQLQuery = "" +
                    "INSERT INTO posts (type, summary, content, isAnonymous, userID, courseID) " +
                    "VALUES ((?), (?), (?), (?), (?), (?))";
            this.regStatement = conn.prepareStatement(SQLQuery, Statement.RETURN_GENERATED_KEYS);

            this.regStatement.setString(1, type);
            this.regStatement.setString(2, summary);
            this.regStatement.setString(3, content);
            this.regStatement.setBoolean(4, isAnonymous);
            this.regStatement.setInt(5, userID);
            this.regStatement.setInt(6, courseID);
            this.regStatement.executeUpdate();
            ResultSet rs = regStatement.getGeneratedKeys();
            int postID = 0;
            if (rs.next())  {
                //We also want the generated primary key value
                postID = Math.toIntExact(rs.getLong(1));
            }
            this.addPostToFolder(postID, folderID);
            return new Post(postID, type, summary, content, 0, isAnonymous, courseID, userID);
        } catch (Exception e) {
            System.out.println();
        }
        return null;
    }

    /**
     * Add post to selected folder
     * @param postID ID of the post
     * @param folderID ID of folder
     */
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

    /**
     * Interface for users to create a folder with required fields
     * @param courseID ID of the course it belongs to
     */
    public void createFolder(int courseID) {
        Scanner in = new Scanner(System.in);
        System.out.println("Folder name: ");
        String folderName = in.nextLine();
        System.out.println("Parent folder (select 0 for none) :  ");
        int superFolderID = this.selectFolder(courseID);
        Folder newFolder = this.insertFolder(courseID, folderName, superFolderID);
        System.out.println("======================================");
        System.out.println("| New folder created! " + newFolder + " |");
        System.out.println("======================================");
    }

    /**
     * Select a folder to corresponding course with courseID
     * @param courseID ID of the course it belonggs to
     * @return ID of folder.
     */
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
            if (courseFolders.stream().anyMatch(e -> e.getFolderID() == folderID) || folderID == 0) {
                return folderID;
            }
            System.out.println("You must choose correct folder ID!");
        }
    }

    /**
     * Insert new folder into database and return the folder as a folder object.
     * @param courseID ID of course it belongs to
     * @param folderName Name of the folder
     * @param superFolderID Parent folder if it is a subfolder.
     * @return Folder object.
     */
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
