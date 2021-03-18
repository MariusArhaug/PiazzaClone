import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CreatePost extends DBConnect {


    //private final Post post;
    private Post newPost;
    private PreparedStatement regStatment;
    public CreatePost() {
        super.connect();
    }

    public void startPost() {
        try {
            String SQLQuery = "" +
                    "INSERT INTO posts (type, summary, content, allowAnonymous, userID, courseID) " +
                    "VALUES ((?), (?), (?), (?), (?), (?))";
            this.regStatment = conn.prepareStatement(SQLQuery, Statement.RETURN_GENERATED_KEYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Folder> viewCourseFolders(int courseID) {
        if (courseID == 0) {
            throw new IllegalArgumentException("Course Not found");
        }
        try {
            String SQLQuery = "SELECT * " +
                    "FROM folders   " +
                    "WHERE courseID = (?)";
            this.regStatment = conn.prepareStatement(SQLQuery);
            this.regStatment.setInt(1, courseID);
            ResultSet rs = this.regStatment.executeQuery();
            List<Folder> folders = new ArrayList<>();

            while (rs.next()) {
                int folderID = rs.getInt("folderID");
                String folderName = rs.getString("name");
                int superFolderID = rs.getInt("superFolderID");
                folders.add(new Folder(folderID, folderName, courseID, superFolderID));
            }
            return folders;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Course selectCourse(String courseID) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM course   " +
                    "WHERE courseID= (?)";
            this.regStatment = conn.prepareStatement(SQLQuery);
            this.regStatment.setString(1, courseID);
            ResultSet rs = this.regStatment.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                char term = rs.getString("term").charAt(0);
                boolean allowAnonymous = rs.getBoolean("allowAnonymous");
                return new Course(Integer.parseInt(courseID), name, term, allowAnonymous);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> viewCourses() {
        try {
            String SQLQuery = "" +
                    "SELECT *   " +
                    "FROM course";
            this.regStatment = conn.prepareStatement(SQLQuery);
            ResultSet rs = this.regStatment.executeQuery();

            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                int courseID = rs.getInt("courseID");
                String courseName = rs.getString("name");
                char term = rs.getString("term").charAt(0);
                boolean allowAnonymous = rs.getBoolean("allowAnonymous");
                courses.add(new Course(courseID, courseName, term, allowAnonymous));
            }
            return courses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /*public void viewPosts() {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM posts";
            this.regStatment = conn.prepareStatement(SQLQuery);
            return this.translateFolder(this.regStatment.executeQuery());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<Integer, String> translatePosts(ResultSet rs) {
        HashMap<Integer, String> posts = new HashMap<>();
        try {
            while (rs.next()) {
                Integer folderID = rs.getInt("folderID");
                String folderName = rs.getString("name");
                folders.put(folderID, folderName);
            }
            return folders;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public void newPost(int courseID, int userID) {
        Scanner in = new Scanner(System.in);
        System.out.println("-------Make new post------");

        System.out.println("Post type: (Question, Note, Poll) ");
        String type = in.nextLine().toLowerCase();
        System.out.println("Folders:");
        List<Folder> courseFolders = this.viewCourseFolders(courseID);
        System.out.println(courseFolders
                .stream()
                .map(Folder::toString)
                .collect(Collectors.joining(" ")));
        System.out.println("Select a folder: [" + courseFolders
                .stream()
                .map(e -> Integer.toString(e.getFolderID()))
                .collect(Collectors.joining(", ")) + "]");
        int folderID = Integer.parseInt(in.nextLine());
        /*Folder folder = courseFolders
                .stream()
                .filter(e -> e.getFolderID() == folderID)
                .collect(Collectors.toList())
                .get(0);*/

        System.out.println("Summary: ");
        String summary = in.nextLine();
        System.out.println("Your question:  ");

        String content = in.nextLine();
        System.out.println("Anonymous? (y/n):  ");
        boolean allowAnonymous = in.nextLine().equalsIgnoreCase("y");
        this.newPost = new Post(type, summary, content, allowAnonymous, courseID, userID);

        this.startPost();
        try {
            this.regStatment.setString(1, this.newPost.getType());
            this.regStatment.setString(2, this.newPost.getSummary());
            this.regStatment.setString(3, this.newPost.getContent());
            this.regStatment.setBoolean(4, this.newPost.isAllowAnonymous());
            this.regStatment.setInt(5, courseID);
            this.regStatment.setInt(6, userID);
            this.regStatment.executeUpdate();
            ResultSet rs = regStatment.getGeneratedKeys();
            if (rs.next())  {
                this.newPost.setPostID(Math.toIntExact(rs.getLong(1)));
            }
            this.addPostToFolder(folderID);
            System.out.println("Success!");
        } catch (Exception e) {
            System.out.println();
        }
    }

    private void addPostToFolder(int folderID) {
        try {
            String SQLQuery = "" +
                    "INSERT INTO postFolder (postID, folderID) " +
                    "VALUES ((?) , (?))";
            this.regStatment = conn.prepareStatement(SQLQuery);
            this.regStatment.setInt(1, this.newPost.getPostID());
            this.regStatment.setInt(2, folderID);
            this.regStatment.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
