package org.condast.js.commons.rwt;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.condast.commons.ui.widgets.entry.IDataEntryPoint;
import org.condast.js.commons.session.AbstractSessionHandler;
import org.condast.js.commons.session.SessionEvent;
import org.condast.js.commons.ui.widgets.rwt.AbstractRWTSessionSupport;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractCondastEntryPoint<S extends Object> extends AbstractEntryPoint implements IDataEntryPoint<S>, AutoCloseable{
	private static final long serialVersionUID = 1L;

	public static final String S_PAGE = "page";

	public static final String S_INVALID_PREPARATION = "Illegal access";

	public static final int DEFAULT_SCHEDULE = 1000; //milliseconds
	public static final int DEFAULT_SESSION_TIMEOUT= 9000 ; //15 minutes seconds

	private String domain;
	
	private RWTUiSessionHandler handler;

	private SessionHandler session;

	private String message;

	private S store;

	private ScheduledExecutorService timer;
	private int startTime, rate;
	
	protected AbstractCondastEntryPoint( String domain ) {
		this( domain, DEFAULT_SCHEDULE, DEFAULT_SCHEDULE);
	}

	protected AbstractCondastEntryPoint( String domain, int startTime, int rate ) {
		super();
		this.domain = domain;
		this.startTime = startTime;
		this.rate = rate;
		this.message = S_INVALID_PREPARATION;
	}

	protected String getDomain() {
		return domain;
	}

	protected S getSessionStore() {
		return store;
	}

	@Override
	public void setData( S store) {
		this.store = store;
		HttpSession session = RWT.getUISession().getHttpSession();
		//if( this.store != null )
		//	session.setAttribute(IDomainProvider.Attributes.TOKEN.name(), store.getToken());
		session.setMaxInactiveInterval(DEFAULT_SESSION_TIMEOUT);
	}

	/**
	 * Try to retrieve the token. Use the one stored
	 * in the session store if possible, otherwise see
	 * if it is also present in the http session
	 * @return
	 */
	protected long getToken() {
		//if( this.store != null )
		//	return store.getToken();
		HttpSession session = RWT.getUISession().getHttpSession();
		//if( session.getAttribute(IDomainProvider.Attributes.TOKEN.name()) == null )
		//	return -1;
		return (long) 1l;//session.getAttribute(IDomainProvider.Attributes.TOKEN.name());
	}
	
	protected Locale onSetLocale() {
		Locale locale = new Locale.Builder()
		        .setLanguage("nl")
		        .setRegion("NL")
		        .build();
		return locale;
	}

	@Override
	protected void createContents(Composite parent) {
		parent.setData(RWT.CUSTOM_VARIANT, "outside-color");
		try{
			this.message = S_INVALID_PREPARATION;
			if( !prepare( parent )) {
				Label label = new Label( parent, SWT.NONE);
				label.setText( createMessage());
				return;
			}
			parent.addDisposeListener( _->close());
			handler = new RWTUiSessionHandler(parent.getDisplay());
			//Set the RWT Locale
			parent.setData( RWT.CUSTOM_VARIANT, domain );
			Locale locale = onSetLocale();
			RWT.setLocale(locale);
			RWT.getUISession().setLocale(locale);
			Locale.setDefault( locale );
	        parent.setLayout(new FillLayout( SWT.VERTICAL));
			Composite composite = createComposite(parent );
			composite.setData( RWT.CUSTOM_VARIANT, domain );
			composite.addDisposeListener(_->close());
			createTimer(false, 1, TimeUnit.MILLISECONDS, this.startTime, this.rate);
			if(!postProcess(parent)) {
				message = getClass().getName() + ": " + message;
				throw new Exception( createMessage());
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	protected boolean prepare( Composite parent ) {
		session = new SessionHandler( parent.getDisplay());
		return true;
	}

	protected abstract Composite createComposite( Composite parent  );

	protected boolean postProcess( Composite parent ) {
		RWT.getUISession().getHttpSession().setMaxInactiveInterval( DEFAULT_SESSION_TIMEOUT);
		return true;
	}

	protected String createMessage( ) {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	protected void handleTimer() {
		if( session != null )
			session.addData(getSessionStore());
	}

	protected void createTimer( boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate ) {
		if(!create)
			return;
		timer = Executors.newScheduledThreadPool(nrOfThreads);
		timer.scheduleAtFixedRate(()->handleTimer(), startTime, rate, unit);
	}


	protected void onRefresh( S store ) {
		/** DEFAULT: NOTHING */
	}

	protected void stopTimer() {
		this.timer.shutdown();
	}

	/**
	 * Handles a session time out. If a false is returned, the time out is stopped
	 * @param reload
	 * @return
	 */
	protected boolean handleSessionTimeout( boolean reload ) {
		return true;
	}
	
	protected void setHttpSessionTimeout( int timeout ) {
		RWT.getUISession().getHttpSession().setMaxInactiveInterval(timeout);
	}
	
	@Override
	public  void close(){
		try {
			if( timer != null )
				timer.shutdown();
			handler.dispose();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private class SessionHandler extends AbstractSessionHandler<S>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<S> sevent) {
			try {
				if( sevent != null )
					onRefresh( sevent.getData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class RWTUiSessionHandler extends AbstractRWTSessionSupport{

		public RWTUiSessionHandler(Display display) {
			super(display, Integer.MAX_VALUE);
		}

		@Override
		protected void onHandleTimeout(boolean reload) {
			try {
				Logger logger = Logger.getLogger(AbstractCondastEntryPoint.class.getName());
				if(!handleSessionTimeout( reload )) {
					return;
				}
				logger.info("Invalidating session");
				handleSessionTimeout(reload );
				close();
				//store.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}