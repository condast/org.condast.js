package org.condast.js.commons.controller;

import org.condast.js.commons.eval.EvaluationEvent;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public class AbstractModel {

	
	private AbstractJavascriptController controller;
	private BrowserFunction function;
		
	public AbstractModel( AbstractJavascriptController controller ) {
		this.controller = controller;
		//this.function = new MarkerClicked( this.controller.getBrowser() );	
	}

	protected Browser getBrowser(){
		return this.controller.getBrowser();
	}

	protected void setQuery( String query, String[] params ){
		this.controller.setQuery( query, params);
	}
	
	protected void notifyEvaluationEvent( EvaluationEvent<Object> event ){
		controller.notifyEvaluation( event );
	}
	
	public void dispose(){
		this.function.dispose();
	}
}