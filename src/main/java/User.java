

public class User {

    private final int userID;
    private final String name;
    private final String email;
    private final String password;
    private final boolean isInstructor;
    private int postsCreated = 0;
    private int postsViewed = 0;
    private int postsLiked = 0;
    private boolean hasUpdated = false;

    //User object instanced from Java
    public User(int userID, String name, String email, String password, boolean isInstructor) {
        this.userID = userID;
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

    public boolean isInstructor() { return this.isInstructor;}


    public String toString() {
        return "| UserID: " + this.userID + "| Name: " + this.name + " | Email: " + this.email + " |";
    }

    public void increasePostsViewed() {
        this.postsViewed++;
        this.hasUpdated = true;
    }

    public void increasePostsCreated() {
        this.postsCreated++;
        this.hasUpdated = true;
    }

    public int getPostsCreated() {
        return postsCreated;

    }

    public int getPostsViewed() {
        return postsViewed;

    }

    public int getPostsLiked() {
        return postsLiked;
    }

    public boolean hasUpdated() {
        return hasUpdated;
    }
}
