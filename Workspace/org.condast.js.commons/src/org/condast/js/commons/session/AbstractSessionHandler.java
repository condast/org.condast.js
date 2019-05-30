package org.condast.js.commons.session;

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

	private boolean disposed;
	
	private ISessionListener<D> listener = new ISessionListener<D>(){

		@Override
		public void notifySessionChanged(SessionEvent<D> event) {
			if(!disposed )
				onHandleSession( event );
		}
	};

	public void addData( D data ) {
		if( !disposed)
			session.addData(data);
	}
	
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
		this.session.init( display );
		this.session.addSessionListener( listener);
		this.session.start();
	}
	
	protected boolean isDisposed() {
		return disposed;
	}

	protected abstract void onHandleSession( SessionEvent<D> sevent );
	
	public void dispose() {
		try {
			this.session.removeSessionListener(listener);
			this.session.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
