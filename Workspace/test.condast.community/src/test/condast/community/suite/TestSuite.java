package test.condast.community.suite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.io.IOUtils;
import org.condast.commons.na.community.CommunityResource;
import org.condast.commons.project.ProjectFolderUtils;
import org.condast.commons.test.core.AbstractTestSuite;
import org.condast.commons.test.core.ITestEvent;
import org.condast.postcode.api.names.CommunityQuery;
import org.xml.sax.Attributes;

import com.google.gson.Gson;

import test.condast.community.core.CommunityParser;

public class TestSuite extends AbstractTestSuite<String, String> {

	public static final String S_BUNDLE_ID = "test.condast.community.suite";

	public enum Tests{
		COMMUNITY_PARSER,
		POSTCODE,
		POSTCODES;
	}
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public TestSuite() {
		super( S_BUNDLE_ID);
	}

	public TestSuite(Attributes attrs) {
		super(S_BUNDLE_ID, attrs);
	}

	@Override
	protected void testSuite() throws Exception {
		Tests test = Tests.POSTCODES;
		switch( test ) {
		case COMMUNITY_PARSER:
			testCommunityParser();
			break;
		case POSTCODE:
			testPostcode();
			break;
		case POSTCODES:
			testPostcodes();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPrepare(ITestEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onPerform(ITestEvent event) {
		// TODO Auto-generated method stub
	}

	public void testPostcodes() {
		try {
			Collection<CommunityResource> results = CommunityQuery.getPostcodes("Overvecht, Utrecht, de Bilt, Tuindorp");
			for( CommunityResource result: results )
				logger.info(result.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void testPostcode() {
		//service = Executors.newCachedThreadPool();
		//service.execute(runnable);
		String userdir = ProjectFolderUtils.getDefaultUserDir("tmp" );
		File file = new File( userdir, CommunityResource.Resources.POSTCODE.toString() );
		if(!file.exists()) {
			logger.info("File not found: " + file.getPath());
			return;
		}
		String[] pc = CommunityParser.getPostcodes(file, "Appingedam");  		
		StringBuilder builder = new StringBuilder();
		for( String str: pc )
			builder.append(str);
		logger.info(builder.toString());
	}

	public void testCommunityParser() {
		try {
			logger.info("PARSING FILES: ");
			CommunityParser parser = CommunityParser.getDefaultParser();
			CommunityParser.ComData[] data = parser.parse();
			Gson gson = new Gson();
			String results = gson.toJson(data, CommunityParser.ComData[].class);
			logger.info("\n\n" + results );
			String userdir = ProjectFolderUtils.getDefaultUserDir("tmp" );
			File file = new File( userdir, CommunityResource.Resources.POSTCODE.toString() );
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter writer = new FileWriter( file );
			try {
				writer.write(results);
			}
			finally {
				IOUtils.close(writer );
			}
			logger.info("COMPLETE: ");
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}		
	}
}
