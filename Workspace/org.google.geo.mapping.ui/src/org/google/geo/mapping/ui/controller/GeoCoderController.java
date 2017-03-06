package org.google.geo.mapping.ui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.google.geo.mapping.ui.servlet.GeocoderSession;
import org.google.geo.mapping.ui.session.ISessionListener;
import org.google.geo.mapping.ui.view.EvaluationEvent;
import org.google.geo.mapping.ui.view.IEvaluationListener;
import org.google.geo.mapping.ui.view.IEvaluationListener.EvaluationEvents;

public class GeoCoderController{

	public static final String S_INDEX_HTML = "/geo/index.html";
	public static final String S_JS_EXECUTED = "jsExecuted";
	
	private Collection<IEvaluationListener<Map<String, String>>> listeners;

	private CommandController controller;
	private Browser browser;

	private GeocoderSession session = GeocoderSession.getInstance();

	private Logger logger = Logger.getLogger( this.getClass().getName());
	
	private DisposeListener dl = new DisposeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void widgetDisposed(DisposeEvent event) {
			session.dispose();
		}
	};
	
	private Lock lock;
	private BrowserCallback getCallBack(){
		BrowserCallback callback = new BrowserCallback() {

			private static final long serialVersionUID = 1L;

			@Override
			public void evaluationSucceeded(Object result) {
				notifyEvaluation( new EvaluationEvent<Map<String, String>>( browser, EvaluationEvents.SUCCEEDED ));
				logger.info("EXECUTION SUCCEEDED");
			}
			@Override
			public void evaluationFailed(Exception exception) {
				notifyEvaluation( new EvaluationEvent<Map<String, String>>( browser, EvaluationEvents.FAILED ));
				logger.warning("EXECUTION FAILED");
			}
		};
		browser.getDisplay().asyncExec( new Runnable(){

			@Override
			public void run() {
				lock.unlock();			
			}
		});
		return callback;
	}

	public GeoCoderController( Browser browser ) {
		this.controller = new CommandController();
		lock = new ReentrantLock();
		this.browser = browser;
		this.browser.addDisposeListener( dl );
		listeners = new ArrayList<IEvaluationListener<Map<String, String>>>();
		session.init(browser.getDisplay());
		session.start();
		browser.setUrl( S_INDEX_HTML);
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

 	public Object evaluate( final String query ){
  		  browser.evaluate( query, getCallBack() );
 		  return true;
    }
	
	private class CommandController{

		private LinkedList<Map.Entry<String, String[]>> commands;
				
		public CommandController() {
			commands = new LinkedList<Map.Entry<String, String[]>>();
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
			lock.lock();
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
	}
}
