package org.openlayer.map.controller;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.js.commons.controller.AbstractJavascriptController;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public class OpenLayerController extends AbstractJavascriptController{

	public static final String S_INDEX_HTML = "/resources/index.html";
	public static final String S_INITIALISTED_ID = "OpenLayerInitialisedId";
	public static final String S_IS_INITIALISTED = "isInitialised";

	
	public static String S_CALLBACK_ID = "CallBackId";
	private static String S_CALLBACK_FUNCTION = "onCallBack";

	public static String S_TIMER_ID = "Timer";
	private static String S_TIMER_FUNCTION = "onTimer";

	private BrowserFunction callback;
	//private BrowserFunction timer;
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public OpenLayerController( Browser browser ) {
		this( browser, S_INITIALISTED_ID, null );
	}

	public OpenLayerController( Browser browser, LatLng location, int zoom ) {
		this( browser, S_INITIALISTED_ID, location, zoom );
	}

	public OpenLayerController( Browser browser, String[] scripts ) {
		this( browser, S_INITIALISTED_ID, scripts );
	}
	
	public OpenLayerController( Browser browser, String id, String[] scripts ) {
		super( browser, id );
		if( !Utils.assertNull(scripts)) {
			for( String script: scripts )
				super.addScript(script);
		}
		setBrowser(OpenLayerController.class.getResourceAsStream( S_INDEX_HTML ));
		this.callback = createCallBackFunction( S_CALLBACK_ID, S_CALLBACK_FUNCTION );	
		//this.timer = createCallBackFunction( S_TIMER_ID, S_TIMER_FUNCTION );	
	}
	
	public OpenLayerController( Browser browser, String id, LatLng location, int zoom ) {
		super( browser, id );
		Scanner scanner = new Scanner( OpenLayerController.class.getResourceAsStream( S_INDEX_HTML ));
		StringBuilder builder = new StringBuilder();
		while( scanner.hasNext()) {
			String line = scanner.nextLine();
			if( line.trim().startsWith("setLocation"))
				line = "setLocation( " + location.getLatitude() + "," + location.getLongitude() + "," + zoom + ");";
			builder.append(line);
		}
		browser.setText( builder.toString());
		this.callback = createCallBackFunction( S_CALLBACK_ID, S_CALLBACK_FUNCTION );	
		//this.timer = createCallBackFunction( S_TIMER_ID, S_TIMER_FUNCTION );	
	}

	@Override
	protected void onLoadCompleted() {
		logger.info("COMPLETED");
	}

	@Override
	public Browser getBrowser() {
		return super.getBrowser();
	}
	
	public void dispose(){
		this.callback.dispose();
	}
	
	public static String[] createScript( InputStream input ) {
		StringBuilder builder = new StringBuilder();
		Scanner scanner = new Scanner( input );
		try {
			while( scanner.hasNextLine()) {
				String str = scanner.nextLine();
				if( str.trim().startsWith("#") || str.trim().startsWith("//"))
					continue;
				builder.append(str);
				builder.append("\n");
			}
		}
		finally {
			scanner.close();
		}
		String[] result = new String[1];
		result[0] = builder.toString();
		return result;
	}

}
