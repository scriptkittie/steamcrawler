package kindred;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

	
	public Connection openConnection()
 {

		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:test.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			return c;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return null;

	}
	
	public void addSteamUser(Connection con, String steamID, String location, String steamName, String lastOnline, String summary, String customURL, String hasPA, String hasSpaceEngineers, String hasEliteDangerous,String hasHome) throws SQLException
	{
		PreparedStatement statement = con.prepareStatement("INSERT INTO 'tblSteamInfo'('steamID','location','steamName','lastOnline','summary','customURL','hasPA','hasSpaceEngineers','hasEliteDangerous','hasHomeWorld') VALUES (?,?,?,?,?,?,?,?,?,?)");
	    statement.setString(1,steamID);
	    statement.setString(2,location);
	    statement.setString(3,steamName);
	    statement.setString(4,lastOnline);
	    statement.setString(5,summary);
	    statement.setString(6,customURL);
	    statement.setString(7,hasPA);
	    statement.setString(8,hasSpaceEngineers);
	    statement.setString(9,hasEliteDangerous);
	    statement.setString(10,hasHome);
	    statement.executeUpdate();
	}
}
