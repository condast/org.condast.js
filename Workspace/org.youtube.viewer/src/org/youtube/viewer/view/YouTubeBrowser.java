package org.youtube.viewer.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.youtube.viewer.servlet.PlayerSession;
import org.youtube.viewer.session.ISessionListener;

public class YouTubeBrowser extends Browser {

	public static final String S_INDEX_HTML = "/youtube/index.html";
	
	private Collection<IEvaluationListener<Map<String, String>>> listeners;

	private Browser browser;
	private CommandController controller;

	private PlayerSession session = PlayerSession.getInstance();

	private boolean ready;
	
	private ProgressListener listener = new ProgressListener(){
		private static final long serialVersionUID = 1L;

		@Override
		public void changed(ProgressEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void completed(ProgressEvent event) {
			ready = true;
		}
		
	};

	private Logger logger = Logger.getLogger( this.getClass().getName());
		
	public YouTubeBrowser(Composite parent, int style) {
		super(parent, style);
		this.ready = false;
		this.browser = this;
		this.browser.addProgressListener(listener);
		this.controller = new CommandController(this);
		listeners = new ArrayList<IEvaluationListener<Map<String, String>>>();
		session.init(parent.getDisplay());
		session.start();
		super.setUrl( S_INDEX_HTML);
	}

	public boolean isReady() {
		return ready;
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

    public synchronized Object executeQuery(){
    	return controller.executeQuery();
    }

    public synchronized Object performQuery( String function, String[] params ){
    	controller.setQuery(function, params);
    	return controller.executeQuery();
    }

    @Override
	public Object evaluate( final String query ){
  		  //evaluate( query, controller.getCallBack() );
 		  return true;
    }
	
	@Override
	public void dispose() {
		session.dispose();
		super.dispose();
	}

	private class CommandController{

		//private YouTubeBrowser browser;
		private LinkedList<Map.Entry<String, String[]>> commands;
		
		public CommandController( YouTubeBrowser browser) {
			//this.browser = browser;
			commands = new LinkedList<Map.Entry<String, String[]>>();
		}

		public BrowserCallback getCallBack(){
			BrowserCallback callback = new BrowserCallback() {

				private static final long serialVersionUID = 1L;

				@Override
				public void evaluationSucceeded(Object result) {
					//notifyEvaluation( new EvaluationEvent<Map<String, String>>( browser, EvaluationEvents.SUCCEEDED ));
					logger.info("EXECUTION SUCCEEDED FROM BROWSERCALLBACK");
				}
				@Override
				public void evaluationFailed(Exception exception) {
					//notifyEvaluation( new EvaluationEvent<Map<String, String>>( browser, EvaluationEvents.FAILED ));
					logger.warning("EXECUTION FAILED FROM BROWSERCALLBACK");
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
		
		protected synchronized Object executeQuery(){
			if( commands.isEmpty() )
				return null;
			StringBuffer buffer = new StringBuffer();
			while( !commands.isEmpty() ){
				Map.Entry<String, String[]> command = commands.removeLast();
				buffer.append( setFunction(command.getKey(), command.getValue()));
				buffer.append(" ");
			}
			return evaluate(buffer.toString());
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
