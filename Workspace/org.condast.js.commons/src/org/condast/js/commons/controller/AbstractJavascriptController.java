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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.js.commons.controller.AbstractView.CommandTypes;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.eval.IEvaluationListener.EvaluationEvents;
import org.condast.js.commons.session.AbstractSessionHandler;
import org.condast.js.commons.session.SessionEvent;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractJavascriptController implements IJavascriptController{

	public static final String S_IS_INITIALISTED = "isInitialised";

	public static final int DEFAULT_UPDATE_TIME = 3;

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
	
	private Logger logger = Logger.getLogger( this.getClass().getName());	

	protected AbstractJavascriptController( Browser browser, String idn ) {
		this( browser, idn, false );
	}
		
	protected AbstractJavascriptController( Browser browser, String idn, boolean warnPending ) {
		this.id = idn;
		this.initialised = false;
		this.busy = false;
		this.disposed = false;
		this.browser = browser;
		this.browser.addDisposeListener(e->onWidgetDisposed(e));
		listeners = new ArrayList<>();
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
		setQuery( function, new String[0], false, false);
	}

	@Override
	public synchronized void setQuery( CommandTypes type, String function, boolean callback ){
		controller.setQuery(type, function, new String[0], false, callback);
	}

	@Override
	public synchronized void setQuery( String function, String[] params, boolean array, boolean callback ){
		controller.setQuery( CommandTypes.SEQUENTIAL, function, params, array, callback);
	}

	@Override
	public synchronized void setQuery( CommandTypes type, String function, String[] params, boolean array, boolean callback ){
		controller.setQuery(type, function, params, array, callback);
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
		StringBuilder buffer = new StringBuilder();
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

		private Lock lock;

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
			this.lock = new ReentrantLock();
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
		public synchronized void setQuery( CommandTypes type, String function, String[] params, boolean array, boolean callback ){
			lock.lock();
			try {
				Command command = new Command( type, function, Arrays.asList( params ), array, callback);
				if(!this.commands.contains(command))
					this.commands.add( command );
			}
			finally {
				lock.unlock();
			}
		}	

		private void handleTimer() {
			if( !initialised )
				return;
			lock.lock();
			try {
				if( Utils.assertNull(this.commands)) {
					return;
				}
				if(( busy ) || Utils.assertNull(this.commands))
					return;
				busy = true;
				Command command = this.commands.remove(0);
				
				if( Command.DefaultCommands.isAtomic(command)) {
					command = this.commands.remove(0);
					Combined combined = new Combined( command );
					while( !this.commands.isEmpty() && ( !command.isCallback() || !Command.DefaultCommands.isAtomicEnd(command))) {
						command = this.commands.remove(0);
						combined.addCommand( command);
					}
					command = combined.isEmpty()?null: combined;
				}
				
				if( command == null )
					return;
				if( command.isCallback())
					handler.addData( command);
				else {
					handler.addData( command );
				}
			}
			finally {
				lock.unlock();
			}
		}
		
		/* (non-Javadoc)
		 * @see org.condast.js.commons.controller.IJavascriptController#evaluate(java.lang.String)
		 */
		private Object evaluate( Command command, String query ){
			try{
				browser.evaluate( query, getCallBack( command ) );
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
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Commands in Buffer:\n");
			lock.lock();
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
				lock.unlock();
			}
		}	
		
		private class SessionHandler extends AbstractSessionHandler<Command> {

			protected SessionHandler(Display display) {
				super(display);
			}

			@Override
			protected void onHandleSession(SessionEvent<Command> sevent) {
				Command command = sevent.getData();
				if( command == null )
					return;
				String function = command.getFunction();
				logger.fine( "Processing: " + function);
				history.push( prettyCode( function ));
				evaluate( command, function );
			}	
		}
		
		private class Combined extends Command{

			private Collection<Command> combined;
			
			public Combined( Command command ) {
				super( CommandTypes.SEQUENTIAL, command.getKey(), null, false, false);
				this.combined =  new ArrayList<>();
				this.combined.add(command);
			}

			public Combined( Collection<Command> commands ) {
				super( CommandTypes.SEQUENTIAL, null, null, false, false);
				this.combined =  new ArrayList<>( commands );
			}

			public void addCommand( Command command ) {
				lock.lock();
				try {
					if( !Command.DefaultCommands.isDefaultCommand(command))
						this.combined.add(command);
				}
				finally {
					lock.unlock();
				}
			}

			public boolean isEmpty() {
				return this.combined.isEmpty();
			}
			@Override
			public String getFunction() {
				StringBuilder builder = new StringBuilder();
				lock.lock();
				try {
					Collection<Command> test = new ArrayList<Command>( commands );
					test.forEach( command -> {
						combined.remove(command);
						if( !command.isCallback())
							builder.append(command.getFunction());
					});
				}
				finally {
					lock.unlock();
				}
				return builder.toString();
			}

			@Override
			public String toString() {
				StringBuilder builder = new StringBuilder();
				builder.append("Commands in Buffer:\n");
				lock.lock();
				try {
					combined.forEach(( entry)->{
						builder.append("\t");
						builder.append( Command.setFunction(entry.getKey(), entry.getValue(), false));
						builder.append("\n");
					});
					builder.append("\n");
					return builder.toString();
				}
				finally {
					lock.unlock();
				}
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
			return result;
		}	
	}
}