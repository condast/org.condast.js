package org.condast.js.commons.session;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class RefreshSession {

	private Display display;

	private ServerPushSession session;
	private boolean started;
	private boolean disposed;

	private Collection<ISessionListener<Object>> listeners;

	public RefreshSession() {
		listeners = new ArrayList<>();
		this.started = false;
		this.disposed = false;
	}

	public void addSessionListener( ISessionListener<Object> listener ){
		this.listeners.add( listener );
	}

	public void removeSessionListener( ISessionListener<Object> listener ){
		this.listeners.remove( listener );
	}

	public void init( Display display ){
		this.display = display;
		this.display.addListener(SWT.Dispose, e->{ disposed=true;});
	}

	/**
	 * Called to refresh the UI
	 */
	public synchronized void activate(){
		refresh();
	}

	public void start(){
		if( this.started)
			return;
		session = new ServerPushSession();	
		session.start();
		this.started = true;
	}

	public void stop(){
		this.started = false;
		this.session.stop();
	}

	protected void notifyListeners( SessionEvent<Object> event ) {
		for(ISessionListener<Object> listener: listeners){
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
		if( disposed || !started || ( display == null ) || ( display.isDisposed()))
			return;

		display.asyncExec( new Runnable() {
			@Override
			public void run() {
				try {
					stop();
					notifyListeners(new SessionEvent<>( this ));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
				finally {
					start();
				}
			}
		});
	}
}