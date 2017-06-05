package org.openlayer.map.controller;

import java.util.logging.Logger;

import org.condast.js.commons.controller.AbstractJavascriptController;
import org.eclipse.swt.browser.Browser;

public class OpenLayerController extends AbstractJavascriptController{

	public static final String S_INDEX_HTML = "/openlayer/index.html";
	public static final String S_INITIALISTED_ID = "OpenLayerInitialisedId";
	public static final String S_IS_INITIALISTED = "isInitialised";
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public OpenLayerController( Browser browser ) {
		super( browser, S_INITIALISTED_ID, S_INDEX_HTML );
	}

	@Override
	protected void onLoadCompleted() {
		logger.info("COMPLETED");
	}

	@Override
	public Browser getBrowser() {
		return super.getBrowser();
	}
}
