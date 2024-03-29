package org.condast.js.commons.session;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

class RefreshSession<T extends Object> {

	private Display display;
	
	private ServerPushSession session;
	private boolean started;
	private boolean refresh;
	private boolean disposed;
	
	private Collection<ISessionListener<T>> listeners;

	public RefreshSession() {
		listeners = new ArrayList<ISessionListener<T>>();
		this.started = false;
		this.refresh = false;
		this.disposed = false;;
		session = new ServerPushSession();
	}

	public void addSessionListener( ISessionListener<T> listener ){
		this.listeners.add( listener );
	}

	public void removeSessionListener( ISessionListener<T> listener ){
		this.listeners.remove( listener );
	}

	public void init( Display display ){
		this.display = display;
		this.display.addListener(SWT.Dispose, e->{ disposed=true;});
	}

	/**
	 * Called to refresh the UI
	 */
	public synchronized void activate( ){
		refresh();
	}
	
	public void start(){
		if( this.started)
			return;
		session.start();
		this.started = true;
	}

	public void stop(){
		this.started = false;
		if( this.disposed)
			return;
		this.session.stop();
	}
	
	protected void notifyListeners( SessionEvent<T> event ) {
		for(ISessionListener<T> listener: listeners){
			try{
				if( listener != null )
					listener.notifySessionChanged( event );
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}		
	}

	public void dispose(){
		if( this.listeners != null )
			this.listeners.clear();
		this.display = null;
		this.stop();
	}
	
	protected void refresh() {
		if( disposed || !started || this.refresh || ( display == null ) || ( display.isDisposed()))
			return;
		this.refresh = true;

		Runnable bgRunnable = new Runnable() {
			@Override
			public void run() {
				if( disposed )
					return;
				display.asyncExec( new Runnable() {
					@Override
					public void run() {
						try {
							session.stop();
							notifyListeners(new SessionEvent<T>( this ));
							refresh = false;
						}
						catch( Exception ex ) {
							ex.printStackTrace();
						}
						start();
					}
				});
			}
		};
		Thread bgThread = new Thread( bgRunnable );
		bgThread.setDaemon( true );
		bgThread.start();
	}
}