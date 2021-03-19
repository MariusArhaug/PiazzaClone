import java.util.List;
import java.util.Map;
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
        System.out.println("-----------Welcome to Piazza-----------");
        if (this.user == null) {
            this.user = this.loginUser();
        }
        System.out.println("Success! Welcome " +  this.user.getName() + "!");
        this.chooseCourse();
        this.selectAction();
    }

    //Interface to log in user.
    private User loginUser() {
        User user = this.login.loginUser();
        //If we don't find user in db we get user = null
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
        System.out.println("All current courses: ");
        System.out.println(courses
                .stream()
                .map(e -> e.getName() + " ID: " + e.getCourseID())
                .collect(Collectors.joining(" "))
        );
        //See a users courses that he is registered for.
        List<Course> registeredCourses = this.view.viewRegisteredCourses(this.user.getUserID());
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

    //Main hub for different actions a student or instructor may choose
    private void selectAction() {
        Scanner in = new Scanner(System.in);
        System.out.println("======== | Welcome to: " + this.course + " | ========");
        //Keep user inside of "Piazza" until he/she wants to log out.
        while (true) {
            this.printPosts(this.course.getCourseID());

            //=====Create post=====//
            System.out.println("Do you want create a post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                Post newPost = createPost.createPost(this.course.getCourseID(), this.user.getUserID(), this.course.allowAnonymous());
                System.out.println("You created a new post: ");
                System.out.println(newPost);
                this.user.increasePostsCreated();
            }
            //=====Reply to post=====//
            System.out.println("Do you want to select/reply to a post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                this.replyToPost();
                this.user.increasePostsViewed();
            }

            //=====View stats / Create folder / invite users=====//
            if (this.user.isInstructor()) {
                System.out.println("Do you want to view user statistics? (y/n)");
                if (in.nextLine().equalsIgnoreCase("y")) {
                    List<Map<String, Integer[]>> stats = this.stats.getStats();
                    this.stats.printStats(stats);
                }

                System.out.println("Do you want to create folders for this course? (y/n)");
                if (in.nextLine().equalsIgnoreCase("y")) {
                    Folder newFolder = this.createPost.createFolder(this.course.getCourseID());
                    System.out.println("======================================");
                    System.out.println("| New folder created! " + newFolder + " |");
                    System.out.println("======================================");
                }

                System.out.println("Do you want to invite students to this course? (y/n)");
                if (in.nextLine().equalsIgnoreCase(("y"))) {
                    this.register.registerUserToCourse(this.course.getCourseID());
                }
            }

            //=====Search for posts=====//
            System.out.println("Do you want to search for a post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
               this.search();
            }

            //=====Log out=====//
            System.out.println("Do you want to log out? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                //If we have updated a state in the current user object we need to update the database
                if (this.user.hasUpdated()) {
                    this.login.updateUser(this.user);
                    System.out.println("New stats saved!");
                }
                System.out.println("Bye bye " + this.user.getName() + "!");
                break;
            }
        }
    }

    //Start search input, user fills in what he wants to search for
    //We try to find a post that either has a summary or content that includes the search input
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
            System.out.println(posts
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n")));
        }
    }

    //Reply to post
    private void replyToPost() {
        this.printPosts(this.course.getCourseID());
        Scanner in = new Scanner(System.in);
        System.out.println("Select a post you want to reply to: ");
        int postID = Integer.parseInt(in.nextLine());
        System.out.println("Your reply: ");
        String content = in.nextLine();

        //Find the threads related to the post we want to reply to.
        List<Thread> threads = view.viewThreads(postID);
        Thread thread;

        //If we have no threads, the reply becomes a Instructor/Student answer
        //Here would also color the post if we used a GUI and not a TUI
        if (threads.isEmpty()) {
            String type = (this.user.isInstructor() ? "Instructor" : "Student") + " answer";
            thread = replyPost.newThread(postID, type);
            System.out.println("You created a new" + type + " !");
        } else {
            System.out.println("You replied to a discussion");
            thread = replyPost.newThread(postID, "Discussion");
        }
        //Only users can select that they want their reply be anonymous
        Reply reply;
        if (this.user.isInstructor()) {
            reply = replyPost.newReply(thread.getThreadID(), this.user, content, false);
        } else {
            System.out.println("Anonymous? (y/n)");
            reply = replyPost.newReply(thread.getThreadID(), this.user, content, in.nextLine().equalsIgnoreCase("y"));
        }
        System.out.println("Your reply: ");
        System.out.println(reply);
    }

    public static void main(String[] args) {
        MainController controller = new MainController();
        controller.startProgram();
    }
}
