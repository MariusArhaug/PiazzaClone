package backend;

import backend.DBConnect;
import types.*;
import types.Thread;

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

    /**
     * backend.View all courses
     * @return List of courses
     */
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

    /**
     * backend.View all courses that user is registered to
     * @param userID ID of user
     * @return List of courses
     */
    public List<Course> viewRegisteredCourses(int userID) {
        try {
            String  SQLQuery = "" +
                    "SELECT *" +
                    "FROM course INNER JOIN userCourse uC on course.courseID = uC.courseID  " +
                    "WHERE userID = (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, userID);
            return this.findCourses();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Turn Result from SELECT into List of courses
     * @return List of courses.
     */
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

    /**
     * backend.View posts inside of a course
     * @param courseID ID of course
     * @return List of posts.
     */
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

    /**
     * List of posts that are inside a course and match a searchInput
     * @param courseID ID of course
     * @param searchInput search input
     * @return List of posts.
     */
    public List<Post> viewPosts(int courseID, String searchInput) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM posts " +
                    "WHERE courseID = (?) AND " +
                    "(content LIKE (?) OR summary LIKE (?))";

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


    /**
     * List of posts that are inside a course and folder
     * @param courseID ID of course
     * @param folderID ID of folder
     * @return List of posts
     */
    public List<Post> viewPosts(int courseID, int folderID) {
        try {
            //We're only interested in posts' attributes.
            String SQLQuery = "" +
                    "SELECT posts.* " +
                    "FROM posts INNER JOIN postFolder ON posts.postID = postFolder.postID   " +
                    "WHERE courseID = (?) AND folderID = (?)";
            this.regStatement = conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, courseID);
            this.regStatement.setInt(2, folderID);

            return this.findPosts(courseID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Query database and turn ResultSet into List of posts
     * @param courseID ID of course
     * @return List of posts
     */
    private List<Post> findPosts(int courseID) {
        try {
            ResultSet rs = this.regStatement.executeQuery();
            List<Post> posts = new ArrayList<>();

            //We create types.Post objects that we return in the list
            while (rs.next()) {
                int postID = rs.getInt("postID");
                String type = rs.getString("type");
                String summary = rs.getString("summary");
                String content = rs.getString("content");
                int likes = rs.getInt("likes");
                boolean isAnonymous = rs.getBoolean("isAnonymous");
                int userID = rs.getInt("userID");
                posts.add(new Post(postID, type, summary, content, likes, isAnonymous, courseID, userID));
            }
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * List of folders in a course
     * @param courseID ID of course
     * @return List of folders.
     */
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

    /**
     * List of threads to a specific post
     * @param postID ID of post
     * @return List of threads.
     */
    public List<Thread> viewThreads(int postID) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM threads   " +
                    "WHERE postID = (?)";
            this.regStatement = this.conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, postID);
            ResultSet rs = this.regStatement.executeQuery();
            List<Thread> threads = new ArrayList<>();

            //We create thread objects that we return in the list
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

    /**
     * List of replies to a specific thread
     * @param threadID ID of thread
     * @return List of reply.
     */
    public List<Reply> viewRepliesInThread(int threadID) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM reply   " +
                    "WHERE threadID = (?)";
            this.regStatement = this.conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, threadID);
            ResultSet rs = this.regStatement.executeQuery();
            List<Reply> replies = new ArrayList<>();

            //We create reply objects that we return in the list
            while (rs.next()) {
                int replyID = rs.getInt("replyID");
                String contents = rs.getString("contents");
                boolean isAnonymous = rs.getBoolean("isAnonymous");
                boolean isInstructor = rs.getBoolean("isInstructor");
                int userID = rs.getInt("userID");
                replies.add(new Reply(replyID, threadID, contents, isAnonymous, isInstructor, userID));
            }
            return replies;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * List of all users that are not registered to a specific course with courseID
     * @param courseID ID of course
     * @return List of types.User
     */
    public List<User> viewUsersNotInCourse(int courseID) {
        try {
            String SQLQuery = "SELECT * " +
                    "FROM users " +
                    "WHERE isInstructor = 0 AND userID NOT IN (" +
                    "   SELECT userID" +
                    "   FROM userCourse" +
                    "   WHERE courseID = (?)" +
                    ")";
            this.regStatement = this.conn.prepareStatement(SQLQuery);
            this.regStatement.setInt(1, courseID);
            ResultSet rs = this.regStatement.executeQuery();
            List<User> users = new ArrayList<>();

            //We create user objects that we return in the list
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
