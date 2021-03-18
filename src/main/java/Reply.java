public class Reply {

    private final int replyID;
    private final int threadID;
    private final String contents;
    private final boolean isAnonymous;
    private final boolean isInstructor;
    private final int userID;

    public Reply(int replyID, int threadID, String contents, boolean isAnonymous, boolean isInstructor, int userID) {
        this.replyID = replyID;
        this.threadID = threadID;
        this.contents = contents;
        this.isAnonymous = isAnonymous;
        this.isInstructor = isInstructor;
        this.userID = userID;
    }

    public String toString() {
        return  "=======================" + "\n" +
                "Reply nr: " + this.replyID + "\n" +
                "======================" + "\n" +
                "----Content----" + "\n" +
                this.contents + "\n" +
                "By: " + (this.isAnonymous ? this.userID : "Anon") + "\n" +
                "================================" + "\n";
    }
}
