package org.condast.js.commons.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

public abstract class AbstractJavascriptController2 implements IJavascriptController{

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
					busy = false;
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
					busy = false;
				}
			}
		};
		return callback;
	}

	protected AbstractJavascriptController2( Browser browser, String idn ) {
		this( browser, idn, false );
	}
		
	protected AbstractJavascriptController2( Browser browser, String idn, boolean warnPending ) {
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
				notifyEvaluation( new EvaluationEvent<Object>( getBrowser(), id, EvaluationEvents.INITIALISED ));
			}
			
			@Override
			public void changed(ProgressEvent event) {
				onLoadChanged();
				notifyEvaluation( new EvaluationEvent<Object>( getBrowser(), id, EvaluationEvents.CHANGED ));
			}
		});
	}

	protected AbstractJavascriptController2( Browser browser, String id, String url ) {
		this( browser, id, LoadTypes.URL, url );
	}
	
	protected AbstractJavascriptController2( Browser browser, String id, LoadTypes type, String url ) {
		this( browser, id );
		setBrowser( type, url );
	}

	protected AbstractJavascriptController2( Browser browser, String id, InputStream in ) {
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
	public  Object[] evaluate( String query ) {
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

	/**
	 * Set a query. It will be carried out as soon as possible
	 * @param function
	 * @param params
	 */
    @Override
	public synchronized void setQuery( String function ){
		setQuery( function, new String[0]);
	}

	@Override
	public synchronized void setQuery( CommandTypes type, String function ){
		controller.setQuery(type, function, new String[0]);
	}

	@Override
	public synchronized void setQuery( String function, String[] params ){
		controller.setQuery( CommandTypes.SEQUENTIAL, function, params);
	}

	@Override
	public synchronized void setQuery( CommandTypes type, String function, String[] params ){
		controller.setQuery(type, function, params);
	}

	/**
	 * Create a default call back function for javascript handling
	 * @param id: the type of callback
	 * @param name: the name of the javascript function
	 * @return
	 */
	protected BrowserFunction createCallBackFunction( String id, String name ){
		return new JavaScriptCallBack(browser, name, id);
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
		public synchronized void setQuery( CommandTypes type, String function, String[] params ){
			lock.lock();
			try {
				Command command = new Command( type, function, Arrays.asList( params ));
				if(!commands.contains(command))
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
				if( Utils.assertNull(commands)) {
					return;
				}
				if(( busy ) || Utils.assertNull(commands))
					return;
				busy = true;
				Command command = commands.remove(0);
				handler.addData( command);
			}
			finally {
				lock.unlock();
			}
		}
		
		/* (non-Javadoc)
		 * @see org.condast.js.commons.controller.IJavascriptController#evaluate(java.lang.String)
		 */
		private Object evaluate( final String query ){
			try{
				browser.evaluate( query, getCallBack() );
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
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Commands in Buffer:\n");
			lock.lock();
			try {
				commands.forEach(( entry)->{
					builder.append("\t");
					builder.append(setFunction(entry.getKey(), entry.getValue()));
					builder.append("\n");
				});
				builder.append("\n");
				return builder.toString();
			}
			finally {
				lock.unlock();
			}
		}
	
		private class Command implements Map.Entry<String, String[]>, Comparable<Command>{

			private CommandTypes type;
			private String key;
			private Collection<String> value;
			
			public Command( CommandTypes type, String key, Collection<String> value) {
				super();
				this.type = type;
				this.key = key;
				this.value = value;
			}

			@Override
			public String getKey() {
				return key;
			}

			@Override
			public String[] getValue() {
				return value.toArray( new String[ value.size()]);
			}

			@Override
			public String[] setValue(String[] value) {
				this.value = Arrays.asList(value);
				return getValue();
			}

			@Override
			public int hashCode() {
				return setFunction(key, getValue()).hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if( super.equals(obj))
					return true;
				if(!( obj instanceof Command ))
					return false;
				Command command = (Command) obj;
				return ( this.compareTo(command) == 0 );
			}

			@Override
			public int compareTo(Command o) {
				if( super.equals(o))
					return 0;
				int result = 0;
				switch( type ){
				case EQUAL:
					result = key.compareTo(o.getKey());
					break;
				case EQUAL_ATTR:
					result = key.compareTo(o.getKey());
					if( result != 0 )
						break;
					result = (( value == null ) && ( o.getValue() == null ))?0: (( value == null ) && ( o.getValue() != null ))?-1:
						 (( value != null ) && ( o.getValue() == null ))?1:0;
					if(( result != 0) || ( value == null ))
						break;
					result = ( value.size() > o.getValue().length)?1:( value.size()< o.getValue().length)?-1:0;
					if( result != 0 )
						break;
					int index = 0;
					for( String str: value ) {
						result = str.compareTo(o.getValue()[index++]);
						if( result != 0)
							break;
					}
					break;
				default:
					result = 1;
				}
				return result;
			}
			
		}
		
		private class SessionHandler extends AbstractSessionHandler<Map.Entry<String, String[]>> {

			protected SessionHandler(Display display) {
				super(display);
			}

			@Override
			protected void onHandleSession(SessionEvent<Map.Entry<String, String[]>> sevent) {
				Map.Entry<String,String[]> command = sevent.getData();
				if( command == null )
					return;
				String function = setFunction(command.getKey(), command.getValue());
				logger.fine("Processing: " + function);
				history.push( prettyCode( function ));
				evaluate( function );
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
		
		private JavaScriptCallBack(Browser browser, String functionName, String id ) {
			super(browser, functionName);
			this.id = id;
		}

		@Override
		public Object function(Object[] arguments) {
			Object result = null;
			try {
				notifyEvaluation( new EvaluationEvent<Object>( this, id, EvaluationEvents.EVENT, arguments ));
				result = super.function(arguments);
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
			return result;
		}	
	}
}