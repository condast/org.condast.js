package org.condast.js.react.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.ui.session.ISessionListener;
import org.condast.commons.ui.session.RefreshSession;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.condast.js.commons.eval.IEvaluationListener.EvaluationEvents;

public class ReactBrowser extends Browser {
	private static final long serialVersionUID = -7462050265419768312L;

	public static final String S_INDEX_HTML = "/react/index.html";
	
	private Collection<IEvaluationListener<Map<String, String>>> listeners;

	private Browser browser;
	private CommandController controller;

	private RefreshSession session = new RefreshSession();

	private Logger logger = Logger.getLogger( this.getClass().getName());
		
	public ReactBrowser(Composite parent, int style) {
		super(parent, style);
		this.browser = this;
		this.controller = new CommandController(this);
		this.browser.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true));
		listeners = new ArrayList<IEvaluationListener<Map<String, String>>>();
		session.init(parent.getDisplay());
		session.start();
		super.setUrl( S_INDEX_HTML);
	}

	@SuppressWarnings("unchecked")
	public void addSessionListener( ISessionListener<Map<String, String>> listener ){
		//this.session.addSessionListener( listener);
	}
	
	@SuppressWarnings("unchecked")
	public void removeSesionListener( ISessionListener<Map<String, String>> listener ){
		//this.session.removeSessionListener((org.condast.commons.ui.session.ISessionListener<Map<String, String>>) listener);
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
	public Object evaluate( final String query ){
  		  evaluate( query, controller.getCallBack() );
 		  return true;
    }
	
	@Override
	public void dispose() {
		session.dispose();
		super.dispose();
	}

	private class CommandController{

		private ReactBrowser browser;
		private LinkedList<Map.Entry<String, String[]>> commands;
		
		public CommandController( ReactBrowser browser) {
			this.browser = browser;
			commands = new LinkedList<Map.Entry<String, String[]>>();
		}

		public BrowserCallback getCallBack(){
			BrowserCallback callback = new BrowserCallback() {

				private static final long serialVersionUID = 1L;

				@Override
				public void evaluationSucceeded(Object result) {
					notifyEvaluation( new EvaluationEvent<Map<String, String>>( browser, ""/* TODO remove*/, EvaluationEvents.SUCCEEDED ));
					logger.info("EXECUTION SUCCEEDED");
				}
				@Override
				public void evaluationFailed(Exception exception) {
					notifyEvaluation( new EvaluationEvent<Map<String, String>>( browser, ""/* TODO remove*/, EvaluationEvents.FAILED ));
					logger.warning("EXECUTION FAILED");
				}
			};
			return callback;
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
	}
}
