package org.youtube.viewer.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.youtube.viewer.core.IBrowserListener.BrowserEvents;
import org.youtube.viewer.view.EvaluationEvent;
import org.youtube.viewer.view.YouTubeBrowser;
import org.youtube.viewer.view.IEvaluationListener;

public abstract class AbstractModel {
	
	private YouTubeBrowser browser;
	private Collection<IBrowserListener> listeners;
	
	private IEvaluationListener<Map<String,String>> listener = new IEvaluationListener<Map<String, String>>(){

		@Override
		public void notifyEvaluation(EvaluationEvent<Map<String,String>> event) {
		}
	};
	
	
	protected AbstractModel( YouTubeBrowser browser ) {
		this.browser = browser;
		this.listeners = new ArrayList<IBrowserListener>();
		this.browser.addEvaluationListener( listener );
		new BrowserChangeFunction(browser );
	}

	protected YouTubeBrowser getBrowser() {
		return browser;
	}

	public void addBrowserListener( IBrowserListener listener ){
		this.listeners.add( listener );
	}
	

	public void removeBrowserListener( IBrowserListener listener ){
		this.listeners.remove( listener );
	}
	
	protected void notifyBrowserChange( BrowserEvent event ){
		for( IBrowserListener listener: listeners)
			listener.notifyBrowserEvent(event);
	}

	public Object synchronize(){
		return browser.executeQuery();
	}

    // Called by JavaScript
    private class BrowserChangeFunction extends BrowserFunction {
   
    	//The function as will be called on by javaScript
    	public static final String S_BROWSER_FUNCTION = "getBrowserFunction";
    	
    	BrowserChangeFunction (Browser browser) {
           super (browser, S_BROWSER_FUNCTION);
       }
       
    	public Object function (Object[] arguments) {
    		try{
    			notifyBrowserChange( new BrowserEvent( this, BrowserEvents.UNKNOWN, arguments ));
    		}
    		catch( Exception ex ){
    			ex.printStackTrace();
    		}
    		return true;
    	}
    }
}