package kindred;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpUtil 
{
	public static String httpGet (String host, Map<String,String> parameters) throws Exception
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(host);	
		URIBuilder builder = new URIBuilder(httpGet.getURI());
		if(parameters != null && !parameters.isEmpty())
		{
			for (Map.Entry<String, String> entry : parameters.entrySet())
			{
			    String key = entry.getKey();
			    String value = entry.getValue();
			    builder.addParameter(key, value);
			}
		}
		((HttpRequestBase) httpGet).setURI(builder.build());
		
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
		String inputLine;
	    StringBuffer response = new StringBuffer();
	    
	    while ((inputLine = reader.readLine()) != null)
        {
            response.append(inputLine);
        }
	    
        reader.close();
        httpClient.close();  
        
        return response.toString();
	}
}
