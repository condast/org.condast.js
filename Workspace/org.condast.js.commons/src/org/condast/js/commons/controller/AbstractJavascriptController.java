package org.condast.js.commons.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.js.commons.controller.AbstractView.CommandTypes;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.eval.IEvaluationListener.EvaluationEvents;
import org.condast.js.commons.session.AbstractSessionHandler;
import org.condast.js.commons.session.SessionEvent;
import org.condast.js.commons.utils.StringUtils;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractJavascriptController implements IJavascriptController{

	public static final String S_IS_INITIALISTED = "isInitialised";

	public static final int DEFAULT_UPDATE_TIME = 10;

	protected static String S_BODY = "</body>";

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
	private boolean busy;
	private int busycounter;
	
	private Collection<String> scripts;
	
	private Logger logger = Logger.getLogger( this.getClass().getName());	

	protected AbstractJavascriptController( Browser browser, String idn ) {
		this( browser, idn, false );
	}
		
	protected AbstractJavascriptController( Browser browser, String idn, boolean warnPending ) {
		this.id = idn;
		this.initialised = false;
		this.busy = false;
		this.busycounter = 0;
		this.disposed = false;
		this.browser = browser;
		this.browser.addDisposeListener(e->onWidgetDisposed(e));
		listeners = new ArrayList<>();
		this.scripts = new ArrayList<>();
		this.controller = new CommandController( DEFAULT_UPDATE_TIME );
		browser.addProgressListener( new ProgressListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void completed(ProgressEvent event) {
				onLoadCompleted();
				initialised = true;
				controller.init();
				notifyEvaluation( new EvaluationEvent<Object>( getBrowser(), null, id, EvaluationEvents.INITIALISED ));
			}
			
			@Override
			public void changed(ProgressEvent event) {
				onLoadChanged();
				notifyEvaluation( new EvaluationEvent<Object>( getBrowser(), null, id, EvaluationEvents.CHANGED ));
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

	public void addScript( String script ) {
		this.scripts.add(script);
	}
	
	protected abstract void onLoadCompleted();

	protected void onLoadChanged(){ /* DEFAULT NOTHING */ }
	
	private void onWidgetDisposed(DisposeEvent event) {
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
	
	@Override
	public Browser getBrowser(){
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
	
	@Override
	public boolean isEmpty() {
		return this.controller.isEmpty();
	}

	public boolean isExecuting() {
		return !this.controller.isEmpty();
	}
	
	protected boolean isWarnPending() {
		return warnPending;
	}
	
	protected void setWarnPending(boolean warnPending) {
		this.warnPending = warnPending;
	}
	
	protected Collection<String> getParameters(){
		Collection<String> parameters = new LinkedList<String>();
		return parameters;
	}

	@Override
	public Object[] evaluate( String query ) {
		StringBuilder builder = new StringBuilder();
		builder.append( "return ");
		builder.append( query );
		builder.append("();");
		Object[] results = null;
		try {
			logger.fine(query);
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
			logger.fine(query);
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
	public void addCommand( Command command ) {
		controller.commands.add(command);
	}
	
	/**
	 * Set a query. It will be carried out as soon as possible
	 * @param function
	 * @param params
	 */
    @Override
	public synchronized void setQuery( String function ){
		setQuery( function, new String[0], false);
	}

	@Override
	public synchronized void setQuery( CommandTypes type, String function ){
		controller.setQuery(type, function, new String[0], false);
	}

	@Override
	public synchronized void setQuery( String function, String[] params, boolean array ){
		controller.setQuery( CommandTypes.SEQUENTIAL, function, params, array);
	}

	@Override
	public synchronized void setQuery( CommandTypes type, String function, String[] params, boolean array ){
		controller.setQuery(type, function, params, array);
	}

	 /* Create a default call back function for javascript handling
	 * @param id: the type of callback
	 * @param name: the name of the javascript function
	 * @return
	 */
	protected BrowserFunction createCallBackFunction( String id, String name ){
		return new JavaScriptCallBack(browser, null, name, id);
	}

	/**
	 * Create a default call back function for javascript handling
	 * @param id: the type of callback
	 * @param name: the name of the javascript function
	 * @return
	 */
	protected BrowserFunction createCallBackFunction( String id, Command command, String name ){
		return new JavaScriptCallBack(browser, command, name, id);
	}

	protected String readInput( InputStream in ){
		StringBuilder builder = new StringBuilder();
		Scanner scanner = new Scanner( in );
		try{
			while( scanner.hasNextLine() ) {
				String str = scanner.nextLine();
				if( S_BODY.equals(str.trim())) {
					if( !Utils.assertNull( scripts )) {
						for( String script: scripts ) {
							builder.append(script);
							builder.append("\n");
						}
					}
				}
				builder.append( str );
			}
		}
		finally{
			scanner.close();
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return controller.toString();
	}
	
	private class CommandController{

		private List<Command> commands;
		
		private Stack<String> history;
		
		private ScheduledExecutorService timer;
		private int time;
		
		private SessionHandler handler;

		private BrowserCallback getCallBack( Command command ){
			BrowserCallback callback = new BrowserCallback() {

				private static final long serialVersionUID = 1L;

				@Override
				public void evaluationSucceeded(Object result) {
					try {
						notifyEvaluation( new EvaluationEvent<Object>( browser, command, id, EvaluationEvents.SUCCEEDED ));
					}
					catch( Exception ex ) {
						ex.printStackTrace();
					}
					finally {
						controller.clearHistory();
						busy = false;
					}
				}
				@Override
				public void evaluationFailed(Exception exception) {
					try {
						notifyEvaluation( new EvaluationEvent<Object>( browser, command, id, EvaluationEvents.FAILED ));
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
						busy = false;
					}
				}
			};
			return callback;
		}

		private CommandController( int time ) {
			this.time = time;
			commands = new ArrayList<>();
			history = new Stack<String>();
			handler = new SessionHandler( browser.getDisplay());
		}

		private void init() {
			timer = Executors.newScheduledThreadPool(1);
			timer.scheduleAtFixedRate(()->handleTimer(), time, time, TimeUnit.MILLISECONDS);				
		}
		
		private void clear(){
			this.commands.clear();
		}

		private boolean isEmpty() {
			return this.commands.isEmpty();
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
		public synchronized void setQuery( CommandTypes type, String function, String[] params, boolean array ){
			try {
				Command command = new Command( type, function, Arrays.asList( params ), array);
				if(!this.commands.contains(command))
					this.commands.add( command );
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}	

		private void handleTimer() {
			if( !initialised )
				return;
			try {
				if( Utils.assertNull(this.commands)) {
					return;
				}
				if(( busy && ( busycounter <= 10 ))  || Utils.assertNull(this.commands)) {
					busycounter++;
					return;
				}
				busy = true;
				busycounter = 0;
				
				StringBuilder builder = new StringBuilder();
				while( !commands.isEmpty()) {
					Command command = this.commands.remove(0);
					if( command == null )
						continue;
					builder.append(command.getFunction());
				}
				handler.addData( builder.toString());
			}
			finally {
			}
		}
		
		/* (non-Javadoc)
		 * @see org.condast.js.commons.controller.IJavascriptController#evaluate(java.lang.String)
		 */
		private Object evaluate( Command command, String query ){
			try{
				browser.evaluate( query, getCallBack( command ) );
				browser.requestLayout();
			}
			catch( Exception se ){
				logger.warning( se.getMessage() + ": " + query );
				return false;
			}
			return true;
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
	
		private void handleCommand( Command command ) {
			String function = command.getFunction();
			if( StringUtils.isEmpty(function))
				return;
			logger.fine( "Processing: " + function);
			history.push( prettyCode( function ));
			evaluate( command, function );			
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Commands in Buffer:\n");
			try {
				commands.forEach(( entry)->{
					builder.append("\t");
					builder.append( Command.setFunction(entry.getKey(), entry.getValue(), entry.isArray()));
					builder.append("\n");
				});
				builder.append("\n");
				return builder.toString();
			}
			finally {
			}
		}	
		
		private class SessionHandler extends AbstractSessionHandler<String> {

			protected SessionHandler(Display display) {
				super(display);
			}

			@Override
			protected void onHandleSession(SessionEvent<String> sevent) {
				String command = sevent.getData();
				if( StringUtils.isEmpty(command))
					return;
				browser.evaluate(command);
			}	
		}
	}
	
	/**
	 * a default browser function that can be added to javascript code for call back
	 * @author Kees
	 *
	 */
	private class JavaScriptCallBack extends BrowserFunction{
		
		private String id;
		private Command command;
		
		private JavaScriptCallBack(Browser browser, Command command,  String functionName, String id ) {
			super(browser, functionName);
			this.id = id;
			this.command = command;
		}

		@Override
		public Object function(Object[] arguments) {
			Object result = null;
			try {
				notifyEvaluation( new EvaluationEvent<Object>( this, command, id, EvaluationEvents.EVENT, arguments ));
				result = super.function(arguments);
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
			finally {
			}
			return result;
		}	
	}
}