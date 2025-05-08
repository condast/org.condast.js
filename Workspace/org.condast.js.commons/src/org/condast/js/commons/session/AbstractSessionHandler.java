package org.condast.js.commons.session;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Handle the session. This is usually implemented as an inner class in
 * a composite or other widget
 * @author Condast
 *
 * @param <D>
 */
public abstract class AbstractSessionHandler<D extends Object> {

	private RefreshSession session;

	private Collection<D> data;
	
	private boolean disposed;
	
	private ISessionListener<Object> listener = e-> onNotifySessionChanged(e);

	protected AbstractSessionHandler( Display display ) {
		this( new RefreshSession(), display );
	}
	
	protected AbstractSessionHandler( RefreshSession session, Display display ) {
		this.session = session;
		this.disposed = false;
		display.addListener( SWT.Dispose, new Listener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void handleEvent(Event event) {
				try {
					disposed = true;
					dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		data = new ArrayList<>();
		
		this.session.init( display );
		this.session.addSessionListener( listener );
		this.session.start();
	}

	public RefreshSession getSession() {
		return session;
	}

	public void addData( D datum ) {
		data.add(datum);
		session.activate();
	}

	private void onNotifySessionChanged(SessionEvent<Object> event) {
		if( disposed )
			return;
		Collection<D> temp = new ArrayList<D>( data );
		data.clear();
		for( D datum: temp)
			onHandleSession( new SessionEvent<D>( this, datum ));
	}

	protected boolean isDisposed() {
		return disposed;
	}

	protected abstract void onHandleSession( SessionEvent<D> sevent );

	public void dispose() {
		try {
			this.session.removeSessionListener( listener );
			this.session.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
