
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


public class GetStatistics extends DBConnect {

    private PreparedStatement regStatement;

    public GetStatistics() {
        super.connect();
    }

    public List<Map<String, Integer[]>> getStats() {
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

                Map<String, Integer[]> map = new Hashtable();
                map.put(strings, stats);
                listOfDictionary.add(map);
            }
            return listOfDictionary;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void printStats(List<Map<String, Integer[]>> stats) {
        if (stats == null) {
            System.out.println("It appears that there are no current statistics available yet!");
        } else {
            String result = "========================";
            for (Map<String, Integer[]> innerMap : stats) {
                result += "Name: " + innerMap.keySet().iterator().next();
                for (Integer[] values : innerMap.values()) {
                    result += " Posts created: " + values[0];
                    result += " Posts viewed: " + values[1];
                }
                result += "========================";
                result += "\n";
            }
            System.out.println(result);
        }
    }
}
