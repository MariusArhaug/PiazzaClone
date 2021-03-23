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
        this.loginUser();
        System.out.println("| Success! Welcome " +  this.user.getName() + "! \n");
        this.chooseCourse();
        System.out.println("|---------------| Welcome to: " + this.course + " |--------------| \n");
        while (true) {
            this.selectAction();
            if (this.user.isInstructor()) this.instructorActions();
            System.out.println("""
                    |--------------------------------------------------------|
                    | Do you want to log out? (y/n)                          |
                    |--------------------------------------------------------|
                    """);
            if (!yes()) continue;
            break;
        }
        this.logout();
    }

    //Interface to log in user.
    private void loginUser() {
        //If we don't find user in db we get user = null
        User user;
        while (true) {
            user = this.login.loginUser();
            if (user == null) {
                System.out.println("""
                        |--------------------------------------------------------|
                        | This account does not exist!  |                        |
                        | Retry?                        | Press: y               |
                        | Register new account?         | Press: any key         |
                        |--------------------------------------------------------|
                         """);
                if (yes()) continue;
                break;
            }
            this.user = user;
            return;
        }

        while (true) {
                user = this.register.registerUser();

            if (user == null) {
                System.out.println("An error has occurred! Please try again: ");
                continue;
            }
            this.user = user;
            return;
        }
    }

    //interface to select course
    private void chooseCourse() {
        Scanner in = new Scanner(System.in);
        List<Course> courses = this.view.viewCourses();
        System.out.println("All available courses on Piazza: ");
        System.out.println(courses
                .stream()
                .map(e -> e.getName() + " ID: " + e.getCourseID())
                .collect(Collectors.joining(" ")) + "\n"
        );
        //See a users courses that he is registered for.
        List<Course> registeredCourses = this.view.viewRegisteredCourses(this.user.getUserID());

        if (registeredCourses == null) {
            System.out.println("It appears that you're not registered for any courses yet! ");
            return;
        }

        System.out.println("All registered courses: ");
        System.out.println(registeredCourses
                .stream()
                .map(e -> e.getName() + " ID: " + e.getCourseID())
                .collect(Collectors.joining(" "))
        );
        while (true) {
            //Make sure that user selects right courseID (only courses that he is registered for)
            System.out.println("Select a courseID: [" + registeredCourses
                    .stream()
                    .map(e -> Integer.toString(e.getCourseID()))
                    .collect(Collectors.joining(", ")) + "]");
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
        | For none press enter.            |                     |   
        |--------------------------------------------------------|
        """);
        String action = new Scanner(System.in).nextLine();
        switch(action) {
            case "0" -> this.printPosts(this.course.getCourseID());         // View posts
            case "1" -> this.createPost.createPost(this.course, this.user); // Create post
            case "2" -> this.getPostInFolder();                             // Look for post in folders
            case "3" -> this.replyToPost();                                 // Reply to post
            case "4" -> this.search();                                      // Search for posts
        }
    }

    //Actions only allowed for instructors.
    public void instructorActions() {
        System.out.println("""
        |--------------------------------------------------------|
        | View user statistics?:          | Press 0              |
        | Create folders for this course? | Press 1              |
        | Invite students to this course? | Press 2              |
        | For none press enter.           |                      |   
        |--------------------------------------------------------|
        """);
        String action = new Scanner(System.in).nextLine();
        switch(action) {
            case "0" -> this.stats.printStats();                                        //Show statistics
            case "1" -> this.createPost.createFolder(this.course.getCourseID());        //Create a folder for course
            case "2" -> this.register.registerUserToCourse(this.course.getCourseID());  //Register user to course
        }
    }

    //Find posts that belong to a specific folder
    public void getPostInFolder() {
        int folderID = this.createPost.selectFolder(this.course.getCourseID());
        List<Post> posts = this.view.viewPosts(this.course.getCourseID(), folderID);
        if (posts.isEmpty()) {
            System.out.println("It appears that the folder has no posts yet!");
            return;
        }

        System.out.println(posts
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n")));
        this.user.increasePostsViewed();
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

    //Print posts to a corresponding course with courseID
    private void printPosts(int courseID) {
        System.out.println("Current posts:" );
        List<Post> posts = this.view.viewPosts(courseID);
        if (posts.isEmpty()) {
            System.out.println("It appears that the course: " + this.createPost.selectCourse(courseID) + " has no posts yet!");
        } else {
            System.out.println(posts.stream().map(Object::toString).collect(Collectors.joining("\n")));
        }
    }


    //Reply to a given post
    public void replyToPost() {
        this.printPosts(this.course.getCourseID());
        List<Post> posts = this.view.viewPosts(this.course.getCourseID());
        if (posts.isEmpty()) {
            return;
        }
        int postID;
        while (true) {
            System.out.println("""
            |--------------------------------------------------------|
            | Select a post nr you want to reply to:                 |
            | Cancel?   | Press -1 to                                |
            |--------------------------------------------------------|
            """);
            postID = Integer.parseInt(new Scanner(System.in).nextLine());
            if (postID == -1) return;

            int finalPostID = postID;
            if (posts.stream().anyMatch(e -> e.getPostID() == finalPostID)) break;
            System.out.println("You have to choose a valid post nr!");
        }
        Thread thread = this.replyPost.selectThread(postID, this.user.isInstructor());
        if (thread == null) return;
        this.replyPost.newReply(thread.getThreadID(), this.user);
    }

    //Logout user, update user stats in database if he/she has seen/created posts
    private void logout() {
        if (this.user.hasUpdated()) this.login.updateUser(this.user);
        System.out.println("Bye bye " + this.user.getName() + "!");
    }

    public static void main(String[] args) {
        MainController controller = new MainController();
        controller.startProgram();
    }
}
