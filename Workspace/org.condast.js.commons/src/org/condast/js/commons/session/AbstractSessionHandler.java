package org.condast.js.commons.session;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Handle the session. This is usually implmenented as an inner class in
 * a composite or other widget
 * @author Condast
 *
 * @param <D>
 */
public abstract class AbstractSessionHandler<D extends Object> {

	private RefreshSession<D> session;

	private Collection<D> data;
	
	private boolean disposed;

	protected AbstractSessionHandler( Display display ) {
		this.session = new RefreshSession<>();
		this.disposed = false;
		display.addListener( SWT.Dispose, new Listener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void handleEvent(Event event) {
				disposed = true;
				dispose();
			}
		});
		data = new ArrayList<>();
		
		this.session.init( display );
		this.session.addSessionListener( e-> onNotifySessionChanged(e));
		this.session.start();
	}

	public void addData( D datum ) {
		if( disposed)
			return;
		data.add(datum);
		session.activate();
	}

	private void onNotifySessionChanged(SessionEvent<D> event) {
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
			this.session.removeSessionListener( e->onNotifySessionChanged(e));
			this.session.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}