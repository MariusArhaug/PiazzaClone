import java.util.Scanner;

public class MainController {

    private final DBConnect connection;
    private User user;
    private Login login = new Login();
    private Register register = new Register();

    public MainController() {
        this.connection = new DBConnect();
        this.connection.connect();
    }
    private void init() {
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
            }
        }
        this.user = user;
        System.out.println("View courses: ");
    }

    public void startProgram() {
        System.out.println("--------Welcome to Piazza--------");
        if (this.user == null) {
            this.init();
        }
    }

    public static void main(String[] args) {
        MainController controller = new MainController();
        controller.startProgram();
    }
}
