package org.google.geo.mapping.ui.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.google.geo.mapping.ui.servlet.GeocoderSession;
import org.google.geo.mapping.ui.session.ISessionListener;

/**
 * : use GeoCoderController instead
 * @author Kees
 *
 */
@Deprecated
public class GeoCoderBrowser extends Browser {
	private static final long serialVersionUID = -7462050265419768312L;

	public static final String S_INDEX_HTML = "/geo/index.html";
	public static final String S_JS_EXECUTED = "jsExecuted";
	
	private Collection<IEvaluationListener<Map<String, String>>> listeners;

	private CommandController controller;

	private GeocoderSession session = GeocoderSession.getInstance();

	private Logger logger = Logger.getLogger( this.getClass().getName());
		
	public GeoCoderBrowser(Composite parent, int style) {
		super(parent, style);
		this.controller = new CommandController(this);
		listeners = new ArrayList<IEvaluationListener<Map<String, String>>>();
		session.init(parent.getDisplay());
		session.start();
		super.setUrl( S_INDEX_HTML);
	}

	public void addSessionListener( ISessionListener<Map<String, String>> listener ){
		this.session.addSessionListener(listener);
	}
	
	public void removeSesionListener( ISessionListener<Map<String, String>> listener ){
		this.session.removeSessionListener(listener);
	}

	public void addEvaluationListener( IEvaluationListener<Map<String, String>> listener ){
		this.listeners.add(listener);
	}
	
	public void removeEvaluationListener( IEvaluationListener<Map<String, String>> listener ){
		this.listeners.remove(listener);
	}

	protected void notifyEvaluation( EvaluationEvent<Map<String, String>> ee ){
		for( IEvaluationListener<Map<String, String>> listener: listeners )
			listener.notifyEvaluation(ee);
	}

    public synchronized void setQuery( String function, String[] params ){
    	controller.setQuery(function, params);
    }

	/**
	 * Set a query. It will be carried out as soon as possible
	 * @param function
	 * @param params
	 */
	public synchronized void setQuery( String function ){
		setQuery( function, new String[0]);
	}

    public synchronized void executeQuery(){
    	controller.executeQuery();
    }

    public synchronized void performQuery( String function, String[] params ){
    	controller.setQuery(function, params);
    	controller.executeQuery();
    }
	
	@Override
	public void dispose() {
		session.dispose();
		super.dispose();
	}

	private class CommandController{

		private GeoCoderBrowser browser;
		private LinkedList<Map.Entry<String, String[]>> commands;
				
		public CommandController( GeoCoderBrowser browser) {
			commands = new LinkedList<Map.Entry<String, String[]>>();
			this.browser = browser;
		}

		/**
		 * Set a query. It will be carried out as soon as possible
		 * @param function
		 * @param params
		 */
		public synchronized void setQuery( String function, String[] params ){
			final String func = function;
			final String[] prms = params;
			this.commands.push( new Map.Entry<String, String[]>() {

				@Override
				public String getKey() {
					return func;
				}

				@Override
				public String[] getValue() {
					return prms;
				}

				@Override
				public String[] setValue(String[] value) {
					return prms;
				}
			});
		}	
		
		protected synchronized void executeQuery(){
			if( commands.isEmpty() )
				return;
			StringBuffer buffer = new StringBuffer();
			while( !commands.isEmpty() ){
				Map.Entry<String, String[]> command = commands.removeLast();
				buffer.append( setFunction(command.getKey(), command.getValue()));
				buffer.append(" ");
			}
			evaluate(buffer.toString());
		}

	    /**
		 * Create the correct string from the function enum
		 * @param function
		 * @param params
		 * @return
		 */
		private String setFunction( String function, String[] params){
			StringBuffer buffer = new StringBuffer();
			buffer.append( function );
			buffer.append("(");
			for( int i=0; i< params.length; i++ ){
				buffer.append( "'" + params[i] + "'" );
				if( i< params.length-1 )
					buffer.append(",");
			}
			buffer.append(");");
			logger.info("EXECUTING: " + buffer.toString() );
			return buffer.toString();
		}
		
		private class JSExecuted extends BrowserFunction{

			public JSExecuted(Browser browser) {
				super(browser, S_JS_EXECUTED);
			}

			@Override
			public Object function(Object[] arguments) {
				// TODO Auto-generated method stub
				return super.function(arguments);
			}
		}
	}
}
