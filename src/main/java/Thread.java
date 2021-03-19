
//Thread class that lets us store different data about a thread.
public class Thread {
    private final int threadID;
    private final boolean isResolved;
    private final String type;
    private final int timesViewed;
    private final int timesCommented;
    private final int postID;

    public Thread(int threadID, boolean isResolved, String type, int timesViewed, int timesCommented, int postID) {
        this.threadID = threadID;
        this.isResolved = isResolved;
        this.type = type;
        this.timesViewed = timesViewed;
        this.timesCommented = timesCommented;
        this.postID = postID;
    }

    public int getThreadID() {
        return threadID;
    }
}
