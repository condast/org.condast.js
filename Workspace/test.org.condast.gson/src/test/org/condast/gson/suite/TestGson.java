package test.org.condast.gson.suite;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.condast.commons.na.model.IApplicationPerson;
import org.condast.commons.na.questionaire.IQuestion;
import org.condast.commons.na.questionaire.IQuestionaire;

import nl.cultuurinzicht.servlet.controller.QuestionnaireController;
import nl.cultuurinzicht.servlet.parser.QuestionnaireParser;
import nl.eetmee.commons.profile.def.IProfile;
import nl.eetmee.commons.profile.utils.ProfileAttributes;

public class TestGson {

	public static final String S_FILE_LOCATION = "P:/Workspaces/eetMee/eetmee/questionaire_v4.txt";
	public static final String S_FILE_LOCATION_2 = "P:/Workspaces/eetMee/eetmee/file.json";
	public static final String S_FILE_LOCATION_3 = "P:/Workspaces/eetMee/eetmee/questions.json";
	public static final String S_FILE_LOCATION_4 = "P:/Workspaces/eetMee/eetmee/eetmee-0410.json";

	public static final String S_ENTRY_RESET = "?reset=true";

	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	public static void testSuite(){
		TestGson tg = new TestGson();
		try {
			//tg.testBase64( S_FILE_LOCATION_4);
			tg.testGsonFromURL( S_FILE_LOCATION_4);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testGson( String str ) throws Exception{
		IQuestionaire qa = QuestionnaireParser.parse( str );
	}

	private void testBase64( String fileName ) throws Exception{
		File file = new File( fileName ); 
		if(!file.exists() ){
			logger.severe("No file foud: " + fileName );
			throw new NullPointerException();
		}
		InputStream in = file.toURI().toURL().openStream();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Base64OutputStream b64out = new Base64OutputStream( out );
		IOUtils.copy( in, out );
		logger.info( out.toString() );
		logger.info("Length: " + out.size() );
	}
	
	private void testGsonFromURL( String fileName ) throws Exception{
		File file = new File( fileName ); 
		if(!file.exists() ){
			logger.severe("No file foud: " + fileName );
			throw new NullPointerException();
		}
		InputStream in = file.toURI().toURL().openStream();
		IQuestionaire qa = null;
		try{
			QuestionnaireParser parser = new QuestionnaireParser( in );
			qa = parser.parse();
		}
		finally{
			in.close();
		}
		
		logger.info( qa.toString() );

		for( IQuestion question: qa.getQuestions() ){
			logger.info( question.toString() );
		}
		
		IApplicationPerson[] ap = QuestionnaireController.createApplicationPerson(qa );
		IProfile profile = QuestionnaireController.createProfile(ap, qa );
		logger.info( ProfileAttributes.printProfile(profile));
	}
	
	private void testGSonToDevelop( String urlbase ) throws MalformedURLException{
		URL url = new URL(urlbase + S_ENTRY_RESET );
		
	}
	
	protected String doGet( URL url ){
		URLConnection conn;
		BufferedReader rd = null;
		StringBuffer buffer = new StringBuffer(); 
		try {
			conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// Get the response 
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
			String line; 
			while ((line = rd.readLine()) != null) { 
				buffer.append(line); 
			} 
			return rd.toString();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				rd.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 	        	
		}
		return buffer.toString();
	}
}
