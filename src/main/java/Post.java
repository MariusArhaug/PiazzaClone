
//Post class that lets us store different data about a post.
public class Post {

    private final int postID;
    private final String type;
    private final String summary;
    private final String content;
    private final boolean isAnonymous;
    private final int courseID;
    private final int userID;
    private int likes = 0;


    public Post(int postID, String type, String summary, String content, int likes, boolean isAnonymous, int courseID, int userID) {
        this.type = type;
        this.summary = summary;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.courseID = courseID;
        this.userID = userID;
        this.postID = postID;
        this.likes = likes;

    }

    /**
     * Format string so that it fits inside the toString method of a post
     * @param content string to format
     * @return formatted string
     */
    public static String reformatContent(String content) {
        StringBuilder sb = new StringBuilder(content);
        int i = 0;
        while (i + 30 < sb.length() && (i = sb.lastIndexOf(" ", i+20)) != -1) {
            sb.replace(i, i+1, "\n| ");
        }
        return sb.toString();
    }

    public int getPostID() {
        return this.postID;
    }


    public String toString() {
        return  "|===========================|" + "\n" +
                "| Post nr: " + this.postID + "\n" +
                "|===========================|" + "\n" +
                "| Type: " + this.type + "\n" +
                "| Summary: -----------------|" + "\n" +
                "| " + reformatContent(this.summary) + "\n| \n" +
                "| Details: -----------------|" + "\n" +
                "| " + reformatContent(this.content) + "\n| \n" +
                "|---------------------------|" + "\n" +
                "| Likes: " + this.likes + "\n" +
                "| By: " + (this.isAnonymous ? "Anon" : "User ID: " + this.userID) + "\n" +
                "| ==========================|" + "\n";
    }
}
