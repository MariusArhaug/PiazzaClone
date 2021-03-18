public class Post {
    private int postID;
    private String type;
    private String summary;
    private String content;
    private boolean allowAnonymous;
    private int courseID;
    private int userID;
    private int likes = 0;

    public Post(String type, String summary, String content, boolean allowAnonymous, int courseID, int userID) {
        this.type = type;
        this.summary = summary;
        this.content = content;
        this.allowAnonymous = allowAnonymous;
        this.courseID = courseID;
        this.userID = userID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public int getPostID() {
        return postID;
    }

    public String getContent() {
        return this.content;
    }

    public int getCourseID() {
        return this.courseID;
    }

    public int getUserID() {
        return this.userID;
    }

    public boolean isAllowAnonymous() {
        return this.allowAnonymous;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getType() {
        return this.type;
    }
}
