package org.google.geo.mapping.ui.controller;

import java.util.logging.Logger;

import org.condast.js.commons.controller.AbstractJavascriptController;
import org.condast.js.commons.eval.EvaluationEvent;
import org.eclipse.swt.browser.Browser;

public class GeoCoderController extends AbstractJavascriptController{

	public static final String S_INDEX_HTML = "/resources/index.html";
	public static final String S_INITIALISTED_ID = "MapInitialisedId";
	public static final String S_IS_INITIALISTED = "isInitialised";
	
	private Logger logger = Logger.getLogger( this.getClass().getName());
	
	public GeoCoderController( Browser browser ) {
		super( browser, S_INITIALISTED_ID, GeoCoderController.class.getResourceAsStream( S_INDEX_HTML ));
	}

	@Override
	protected void onLoadCompleted() {
		logger.info("complete");	
	}

	
	@Override
	public Browser getBrowser() {
		return super.getBrowser();
	}

	@Override
	public void notifyEvaluation( EvaluationEvent<Object[]> ee) {
		super.notifyEvaluation(ee);
	}
}
