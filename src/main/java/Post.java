public class Post {
    private int postID;
    private String type;
    private String summary;
    private String content;
    private boolean allowAnonymous;
    private int likes = 0;

    public Post(String type, String summary, String content, boolean allowAnonymous) {
        this.type = type;
        this.summary = summary;
        this.content = content;
        this.allowAnonymous = allowAnonymous;
    }

    public String getContent() {
        return content;
    }

    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    public String getSummary() {
        return summary;
    }

    public String getType() {
        return type;
    }
}
