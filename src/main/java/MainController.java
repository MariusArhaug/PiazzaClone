import java.util.HashMap;
import java.util.Scanner;

public class MainController {

    private final DBConnect connection;
    private User user;
    private Login login = new Login();
    private Register register = new Register();
    private CreatePost createPost = new CreatePost();
    private Course course = null;

    public MainController() {
        this.connection = new DBConnect();
        this.connection.connect();
    }
    private User start() {
        User user = this.login.loginUser();
        if (user == null) {
            System.out.println("This account does not exist yet! Please register");
            while (true) {
                user = this.register.registerUser();
                boolean isRegistered = register.insertUser(user);
                if (isRegistered) {
                    break;
                }
                System.out.println("An error has occurred! Please try again: ");
            }
        }
        return user;
    }

    private void chooseCourse() {
        Scanner in = new Scanner(System.in);
        HashMap<Integer, String> courses = this.createPost.viewCourses();
        System.out.println("Courses: ");
        System.out.println(courses.values().toString());
        System.out.println("Select a course: " + courses.keySet());
        String courseID = in.nextLine();
        this.course = createPost.selectCourse(courseID);
    }

    private void selectAction() {
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to: " + this.course);
        while (true) {
            System.out.println("Current posts:" );


            System.out.println("Do you want to post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                //create post
                createPost.newPost(this.course.getCourseID(), this.user.getUserID());
                continue;
            }
            System.out.println("Do you want to comment on a post? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                //find post then comment
                continue;

            }
            System.out.println("Do you want to log out? (y/n)");
            if (in.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Bye bye " + this.user.getName() + "!");
                break;
            }
        }
    }

    public void startProgram() {
        System.out.println("--------Welcome to Piazza--------");
        if (this.user == null) {
            this.user = this.start();
        }
        System.out.println("Success! You are logged in. ");
        this.chooseCourse();
        this.selectAction();

    }


    public static void main(String[] args) {
        MainController controller = new MainController();
        controller.startProgram();
    }
}
