import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

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
    //Interface to log in user.
    private User start() {
        User user = this.login.loginUser();
        if (user == null) {
            System.out.println("This account does not exist yet! Please register");
            while (true) {
                user = this.register.registerUser();

                if (user != null) {
                    break;
                }
                System.out.println("An error has occurred! Please try again: ");
            }
        }
        return user;
    }

    //interface to select course
    private void chooseCourse() {
        Scanner in = new Scanner(System.in);
        List<Course> courses = this.view.viewCourses();
        System.out.println("Courses: ");
        System.out.println(courses
                .stream()
                .map(e -> e.getName() + " ID: " + e.getCourseID())
                .collect(Collectors.joining(" "))
        );
        System.out.println("Select a courseID: [" + courses
                .stream()
                .map(e -> Integer.toString(e.getCourseID()))
                .collect(Collectors.joining(", ")) + "]");
        int courseID = Integer.parseInt(in.nextLine());
        this.course = createPost.selectCourse(courseID);
    }

    //Main hub for different actions a student or instructor may choose
    private void selectAction() {
        Scanner in = new Scanner(System.in);
        System.out.println("======== | Welcome to: " + this.course + " | ========");
        while (true) {
            this.printPosts(this.course.getCourseID());

            System.out.println("Do you want create a post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                //create post
                Post newPost = createPost.createPost(this.course.getCourseID(), this.user.getUserID());
                System.out.println("You created a new post: ");
                System.out.println(newPost);
                this.user.increasePostsCreated();

            }
            System.out.println("Do you want to select/reply to a post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                this.printPosts(this.course.getCourseID());
                this.replyToPost();
                this.user.increasePostsViewed();
            }

            if (this.user.isInstructor()) {
                System.out.println("Do you want to view user statistics? (y/n)");
                if (in.nextLine().equalsIgnoreCase("y")) {
                    List<Map<String, Integer[]>> stats = this.stats.getStats();
                    System.out.println(this.stats.printStats(stats));
                }
            }
            System.out.println("Do you want to search for a post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
               this.search();
            }

            System.out.println("Do you want to log out? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Bye bye " + this.user.getName() + "!");
                break;
            }
            //Update user
        }
    }

    private void search() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Search: ");
            String searchInput = in.nextLine();
            System.out.println("You searched for: " + searchInput);
            List<Post> posts = this.view.viewPosts(this.course.getCourseID(), searchInput);
            if (posts.isEmpty()) {
                System.out.println("It appears that there are currently no existing posts with this input!");
                System.out.println("Do you want to try again? (y/n)");
                if (in.nextLine().equalsIgnoreCase("y")) {
                    continue;
                }
                break;
            }
            System.out.println("Found " + posts.size() + " posts!");
            System.out.println(posts
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n")));
            break;

        }
    }


    private void printPosts(int courseID) {
        System.out.println("Current posts:" );
        List<Post> posts = this.view.viewPosts(courseID);
        if (posts.isEmpty()) {
            System.out.println("It appears that the course: " + this.createPost.selectCourse(courseID) + " has no posts yet!");
        } else {
            System.out.println(posts
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n")));
        }
    }

    private void replyToPost() {
        Scanner in = new Scanner(System.in);
        System.out.println("Select a post you want to reply to: ");
        int postID = Integer.parseInt(in.nextLine());
        System.out.println("Your reply: ");
        String content = in.nextLine();

        List<Thread> threads = view.viewThreads(postID);
        Thread thread = null;
        if (threads.isEmpty()) {
            String type = (this.user.isInstructor() ? "Instructor" : "Student") + " answer";
            thread = replyPost.newThread(postID, type);
            System.out.println("You created a new" + type + " !");
        } else {
            System.out.println("You replied to a discussion");
            thread = replyPost.newThread(postID, "Discussion");
        }
        Reply reply = null;
        if (this.user.isInstructor()) {
            reply = replyPost.newReply(thread.getThreadID(), this.user, content, false);
        } else {
            System.out.println("Anonymous? (y/n)");
            reply = replyPost.newReply(thread.getThreadID(), this.user, content, in.nextLine().equalsIgnoreCase("y"));
        }
        System.out.println("Your reply: ");
        System.out.println(reply);
    }

    public void startProgram() {
        System.out.println("-----------Welcome to Piazza-----------");
        if (this.user == null) {
            this.user = this.start();
        }
        System.out.println("Success! Welcome " +  this.user.getName());
        this.chooseCourse();
        this.selectAction();
    }

    public static void main(String[] args) {
        MainController controller = new MainController();
        controller.startProgram();
    }
}
