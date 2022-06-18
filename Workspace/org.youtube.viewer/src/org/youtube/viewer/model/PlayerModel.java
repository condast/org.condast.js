package org.youtube.viewer.model;

import java.util.logging.Logger;

import org.condast.commons.strings.StringStyler;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.youtube.viewer.core.AbstractModel;
import org.youtube.viewer.core.BrowserEvent;
import org.youtube.viewer.core.IBrowserListener.BrowserEvents;
import org.youtube.viewer.view.YouTubeBrowser;

public class PlayerModel extends AbstractModel{

	public enum Functions{
		INITIALISE,
		SET_LOOP,
		GET_CURRENT_TIME,
		GET_DURATION,
		STOP_VIDEO,
		CHANGE_VIDEO;
		
		public String toString(){
			return StringStyler.toMethodString( super.toString());
		}
	}
	
	private double duration;
	private double currentTime;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
		
	public PlayerModel( YouTubeBrowser browser ) {
		super( browser );
		new PlayerDataFunction(browser);
	}

	public void initialise( int width, int height, String videoId, int interval ) {
		String[] params = new String[4];
		params[0] = String.valueOf( width );
		params[1] = String.valueOf( height );
		params[2] = videoId;
		params[3] = String.valueOf( interval );
		String initializeFunction = Functions.INITIALISE.toString();
		getBrowser().setQuery(initializeFunction, params );
	}	

	public double getCurrentTime(){
		return this.currentTime;
	}

	public double getDuration(){
		return this.duration;
	}

	public void setLoop( boolean choice ){
		String[] params = new String[1];
		params[0] = String.valueOf( choice );
		getBrowser().setQuery(Functions.INITIALISE.toString(), params );		
	}

	public void changeVideo( String videoId ){
		String[] params = new String[1];
		params[0] = videoId;
		getBrowser().setQuery(Functions.CHANGE_VIDEO.toString(), params );				
	}

	public void stopVideo(){
		getBrowser().setQuery(Functions.STOP_VIDEO.toString() );				
	}
	
    // Called by JavaScript
    private class PlayerDataFunction extends BrowserFunction {
   
    	//The function as will be called on by javaScript
    	public static final String S_PLAYER_FUNCTION = "getPlayerData";

    	PlayerDataFunction (Browser browser) {
           super (browser, S_PLAYER_FUNCTION);
       }
       
    	public Object function (Object[] arguments) {
    		try{
    			duration = ( arguments[0] == null )?0: ((Double) arguments[0]).doubleValue();
    			currentTime = ( arguments[1] == null )?0:((Double) arguments[1]).doubleValue();
    			if( currentTime >= duration )
    				return null;     			
    			logger.info( S_PLAYER_FUNCTION + ": {" + duration + ", " + currentTime + "}");
    			notifyBrowserChange( new BrowserEvent( this, BrowserEvents.INTERVAL, arguments ));
    		}
    		catch( Exception ex ){
    			ex.printStackTrace();
    		}
    		return true;
    	}
    }
}