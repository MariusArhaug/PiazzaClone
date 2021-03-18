import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class View extends DBConnect {

    private PreparedStatement regStatement;
    public View() {
        super.connect();
    }

    public List<Folder> viewCourseFolders(int courseID) {
        if (courseID == 0) {
            throw new IllegalArgumentException("Course Not found");
        }
        try {
            String SQLQuery = "SELECT * " +
                    "FROM folders   " +
                    "WHERE courseID = (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, courseID);
            ResultSet rs = this.regStatement.executeQuery();
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

    public List<Course> viewCourses() {
        try {
            String SQLQuery = "" +
                    "SELECT *   " +
                    "FROM course";
            this.regStatement = conn.prepareStatement(SQLQuery);
            ResultSet rs = this.regStatement.executeQuery();

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


    public List<Post> viewPosts(int courseID) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM posts " +
                    "WHERE courseID = (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, courseID);
            return this.findPosts(courseID);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Post> findPosts(int courseID) {
        try {
            ResultSet rs = this.regStatement.executeQuery();
            List<Post> posts = new ArrayList<>();
            while (rs.next()) {
                int postID = rs.getInt("postID");
                String type = rs.getString("type");
                String summary = rs.getString("summary");
                String content = rs.getString("summary");
                int likes = rs.getInt("likes");
                boolean allowAnonymous = rs.getBoolean("allowAnonymous");
                int userID = rs.getInt("userID");
                posts.add(new Post(postID, type, summary, content, likes, allowAnonymous, courseID, userID));
            }
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Post> viewPosts(int courseID, String searchInput) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM posts " +
                    "WHERE courseID = (?) AND (content LIKE (?) OR summary LIKE(?))";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, courseID);
            this.regStatement.setString(2, "%" + searchInput + "%");
            this.regStatement.setString(3, "%" + searchInput + "%");
            return this.findPosts(courseID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Thread> viewThreads(int postID) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM threads   " +
                    "WHERE postID = (?)";
            this.regStatement = this.conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, postID);
            ResultSet rs = this.regStatement.executeQuery();
            List<Thread> threads = new ArrayList<>();

            while (rs.next()) {
                int threadID = rs.getInt("threadID");
                boolean isResolved = rs.getBoolean("isResolved");
                String type = rs.getString("type");
                int timesViewed = rs.getInt("timesViewed");
                int timesCommented = rs.getInt("timesCommented");
                threads.add(new Thread(threadID, isResolved, type, timesViewed, timesCommented, postID));
            }
            return  threads;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
