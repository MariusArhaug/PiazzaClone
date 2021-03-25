package backend;

import backend.DBConnect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

//Get user statistics from database and print them into to understandable text.
public class GetStatistics extends DBConnect {

    private PreparedStatement regStatement;

    public GetStatistics() {
        super.connect();
    }

    /**
     * Get stats as a List of maps with name and corresponding values, posts created and
     * @return List<Map<String, Integer[]>> Stats
     */
    private List<Map<String, Integer[]>> getStats() {
        try {
            String SQL = "SElECT name, postsCreated, postsViewed " +
                    "FROM users " +
                    "ORDER BY postsViewed DESC";
            this.regStatement = conn.prepareStatement(SQL);

            ResultSet rs = this.regStatement.executeQuery();
            List<Map<String, Integer[]>> listOfDictionary = new ArrayList<>();

            while (rs.next()) {
                String strings = rs.getString("name");
                Integer[] stats = {
                        rs.getInt("postsCreated"),
                        rs.getInt("postsViewed")
                };

                Hashtable<String, Integer[]> map = new Hashtable<>();
                map.put(strings, stats);
                listOfDictionary.add(map);
            }
            return listOfDictionary;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Format stats into a viewable interface.
    public void printStats() {
        List<Map<String, Integer[]>> stats = getStats();
        if (stats == null) {
            System.out.println("It appears that there are no current statistics available yet!");
            return;
        }
        String result = "|=====================User Statistics====================|" + "\n";
        for (Map<String, Integer[]> innerMap : stats) {
            result += "| Name: " + innerMap.keySet().iterator().next();
            for (Integer[] values : innerMap.values()) {
                result += "\n| Posts created: " + values[0];
                result += "\n| Posts viewed: " + values[1];
                result += "\n|--------------------------------------------------------|";
            }
            result += "\n";
        }
        System.out.println(result);
    }
}
