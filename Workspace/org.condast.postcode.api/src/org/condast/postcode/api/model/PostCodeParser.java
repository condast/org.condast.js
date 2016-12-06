package org.condast.postcode.api.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.condast.commons.Utils;
import org.condast.commons.strings.PostCodeUtils;

import com.google.gson.Gson;

public class PostCodeParser {

	private static String S_POSTCODE_API = "https://api.postcode.nl/rest/addresses/"; 
	private static final String S_API_KEY = "VrZFl8NHpPHQzrrs7xyJPQJdeIbu0tTtnadQnQZFdRs";
	private static final String S_SECRET_KEY = "thkOpxxeL19eb973IqseGpOwVefPxmQFq2ds43Dpo08znTJNbr";

	@SuppressWarnings("unchecked")
	public static Map<String, String> findAddresses( String postcode, int number, String numberExtension ) throws ClientProtocolException, IOException{
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials( AuthScope.ANY, new UsernamePasswordCredentials( S_API_KEY, S_SECRET_KEY ));
		
		CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
		
		String completedUri = S_POSTCODE_API + PostCodeUtils.toStyledPostcode(postcode) + "/" + number;
		if( !Utils.assertNull(numberExtension))
			completedUri += "/" + numberExtension.replace(" ", "" );
		HttpGet httpget = new HttpGet( completedUri );
		CloseableHttpResponse response = httpclient.execute( httpget );
		Map<String, String> obj = null;
		try {
		    HttpEntity entity1 = response.getEntity();
		    Gson gson = new Gson();
            BufferedReader reader = new BufferedReader(new InputStreamReader( entity1.getContent()));
            obj = gson.fromJson(reader, HashMap.class );
		}
		catch( Exception ex ){
			ex.printStackTrace();
		} finally {
		    response.close();
		    System.out.println( obj.toString() );
		}
		return obj;
	}
	

}
