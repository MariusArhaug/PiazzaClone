
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

    public static String reformatContent(String content) {
        String[] words = content.split(" ");
        String reformatedContent = "";
        for (String word : words) {
            if ((reformatedContent + word).length() < 30) {
            reformatedContent +=  word + " ";
            } else {
                reformatedContent +=  "\n";
            }
        }
        return reformatedContent;
    }

    public int getPostID() {
        return this.postID;
    }


    public String toString() {
        return  "|===========================|" + "\n" +
                "| Post nr: " + this.postID + "\n" +
                "|===========================|" + "\n" +
                "| Type: " + this.type + "\n" +
                "| Summary: " + this.summary + "\n" +
                "| Details: -----------------|" + "\n" +
                "| " + reformatContent(this.content) + "\n| \n" +
                "|---------------------------|" + "\n" +
                "| Likes: " + this.likes + "\n" +
                "| By: " + (this.isAnonymous ? "Anon" : "User ID: " + this.userID) + "\n" +
                "| ==========================|" + "\n";
    }
}
