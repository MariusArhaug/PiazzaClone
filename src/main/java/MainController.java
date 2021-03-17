import java.util.HashMap;
import java.util.Scanner;

public class MainController {

    private final DBConnect connection;
    private User user;
    private Login login = new Login();
    private Register register = new Register();
    private CreatePost createPost = new CreatePost();

    public MainController() {
        this.connection = new DBConnect();
        this.connection.connect();
    }
    private User init() {
        Scanner in = new Scanner(System.in);
        System.out.println("It appears that you're not logged in, please log in.");
        System.out.println("----------Login--------");
        System.out.println("Email: ");
        String email = in.nextLine();
        System.out.println("Password: ");
        String password = in.nextLine();
        User user = login.getUser(email, password);
        if (user == null) {
            System.out.println("This account does not exist yet! Please register");
            while (true) {
                System.out.println("Name: ");
                String name = in.nextLine();
                System.out.println("Email: ");
                email = in.nextLine();
                System.out.println("Password: ");
                password = in.nextLine();
                System.out.println("Instructor: (y/n)");
                boolean isInstructor = in.nextLine().equalsIgnoreCase("y");
                user = new User(name, email, password, isInstructor);
                boolean isRegistered = register.registerUser(user);
                if (isRegistered) {
                    break;
                }
                System.out.println("An error has occurred! Try again: ");
            }
        }
        return user;
    }

    private void chooseCourse() {
        Scanner in = new Scanner(System.in);
        HashMap<Integer, String> courses = this.createPost.viewCourses();
        System.out.println("Courses: ");
        System.out.println(courses.values().toString());
        System.out.println("Select a course: " + courses.keySet().toString());
        String courseID = in.nextLine();
        Course course = createPost.selectCourse(courseID);
        System.out.println("Welcome to: " + course);
        while (true) {
            System.out.println("Do you want to post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                //create post
            }
            System.out.println("Do you want to comment on a post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                //find post then comment.
            }
            System.out.println("Do you want to log out? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                break;
            }
        }
    }

    public void startProgram() {
        System.out.println("--------Welcome to Piazza--------");
        if (this.user == null) {
            this.user = this.init();
        }
        this.chooseCourse();

    }


    public static void main(String[] args) {
        MainController controller = new MainController();
        controller.startProgram();
    }
}
