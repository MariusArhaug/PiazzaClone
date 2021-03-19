import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

//Let users reply to a specific post
public class ReplyPost extends DBConnect {

    private PreparedStatement regStatement;
    public ReplyPost() {
        super.connect();
    }

    //Reply to a post with a specific threadID.
    public Reply newReply(int threadID, User user, String contents, boolean isAnonymous) {
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

    //Create a new thread for replies.
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
}
