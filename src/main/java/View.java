import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//A view class that holds different methods that lets us view the total number of different parts of piazza,
//Courses, Folders, Posts, Users. etc..
public class View extends DBConnect {

    private PreparedStatement regStatement;
    public View() {
        super.connect();
    }

    //View all courses
    public List<Course> viewCourses() {
        try {
            String SQLQuery = "" +
                    "SELECT *   " +
                    "FROM course";
            this.regStatement = conn.prepareStatement(SQLQuery);
            return this.findCourses();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //View the courses that a user is registered for
    public List<Course> viewRegisteredCourses(int userID) {
        try {
            String SQLQuery = "" +
                    "SELECT *   " +
                    "FROM course " +
                    "WHERE courseID IN (" +
                    "   SELECT courseID " +
                    "   FROM userCourse" +
                    "   WHERE userID = (?)" +
                    ") ";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, userID);
            return this.findCourses();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Both viewCourses and viewRegisteredCourses use the same method to return of courses,
    // depending on which method that fired findCourses()
    private List<Course> findCourses() {
        try {
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

    //View posts to a corresponding course with courseID
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

    //View posts that corresponds to both a course with courseID aswell as a user search input.
    public List<Post> viewPosts(int courseID, String searchInput) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM posts " +
                    "WHERE courseID = (?) AND (content LIKE (?) OR summary LIKE (?))";
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
    //both viewPosts methods call the findPost() to return a list of posts to a corresponding course with courseID
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

    //View all the available folders for a given coruse with courseID
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

    //view the different threads to a specific post with postID
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

    //View all users that are not registered to a specific course with courseID
    public List<User> viewUsersNotInCourse(int courseID) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM users " +
                    "WHERE isInstructor = 0 AND userID NOT IN (" +
                    "   SELECT userID" +
                    "   FROM userCourse" +
                    "   WHERE courseID <> (?)" +
                    ")";
            this.regStatement = this.conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, courseID);
            ResultSet rs = this.regStatement.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String[] strings = {
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                };
                boolean isInstructor = rs.getBoolean("isInstructor");
                int[] postCounts = {
                        rs.getInt("postsCreated"),
                        rs.getInt("postsViewed"),
                        rs.getInt("postsLiked")

                };
                users.add(new User(userID, strings, isInstructor, postCounts));
            }
            return users;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
