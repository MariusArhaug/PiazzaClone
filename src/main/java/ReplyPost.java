import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//Let users reply to a specific post
public class ReplyPost extends DBConnect {

    private final View view = new View();
    private PreparedStatement regStatement;
    public ReplyPost() {
        super.connect();
    }

    /**
     * Let user create new reply and increase post viewed and posts created.
     * @param threadID ID of the thread the reply belongs to
     * @param user ID of the user who created the reply.
     */
    public void newReply(int threadID, User user) {
        //Only users can select that they want their reply be anonymous
        Scanner in = new Scanner(System.in);
        System.out.println("Your reply: ");
        String content = in.nextLine();
        Reply reply;
        if (user.isInstructor()) {
            reply = this.insertReply(threadID, user, content, false);
        } else {
            System.out.println("Anonymous? (y/n)");
            reply = this.insertReply(threadID, user, content, MainController.yes());
        }
        user.increasePostsViewed();
        user.increasePostsCreated();
        System.out.println("Your reply: ");
        System.out.println(reply);
    }

    /**
     * Reply to a thread and insert into the database
     * @param threadID ID of thread we want to reply to
     * @param user who has made reply
     * @param contents details of the reply
     * @param isAnonymous whether the reply is anonymous or not
     * @return Reply object.
     */
    private Reply insertReply(int threadID, User user, String contents, boolean isAnonymous) {
        try {
            String SQLQuery =
                    "INSERT INTO reply (threadID, contents, isAnonymous, isInstructor, userID)" +
                            "VALUES ((?), (?), (?), (?), (?))";
            this.regStatement = this.conn.prepareStatement(SQLQuery, Statement.RETURN_GENERATED_KEYS);
            this.regStatement.setInt(1, threadID);
            this.regStatement.setString(2, contents);
            this.regStatement.setBoolean(3, isAnonymous);
            this.regStatement.setBoolean(4, user.isInstructor());
            this.regStatement.setInt(5, user.getUserID());
            this.regStatement.executeUpdate();
            ResultSet rs = this.regStatement.getGeneratedKeys();
            int replyID = 0;
            if (rs.next()) {
                replyID = Math.toIntExact(rs.getLong(1));
            }
            return new Reply(replyID, threadID,  contents, isAnonymous, user.isInstructor(), user.getUserID());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create and insert new thread into database
     * @param postID ID of the post the thread belongs to
     * @param type The type of thread that's been made; Instructor/Student/Discussion
     * @return Thread object.
     */
    public Thread newThread(int postID, String type) {
        try {
            String SQLQuery =
                    "INSERT INTO threads (type, postID)" +
                    "VALUES ((?), (?))";
            this.regStatement = this.conn.prepareStatement(SQLQuery, Statement.RETURN_GENERATED_KEYS);
            this.regStatement.setString(1, type);
            this.regStatement.setInt(2, postID);
            this.regStatement.executeUpdate();

            ResultSet rs = regStatement.getGeneratedKeys();
            if (rs.next())  {
                return new Thread(Math.toIntExact(rs.getLong(1)), false, type, 0, 0, postID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * Select thread we want to reply to.
     * @param postID ID of post we want to find thread for
     * @param isInstructor boolean if user is instructor or not.
     * @return thread
     */
    public Thread selectThread(int postID, boolean isInstructor) {
        Scanner in = new Scanner(System.in);
        List<Thread> threads = this.view.viewThreads(postID);

        //If we have no threads, the reply becomes a Instructor/Student answer
        //Here would also color the post if we used a GUI and not a TUI
        if (threads.isEmpty()) {
            String type = (isInstructor ? "Instructor" : "Student") + " answer";
            System.out.println("You created a new " + type + "!");
            return this.newThread(postID, type);
        }

        this.printThreads(threads);
        System.out.println("""
                    Select a discussion nr you want to reply to.
                    Press 0 to start a new discussion
                    Press -1 If you don't want to reply""");
        while (true) {
            int threadID = Integer.parseInt(in.nextLine());

            if (threads.stream().anyMatch(e -> e.getThreadID() == threadID )) {
                return threads
                        .stream()
                        .filter(e -> e.getThreadID() == threadID)
                        .collect(Collectors.toList())
                        .get(0);
            }
            if (threadID == 0) {
                System.out.println("You started a new discussion");
                return this.newThread(postID, "Discussion");

            }
            if (threadID == -1) {
                System.out.println("No reply created!");
                return null;
            }
            System.out.println("You must select a valid thread nr! ");
        }
    }

    /**
     * Print out threads and their corresponding trail of replies.
     * @param threads list of Thread objects.
     */
    private void printThreads(List<Thread> threads) {
        System.out.println("Threads: " + threads
                .stream()
                .filter(e -> e.getType().equals("Discussion"))
                .map(e -> e.toString() + this.view.viewRepliesInThread(e.getThreadID())
                        .stream()
                        .map(a ->  a
                                .toString()
                                .replaceAll("(?m)^", "\t"))
                        .collect(Collectors.joining("\n"))
                )
                .collect(Collectors.joining("\n")) + "\n" );
    }
}
