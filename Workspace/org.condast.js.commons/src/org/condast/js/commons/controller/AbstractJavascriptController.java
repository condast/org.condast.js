package org.condast.js.commons.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.eval.IEvaluationListener.EvaluationEvents;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

public abstract class AbstractJavascriptController implements IJavascriptController{

	public static final String S_IS_INITIALISTED = "isInitialised";
	
	public enum LoadTypes{
		URL,
		TEXT;
	}

	private Collection<IEvaluationListener<Object>> listeners;

	private CommandController controller;
	private Browser browser;
	private boolean initialised;
	private String id;
	private boolean disposed;
	private boolean warnPending;
	
	private Logger logger = Logger.getLogger( this.getClass().getName());
	
	private BrowserCallback getCallBack(){
		BrowserCallback callback = new BrowserCallback() {

			private static final long serialVersionUID = 1L;

			@Override
			public void evaluationSucceeded(Object result) {
				try {
					notifyEvaluation( new EvaluationEvent<Object>( browser, id, EvaluationEvents.SUCCEEDED ));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
				finally {
					controller.clearHistory();
					Thread.currentThread().interrupt();
					logger.fine("EXECUTION SUCCEEDED");
					controller.executeQuery();
				}
			}
			@Override
			public void evaluationFailed(Exception exception) {
				try {
					notifyEvaluation( new EvaluationEvent<Object>( browser, id, EvaluationEvents.FAILED ));
				}
				catch( Exception ex ) {
					logger.warning(ex.getMessage());
					//ex.printStackTrace();
				}
				finally {
					StringBuffer buffer = new StringBuffer();
					buffer.append( "EXECUTION FAILED: \n" );
					buffer.append( controller.retrieve() );
					logger.warning(buffer.toString());
					controller.clearHistory();
					Thread.currentThread().interrupt();
				}
			}
		};
		return callback;
	}

	private DisposeListener dl = new DisposeListener(){
		private static final long serialVersionUID = 1L;

		@Override
		public void widgetDisposed(DisposeEvent event) {
			try {
				controller.clear();
				controller.clearHistory();
				disposed = true;
				listeners.clear();
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}	
	};

	protected AbstractJavascriptController( Browser browser, String idn ) {
		this( browser, idn, false );
	}
		protected AbstractJavascriptController( Browser browser, String idn, boolean warnPending ) {
		this.id = idn;
		this.initialised = false;
		this.disposed = false;
		this.browser = browser;
		this.browser.addDisposeListener(dl);
		listeners = new ArrayList<>();
		this.controller = new CommandController( );
		browser.addProgressListener( new ProgressListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void completed(ProgressEvent event) {
				onLoadCompleted();
				initialised = true;
				notifyEvaluation( new EvaluationEvent<Object>( getBrowser(), id, EvaluationEvents.INITIALISED ));
				controller.executeQuery();
			}
			
			@Override
			public void changed(ProgressEvent event) {
				onLoadChanged();
				notifyEvaluation( new EvaluationEvent<Object>( getBrowser(), id, EvaluationEvents.CHANGED ));
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
		controller.executeQuery();
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
	
	@Override
	public boolean isBrowserVisible() {
		return browser.isVisible();
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}
	@Override
	public void clear() {
		this.controller.clear();
	}
	
	protected boolean isWarnPending() {
		return warnPending;
	}
	
	protected void setWarnPending(boolean warnPending) {
		this.warnPending = warnPending;
	}
	
	@Override
	public  Object[] evaluate( String query, String[] params ) {
		StringBuilder builder = new StringBuilder();
		builder.append( "return ");
		builder.append( query );
		builder.append("(");
		for( int i=0; i<params.length; i++ ) {
			String p = params[i];
			builder.append(p);
			if( i < params.length -1)
				builder.append(",");
		}
		builder.append(");");
		Object[] results = null;
		try {
			logger.info(query);
			results = (Object[]) browser.evaluate( builder.toString() );
		}
		catch( IllegalStateException ex ) {
			if( this.warnPending )
				logger.info(ex.getMessage());
			else {
				logger.fine(ex.getMessage());				
			}
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see org.condast.js.commons.controller.IJavascriptController#addEvaluationListener(org.condast.js.commons.eval.IEvaluationListener)
	 */
	@Override
	public void addEvaluationListener( IEvaluationListener<Object> listener ){
		this.listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.condast.js.commons.controller.IJavascriptController#removeEvaluationListener(org.condast.js.commons.eval.IEvaluationListener)
	 */
	@Override
	public void removeEvaluationListener( IEvaluationListener<Object> listener ){
		this.listeners.remove(listener);
	}

	public void notifyEvaluation( EvaluationEvent<Object> ee ){
		for( IEvaluationListener<Object> listener: listeners )
			listener.notifyEvaluation(ee);
	}

    @Override
	public synchronized void setQuery( String function, String[] params ){
    	controller.setQuery(function, params);
    }

	/**
	 * Set a query. It will be carried out as soon as possible
	 * @param function
	 * @param params
	 */
    @Override
	public synchronized void setQuery( String function ){
		setQuery( function, new String[0]);
	}

    protected synchronized void executeQuery(){
		if( disposed || browser.isDisposed() || !browser.isVisible() )
			return;
		browser.getDisplay().syncExec( new Runnable() {

			@Override
			public synchronized void run() {
				controller.executeQuery();
			}
			
		});
	}

	protected synchronized void performQuery( String function, String[] params ){
		if(!browser.isVisible() )
			return;
		controller.setQuery(function, params);
		controller.executeQuery();
	}
	
	@Override
	public void synchronize() {
		if(isDisposed())
			return;
		browser.getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				executeQuery();		
			}			
		});
	}
	
	/**
	 * Create a default call back function for javascript handling
	 * @param id
	 * @param name
	 * @return
	 */
	protected BrowserFunction createCallBackFunction( String id, String name ){
		return new JavaScriptCallBack(browser, name, id);
	}

	protected String readInput( InputStream in ){
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
		
		private Stack<String> history;
	
		private CommandController() {
			commands = new LinkedList<Map.Entry<String, String[]>>();
			history = new Stack<String>();
		}

		private void clear(){
			this.commands.clear();
		}

		private void clearHistory(){
			this.history.clear();
		}
		
		private String retrieve(){
			return history.pop();
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

		/* (non-Javadoc)
		 * @see org.condast.js.commons.controller.IJavascriptController#evaluate(java.lang.String)
		 */
		private Object evaluate( final String query ){
			try{
				browser.evaluate( query, getCallBack() );
			}
			catch( IllegalStateException se ){
				logger.warning( se.getMessage() + ": " + query );
				return false;
			}
			return true;
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
			history.push( prettyCode( buffer.toString() ));
			evaluate(buffer.toString());
		}

		/**
		 * Create a pretty code of the javascript by putting in line breaks and tabs
		 * @param code
		 * @return
		 */
		protected String prettyCode( String code ){
			String retval = code.replace("; ", ";");
			retval = code.replace(";", ";\n\t");
			return "\t" + retval + "\n";
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
			logger.fine("EXECUTING: " + buffer.toString() );
			return buffer.toString();
		}
	}
	
	/**
	 * a default browser function that ca be added to javascript code for call back
	 * @author Kees
	 *
	 */
	private class JavaScriptCallBack extends BrowserFunction{
		
		private String id;
		
		private JavaScriptCallBack(Browser browser, String name, String id ) {
			super(browser, name);
			this.id = id;
		}

		@Override
		public Object function(Object[] arguments) {
			notifyEvaluation( new EvaluationEvent<Object>( this, id, EvaluationEvents.EVENT, arguments ));
			return super.function(arguments);
		}	
	}
}
