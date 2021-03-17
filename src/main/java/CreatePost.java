import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class CreatePost extends DBConnect {


    //private final Post post;
    private PreparedStatement regStatment;
    public CreatePost() {
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

    public HashMap<Integer, String> viewCourseFolders(int courseID) {
        if (courseID == 0) {
            throw new IllegalArgumentException("Course Not found");
        }
        try {
            String SQLQuery = "SELECT * " +
                    "FROM folders   " +
                    "WHERE courseID = (?)";
            this.regStatment = conn.prepareStatement(SQLQuery);
            this.regStatment.setInt(1, courseID);
            return this.translateFolder(this.regStatment.executeQuery());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<Integer, String> translateFolder(ResultSet rs) {
        HashMap<Integer, String> folders = new HashMap<>();
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

    public HashMap<Integer, String>  viewCourses() {
        try {
            String SQLQuery = "" +
                    "SELECT *   " +
                    "FROM course";
            this.regStatment = conn.prepareStatement(SQLQuery);
            return this.translateCourse(this.regStatment.executeQuery());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<Integer, String> translateCourse(ResultSet rs) {
        HashMap<Integer, String> courses = new HashMap<>();
        try {
            while (rs.next()) {
                Integer courseID = rs.getInt("courseID");
                String courseName = rs.getString("name");
                courses.put(courseID, courseName);
            }
            return courses;
        } catch(Exception e) {
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
        HashMap<Integer, String> courseFolders = this.viewCourseFolders(courseID);
        System.out.println(courseFolders.values());
        System.out.println("Select a folder: " + courseFolders.keySet());
        String folderID = in.nextLine();
        String folderName = courseFolders.get(Integer.parseInt(folderID));

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
            this.regStatment.setString(2, newPost.getSummary());
            this.regStatment.setString(3, newPost.getContent());
            this.regStatment.setBoolean(4, newPost.isAllowAnonymous());
            this.regStatment.setInt(5, courseID);
            this.regStatment.setInt(6, userID);
            this.regStatment.execute();
        } catch(Exception e) {
            System.out.println();
        }
    }

}
