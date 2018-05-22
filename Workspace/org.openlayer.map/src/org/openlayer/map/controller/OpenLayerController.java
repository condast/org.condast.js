package org.openlayer.map.controller;

import java.util.logging.Logger;

import org.condast.js.commons.controller.AbstractJavascriptController;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public class OpenLayerController extends AbstractJavascriptController{

	public static final String S_INDEX_HTML = "/resources/index.html";
	public static final String S_INITIALISTED_ID = "OpenLayerInitialisedId";
	public static final String S_IS_INITIALISTED = "isInitialised";

	
	public static String S_CALLBACK_ID = "CallBackId";
	private static String S_CALLBACK_FUNCTION = "onCallBack";

	private BrowserFunction callback;

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public OpenLayerController( Browser browser ) {
		this( browser, S_INITIALISTED_ID );
	}

	public OpenLayerController( Browser browser, String id ) {
		super( browser, id );
		setBrowser(OpenLayerController.class.getResourceAsStream( S_INDEX_HTML ));
		this.callback = createCallBackFunction( S_CALLBACK_ID, S_CALLBACK_FUNCTION );	
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
}
