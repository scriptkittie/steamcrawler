package kindred;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.sql.Connection;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class SteamCrawler
{
	private BigInteger MAX_PROFILE_ID = new BigInteger("76599999999999999");
	private Database database = new Database();
	///threadssss soon
//	public void crawlSteamProfiles(BigInteger profileIDStart) throws Exception
//	{
//		
//		Connection con = database.openConnection();
//		int i = 0;
//		for (BigInteger bi = profileIDStart; bi.compareTo(MAX_PROFILE_ID) <= 0; bi = bi.add(BigInteger.ONE)) {
//			
//			if(i == 100)
//			{
//				con.commit();
//				con.close();
//				con = database.openConnection();
//			}
//			String gamesListUrl = "http://steamcommunity.com/profiles/"  + bi.toString()+ "/games?tab=all&xml=1";
//			String profileInfoUrl = "http://steamcommunity.com/profiles/"  + bi.toString()+ "?xml=1";
//			System.out.println("Reading profile: " + profileInfoUrl);
//			String gamesXML = HttpUtil.httpGet(gamesListUrl, null);
//			String profileXML = HttpUtil.httpGet(profileInfoUrl, null);
//			parseGameXML(profileXML,gamesXML,con);
//			i++;
//		}
//	}		
	
	public void crawlSteamProfiles(BigInteger profileIDStart) throws Exception
	{
	
		BigInteger endLoop = profileIDStart.add(BigInteger.TEN);
		Connection con = database.openConnection();
		int i = 0;
		for (BigInteger bi = profileIDStart; bi.compareTo(endLoop) <= 0; bi = bi.add(BigInteger.ONE)) {
			
			if(i == 10)
			{
				con.commit();
				con.close();
				con = database.openConnection();
				break;
			}
			String gamesListUrl = "http://steamcommunity.com/profiles/"  + bi.toString()+ "/games?tab=all&xml=1";
			String profileInfoUrl = "http://steamcommunity.com/profiles/"  + bi.toString()+ "?xml=1";
			System.out.println("Reading profile: " + profileInfoUrl);
			String gamesXML = HttpUtil.httpGet(gamesListUrl, null);
			String profileXML = HttpUtil.httpGet(profileInfoUrl, null);
			parseGameXML(profileXML,gamesXML,con);
			i++;
		}
	}	
	
	
	
	public void parseGameXML(String profileXML, String gamesXML, Connection con) throws Exception
	{
		String hasPA = "0";
		String hasSpaceEngineers = "0";
		String hasElite = "0";
		String hasHome = "0";
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try
		{
			XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader((new ByteArrayInputStream(gamesXML.getBytes())));
			while(streamReader.hasNext())
			{
				int eventType = streamReader.next();
				if(eventType == XMLStreamReader.START_ELEMENT)
				{
					String tag = streamReader.getLocalName();
					if(tag.equalsIgnoreCase("appID"))
					{
						String text = streamReader.getElementText();
						if(text.equalsIgnoreCase("233250"))
						{
							hasPA = "1";
						}
						else if(text.equalsIgnoreCase("359320"))
						{
							hasElite = "1";
						}
						else if(text.equalsIgnoreCase("244160") || text.equalsIgnoreCase("281610"))
						{
							hasHome = "1";
						}
						else if(text.equalsIgnoreCase("244850"))
						{
							hasSpaceEngineers = "1";
						}
					}
					else if(tag.equalsIgnoreCase("error"))
					{
						System.out.println("Skipping because private");
					}
			       
			    }
			}
		}
		catch(Exception ex)
		{
			System.out.println("Error reading profile");
		}
		
		if(!(hasPA.equals("0") && hasElite.equals("0") && hasHome.equals("0")  && hasSpaceEngineers.equals("0")))
		{
			parseProfile(con, profileXML, hasPA, hasSpaceEngineers, hasElite, hasHome);
		}
	}
	
	
	private void parseProfile(Connection con, String profileXML,String hasPA, String hasSpaceEngineers, String hasElite, String hasHome) throws Exception
	{
		String steamID = "0";
		String steamName = "";
		String lastOnline = "";
		String location = "";
		String summary = "";
		String customURL = "";
		
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try
		{
			XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader((new ByteArrayInputStream(profileXML.getBytes())));
			while(streamReader.hasNext())
			{
				int eventType = streamReader.next();
				if(eventType == XMLStreamReader.START_ELEMENT)
				{
					String tag = streamReader.getLocalName();
					if(tag.equalsIgnoreCase("privacyState"))
					{
						String text = streamReader.getElementText();
						if(text != null && text.equalsIgnoreCase("private"))
						{
							System.out.println("Skipping because private");
							return;
						}
					}
					else if(tag.equalsIgnoreCase("steamID64"))
					{
						steamID = streamReader.getElementText();
					}
					else if(tag.equalsIgnoreCase("steamID"))
					{
						steamName = streamReader.getElementText();
					}
					else if(tag.equalsIgnoreCase("stateMessage"))
					{
						lastOnline = streamReader.getElementText();
					}
					else if(tag.equalsIgnoreCase("location"))
					{
						location = streamReader.getElementText();
					}
					else if(tag.equalsIgnoreCase("customURL"))
					{
						customURL = streamReader.getElementText();
					}
					else if(tag.equalsIgnoreCase("summary"))
					{
						summary = streamReader.getElementText();
					}
			    }
			}
		}
		catch(Exception ex)
		{
			System.out.println("error reading profile");
		}
		
		database.addSteamUser(con, steamID, location, steamName, lastOnline, summary, customURL, hasPA, hasSpaceEngineers, hasElite, hasHome);
			
	}
	
	  public static long convertSteamIdToCommunityId(String steamId)
	            throws Exception {
	        if(steamId.equals("STEAM_ID_LAN") || steamId.equals("BOT")) {
	            throw new Exception("Cannot convert SteamID \"" + steamId + "\" to a community ID.");
	        }
	        if(steamId.matches("^STEAM_[0-1]:[0-1]:[0-9]+$")) {
	            String[] tmpId = steamId.substring(8).split(":");
	            return Long.valueOf(tmpId[0]) + Long.valueOf(tmpId[1]) * 2 + 76561197960265728L;
	        } else if(steamId.matches("^\\[U:[0-1]:[0-9]+\\]+$")) {
	            String[] tmpId = steamId.substring(3, steamId.length() - 1).split(":");
	            return Long.valueOf(tmpId[0]) + Long.valueOf(tmpId[1]) + 76561197960265727L;
	        } else {
	            throw new Exception("SteamID \"" + steamId + "\" doesn't have the correct format.");
	        }
	    }

	
	
}
