

public class User {

    private int userID;
    private String name;
    private String email;
    private String password;
    private boolean isInstructor = false;
    private int postsCreated = 0;
    private int postsViewed = 0;
    private int postsLiked = 0;

    //User object instanced from Java
    public User(String name, String email, String password, boolean isInstructor) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isInstructor = isInstructor;
    }
    //User object instanced from SQL query
    public User(int userID,String[] strings, boolean isInstructor, int[] postCounts) {
        this.userID = userID;
        this.name = strings[0];
        this.email = strings[1];
        this.password = strings[2];
        this.isInstructor = isInstructor;
        this.postsCreated = postCounts[0];
        this.postsViewed = postCounts[1];
        this.postsLiked = postCounts[2];
    }

    public int getUserID() {
        return userID;
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

    public boolean isInstructor() { return this.isInstructor;}


    public String toString() {
        return "UserID: " + this.userID + ", Name: " + this.name + ", Email: " + this.email;
    }

    public static void main(String[] args) {

    }
}
