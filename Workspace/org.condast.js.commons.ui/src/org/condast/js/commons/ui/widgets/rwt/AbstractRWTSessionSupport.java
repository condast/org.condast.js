package org.condast.js.commons.ui.widgets.rwt;

import java.util.logging.Logger;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractRWTSessionSupport {

	private static final String S_WRN_CLOSING_SESSION = "Closing session. Logging out ";
	private static final String S_WRN_DISPOSING_FRONTEND = "The front end widget is disposed";

	private static final int DEFAULT_SESSION_TIMEOUT = 15*60;//15 mins

	private int timeout;
	private boolean reload;
	private Display display;

	private UISession uisession;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Respond to a time out. If 'reload' is true, a refresh was generated
	 * @param reload
	 */
	protected abstract void onHandleTimeout( boolean reload );

	UISessionListener listener = new UISessionListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void beforeDestroy( UISessionEvent event ) {
			if(( display == null ) || ( display.isDisposed()))
				return;
			display.asyncExec( new Runnable() {

				@Override
				public void run() {
					try {
						if(( display == null ) || ( display.isDisposed()))
							return;
						logger.warning( S_WRN_CLOSING_SESSION + !reload);
						onHandleTimeout(reload);
						reload = false;
					}
					catch( Exception ex ){
						ex.printStackTrace();
					}
				}

			});
		}
	};

	protected AbstractRWTSessionSupport( Display display ) {
		this( display, DEFAULT_SESSION_TIMEOUT );
	}

	protected AbstractRWTSessionSupport( Display display, int timeout ) {
		this.reload = false;
		this.timeout = timeout;
		this.display = display;
	}

	void init( Display display ) {
		uisession = RWT.getUISession();
		uisession.getHttpSession().setMaxInactiveInterval( timeout );
		uisession.addUISessionListener( listener );

		Display.getDefault().disposeExec( new Runnable() {

			@Override
			public void run() {
				reload = true;
				logger.warning( S_WRN_DISPOSING_FRONTEND );
			}
		});
	}

	protected UISession getUisession() {
		return uisession;
	}

	public void dispose() throws Throwable {
		RWT.getUISession().removeUISessionListener( listener );
		display = null;
	}

}
