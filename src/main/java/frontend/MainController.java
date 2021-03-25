package frontend;

import backend.*;
import types.Course;
import types.Post;
import types.User;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//Main controller for Piazza
public class MainController {

    //Regular classes
    private User user;
    private Course course = null;

    //Classes that allow us to interact with database
    private final Login login = new Login();
    private final Register register = new Register();
    private final CreatePost createPost = new CreatePost();
    private final ReplyPost replyPost = new ReplyPost();
    private final View view = new View();
    private final GetStatistics stats = new GetStatistics();

    public MainController() {
        DBConnect connection = new DBConnect();
        connection.connect();
    }

    //Main start tp the program
    public void startProgram() {
        System.out.println("|-------------------Welcome to Piazza---------------------| \n");
        this.user = this.login.loginUser();
        if (this.user == null) this.user = this.register.registerUser();
        System.out.println("--- Success! Welcome " +  this.user.getName() + "! ------- \n");
        this.chooseCourse();
        System.out.println("|---------------| Welcome to: " + this.course + " |--------------| \n");
        while (true) {
            this.selectAction();
            if (this.user.isInstructor()) this.instructorActions();
            if (this.login.logout(this.user)) break;
        }
    }

    //interface to select course
    private void chooseCourse() {
        Scanner in = new Scanner(System.in);
        List<Course> courses = this.view.viewCourses();
        System.out.println("""
                |--------------------------------------------------------|
                | All available courses :                                |""");
        Course.printCourses(courses);
        //See a users courses that he is registered for.
        List<Course> registeredCourses = this.view.viewRegisteredCourses(this.user.getUserID());

        if (registeredCourses == null) {
            System.out.println("It appears that you're not registered for any courses yet! ");
            return;
        }

        System.out.println("""
                |--------------------------------------------------------|
                | All registered courses :                               |""");
        Course.printCourses(registeredCourses);
        while (true) {
            //Make sure that user selects right courseID (only courses that he is registered for)
            System.out.println("""
                    |--------------------------------------------------------|
                    | Select a course ID:                                    |""");
            System.out.println("| [" + registeredCourses
                    .stream()
                    .map(e -> Integer.toString(e.getCourseID()))
                    .collect(Collectors.joining(", ")) + "] \n" +
                    "|--------------------------------------------------------|");
            int courseID = Integer.parseInt(in.nextLine());
            if (registeredCourses.stream().anyMatch(e -> e.getCourseID() == courseID)) {
                this.course = createPost.selectCourse(courseID);
                break;
            }
            System.out.println("You are not registered for this course!");
        }
    }

    //Static method for checking if user inputted "y"
    public static boolean yes() {
        Scanner in = new Scanner(System.in);
        return in.nextLine().equalsIgnoreCase("y");
    }

    //Main hub for different actions a student or instructor may choose
    private void selectAction() {
        System.out.println("""
        |--------------------------------------------------------|
        | View posts?                      | Press: 0            |
        | Create a post?                   | Press: 1            |
        | Look for a post inside a folder? | Press: 2            |
        | Select/reply to a post?          | Press: 3            | 
        | Search for a post?               | Press: 4            |   
        | To continue to next actions.     | Press: any key      |   
        |--------------------------------------------------------|
        """);
        String action = new Scanner(System.in).nextLine();
        switch(action) {
            case "0" -> this.replyPost.printPosts(this.course.getCourseID());                 // backend.View posts
            case "1" -> this.createPost.createPost(this.course, this.user);                   // Create post
            case "2" -> this.replyPost.getPostInFolder(this.course.getCourseID(), this.user); // Find posts in folders
            case "3" -> this.replyPost.replyToPost(this.course.getCourseID(), this.user);     // types.Reply to post
            case "4" -> this.search();                                                        // Search for posts
        }
    }

    //Actions only allowed for instructors.
    public void instructorActions() {
        System.out.println("""
        |--------------------------------------------------------|
        | View user statistics?:          | Press 0              |
        | Create folders for this course? | Press 1              |
        | Invite students to this course? | Press 2              |
        | To continue to next actions.    | Press: any key       |   
        |--------------------------------------------------------|
        """);
        String action = new Scanner(System.in).nextLine();
        switch(action) {
            case "0" -> this.stats.printStats();                                            //Show user statistics
            case "1" -> this.createPost.createFolder(this.course.getCourseID());            //Create folder for course
            case "2" -> this.register.registerUserToCourse(this.course.getCourseID());      //backend.Register user to course
        }
    }

    //Search for a specific post where content/summary has to match with search input.
    private void search() {
        while (true) {
            System.out.println("Search: ");
            String searchInput = new Scanner(System.in).nextLine();
            System.out.println("You searched for: " + searchInput);
            List<Post> posts = this.view.viewPosts(this.course.getCourseID(), searchInput);
            if (posts.isEmpty()) {
                System.out.println("It appears that there are currently no existing posts with this input!");
                System.out.println("Do you want to try again? (y/n)");
                if (yes()) continue;
                break;
            }
            System.out.println("Found " + posts.size() + " posts!");
            System.out.println(posts.stream().map(Object::toString).collect(Collectors.joining("\n")));
            this.user.increasePostsViewed();
            break;
        }
    }


    public static void main(String[] args) {
        MainController controller = new MainController();
        controller.startProgram();
    }
}
