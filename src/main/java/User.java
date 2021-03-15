public class User {

    private String name;
    private String email;
    private String password;
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

    public void method() {
        System.out.println("do something");
    }

    public static void main(String[] args) {

    }
}
