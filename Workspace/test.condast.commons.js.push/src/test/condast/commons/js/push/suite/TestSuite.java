package test.condast.commons.js.push.suite;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.condast.commons.authentication.vapid.VapidGenerator;
import org.condast.commons.test.core.AbstractTestSuite;
import org.condast.commons.test.core.ITestEvent;
import org.condast.js.push.core.advice.IAdvice;

import com.google.common.io.BaseEncoding;

import test.condast.commons.js.push.core.Dispatcher;
import test.condast.commons.js.push.servlet.PushOptionsAdviceBuilder;

public class TestSuite extends AbstractTestSuite<Object, Object> {

	public static final String S_TEST_NAME = "TEST";
	public static final String S_PATH = "P:/GitHub/Covaid/Workspace/org.covaid.project/vapid.txt";

	public static final String S_EXAMPLE_PUBLIC_KEY = "BPf36QAqZNNvvnl9kkpTDerXUOt6Nm6P4x9GEvmFVFKgVyCVWy24KUTs6wLQtbV2Ug81utbNnx86_vZzXDyrl88=";
	
	public enum Tests{
		TEST_VAPID,
		TEST_SUBSCRIBE;
	}
	
	private static TestSuite suite = new TestSuite();

	private Dispatcher dispatcher = Dispatcher.getInstance();

	private static Logger logger = Logger.getLogger( TestSuite.class.getName() );
	
	protected TestSuite() {
		super("Test", null );
	}

	public static TestSuite getInstance(){
		return suite;
	}

	public void runTests(  ){
		try {
			testSuite();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	protected void testSuite() throws Exception {
		Tests test = Tests.TEST_VAPID;
		logger.info("\n\n RUN TEST: " + test + "\n");
		try{
			//LatLng position = field.transform( 0, field.getWidth()/2);
			switch( test ){
			case TEST_VAPID:
				testVapid();
				break;
			case TEST_SUBSCRIBE:
				testSubscribe();
				break;
			default:
				break;
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		logger.info("Tests completed");
	}

	public void testClassLoader() throws IOException {
		ClassLoader loader = this.getClass().getClassLoader();
		Enumeration<URL> enm = loader.getResources("test/covaid/core");
		while( enm.hasMoreElements())
			logger.info(enm.nextElement().toExternalForm());
	}
	
	private void testVapid() {
		VapidGenerator generator = new VapidGenerator();
		try {
			logger.info("Generate keys");
			generator.initKeys( S_PATH);
			logger.info("Can decode: " + generator.getPublicKeyBase64() + ": " + BaseEncoding.base64().canDecode( generator.getPublicKeyBase64()));
			logger.info("Can decode: " + S_EXAMPLE_PUBLIC_KEY + ": " + BaseEncoding.base64().canDecode( S_EXAMPLE_PUBLIC_KEY));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void testSubscribe() {
		VapidGenerator generator = new VapidGenerator();
		try {
			generator.initKeys( S_PATH);
			PushOptionsAdviceBuilder builder = new PushOptionsAdviceBuilder();
			IAdvice advice = builder.createAdvice(1, 1, "Gino", IAdvice.AdviceTypes.SUCCESS, "good job", 1);
			byte[] payload = builder.createPayLoad(advice, true);
			dispatcher.sendPushMessage(1, advice);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPrepare(ITestEvent<Object, Object> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPerform(ITestEvent<Object, Object> event) {
		// TODO Auto-generated method stub
		
	}
}