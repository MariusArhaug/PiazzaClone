

public class User {

    private final String name;
    private final String email;
    private final String password;
    private boolean isInstructor = false;
    private int postsCreated = 0;
    private int postsViewed = 0;
    private int postsLiked = 0;

    public User(String name, String email, String password, boolean isInstructor) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isInstructor = isInstructor;
    }

    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }

    public boolean isInstructor() {
        return this.isInstructor;
    }


    public void method() {
        System.out.println("do something");
    }

    public static void main(String[] args) {

    }
}
