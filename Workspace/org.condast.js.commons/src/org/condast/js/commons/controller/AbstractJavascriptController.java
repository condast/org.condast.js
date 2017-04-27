package org.condast.js.commons.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.eval.IEvaluationListener.EvaluationEvents;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;

public abstract class AbstractJavascriptController implements IJavascriptController{

	public static final String S_IS_INITIALISTED = "isInitialised";
	
	private static final int DEFAULT_INIT_DELAY = 1500;
	
	public enum LoadTypes{
		URL,
		TEXT;
	}
	
	private Collection<IEvaluationListener<Object[]>> listeners;

	private CommandController controller;
	private Browser browser;
	private boolean initialised;
	private String id;

	private Logger logger = Logger.getLogger( this.getClass().getName());
	
	private BrowserCallback getCallBack(){
		BrowserCallback callback = new BrowserCallback() {

			private static final long serialVersionUID = 1L;

			@Override
			public void evaluationSucceeded(Object result) {
				notifyEvaluation( new EvaluationEvent<Object[]>( browser, id, EvaluationEvents.SUCCEEDED ));
				logger.info("EXECUTION SUCCEEDED");
			}
			@Override
			public void evaluationFailed(Exception exception) {
				notifyEvaluation( new EvaluationEvent<Object[]>( browser, id, EvaluationEvents.FAILED ));
				logger.warning("EXECUTION FAILED");
				exception.printStackTrace();
			}
		};
		return callback;
	}

	protected AbstractJavascriptController( Browser browser, String id ) {
		this.id = id;
		this.initialised = false;
		this.browser = browser;
		listeners = new ArrayList<IEvaluationListener<Object[]>>();
		this.controller = new CommandController( );
		browser.addProgressListener( new ProgressListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void completed(ProgressEvent event) {
				onLoadCompleted();
				getBrowser().getDisplay().asyncExec( new Runnable(){

					@Override
					public void run() {
						getBrowser().evaluate("var init=true");
						logger.info( getBrowser().getUrl());
					}
					
				});
			}
			
			@Override
			public void changed(ProgressEvent event) {
				onLoadChanged();
			}
		});
	}

	protected AbstractJavascriptController( Browser browser, String id, String url ) {
		this( browser, id, LoadTypes.URL, url );
	}
	
	protected AbstractJavascriptController( Browser browser, String id, LoadTypes type, String url ) {
		this( browser, id );
		setBrowser( type, url );
	}

	protected AbstractJavascriptController( Browser browser, String id, InputStream in ) {
		this( browser, id );
		setBrowser( in );
	}

	protected abstract void onLoadCompleted();

	protected void onLoadChanged(){ /* DEFAULT NOTHING */ }

	/**
	 * Initialise the composite
	 */
	protected void initComposite(){
		controller.performInit( DEFAULT_INIT_DELAY);		
	}

	/**
	 * Initialise the composite
	 */
	protected void initComposite( int delay ){
		controller.performInit( delay );		
	}

	/* (non-Javadoc)
	 * @see org.condast.js.commons.controller.IJavascriptController#isInitialised()
	 */
	@Override
	public boolean isInitialised() {
		return initialised;
	}

	protected void setBrowser( LoadTypes type, String content ){
		if( LoadTypes.URL.equals( type ))
			browser.setUrl( content );
		else{
			browser.setText( content );
		}
	}
	
	protected void setBrowser( final InputStream in ){
		browser.setText( readInput(in));	
	}
	
	protected Browser getBrowser(){
		return browser;
	}
	
	/* (non-Javadoc)
	 * @see org.condast.js.commons.controller.IJavascriptController#addEvaluationListener(org.condast.js.commons.eval.IEvaluationListener)
	 */
	@Override
	public void addEvaluationListener( IEvaluationListener<Object[]> listener ){
		this.listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.condast.js.commons.controller.IJavascriptController#removeEvaluationListener(org.condast.js.commons.eval.IEvaluationListener)
	 */
	@Override
	public void removeEvaluationListener( IEvaluationListener<Object[]> listener ){
		this.listeners.remove(listener);
	}

	public void notifyEvaluation( EvaluationEvent<Object[]> ee ){
		for( IEvaluationListener<Object[]> listener: listeners )
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

	/* (non-Javadoc)
	 * @see org.condast.js.commons.controller.IJavascriptController#evaluate(java.lang.String)
	 */
	@Override
	public Object evaluate( final String query ){
		browser.evaluate( query, getCallBack() );
		return true;
	}
	
	private String readInput( InputStream in ){
		StringBuffer buffer = new StringBuffer();
		Scanner scanner = new Scanner( in );
		try{
		while( scanner.hasNextLine() )
			buffer.append( scanner.nextLine() );
		}
		finally{
			scanner.close();
		}
		return buffer.toString();
		
	}

	private class CommandController{

		private LinkedList<Map.Entry<String, String[]>> commands;
	
		private ScheduledExecutorService scheduler; 
		private Runnable runnable = new Runnable(){

			@Override
			public void run() {
				browser.getDisplay().asyncExec( new Runnable(){

					@Override
					public void run() {
						try{
							executeQuery();
							initialised = true;
						}
						catch( Exception ex ){
							ex.printStackTrace();
						}
					}					
				});
			}	
		};

		private CommandController() {
			commands = new LinkedList<Map.Entry<String, String[]>>();
			scheduler = Executors.newScheduledThreadPool(1);
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
		
		private void start( int delay){
			scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);
		}
		
		private final synchronized void executeQuery(){
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
			if( !Utils.assertNull(params)){
				for( int i=0; i< params.length; i++ ){
					buffer.append( "'" + params[i] + "'" );
					if( i< params.length-1 )
						buffer.append(",");
				}
			}
			buffer.append(");");
			logger.info("EXECUTING: " + buffer.toString() );
			return buffer.toString();
		}
		
		/**
		 * initialise the browser. The delay is needed to synchronise the web page
		 * with the additional commands that may be required to fill the screen. The more
		 * additional commands, the more delay is needed. If the initialisation is too short,
		 * the browser will throw an exception with the message (EXECUTION FAILED); 
		 * @param delay
		 */
		private final void performInit( int delay ){
			setQuery( S_IS_INITIALISTED, null );
			start( delay );
		}
	}
}
