package kindred;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

public class SteamCrawlerTest {
	
	private BigInteger MAX_PROFILE_ID = new BigInteger("76599999999999999");
	private BigInteger MIN_PROFILE_ID = new BigInteger("76500000000000000");
	
	@Test
	public void testCrawl() throws Exception
	{
		SteamCrawler sc = new SteamCrawler();
		BigInteger goatSteam = new BigInteger("76561197995225990");
		sc.crawlSteamProfiles(goatSteam);
	}
	
	@Test
	public void testCrawl1() throws Exception
	{
		SteamCrawler sc = new SteamCrawler();
		BigInteger goatSteam = new BigInteger("76561197995225990");
		System.out.println(sc.convertSteamIdToCommunityId("STEAM_0:1:83843440"));
	}
	
	@Test
	public void testCrawlThreads() throws Exception
	{
		
		BigInteger profileIDStart = MIN_PROFILE_ID;
		
	    ExecutorService service = Executors.newFixedThreadPool(10);
	    
	    for (BigInteger bi = profileIDStart; bi.compareTo(MAX_PROFILE_ID) <= 0; bi = bi.add(BigInteger.TEN)) {
	    	service.submit(future(bi)).get();

		}
	}
	
	public Callable<Object> future(final BigInteger profileStart)
	{
		Callable<Object> callable = new Callable<Object>() {
   		 public Object call() throws Exception {
   			 SteamCrawler s = new SteamCrawler();
   			 s.crawlSteamProfiles(profileStart);
   			 return s;
   		 }
		};
   		 
   		 return callable;
	}
	
	@Test
	public void testDatabase() throws Exception
	{
		Database d = new Database();
		d.openConnection();
	}
}
