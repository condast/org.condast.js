package org.condast.js.commons.ui.widgets.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class SyncUIExecute {

	private Display display;

	private boolean disposed;

	private Runnable runnable = new Runnable() {

		@Override
		public synchronized void run() {
			if( disposed )
				return;
			try {
				if( disposed )
					return;
				onExecute();
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
			display.removeListener(SWT.Dispose, listener);
		}

	};

	private Listener listener = new Listener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void handleEvent(Event event) {
			checkDisposed();
		}

	};

	public SyncUIExecute( Display displ ) {
		this.display = displ;
		if( checkDisposed())
			return;
		display.asyncExec( new Runnable() {

			@Override
			public void run() {
				display.addListener( SWT.Dispose, listener);
			}

		});
	}

	protected abstract void onExecute( );

	protected final boolean checkDisposed() {
		if( this.disposed )
			return disposed;
		this.disposed = ( this.display == null ) || this.display.isDisposed();
		return this.disposed;
	}

	public void asyncExec() {
		if( checkDisposed())
			return;
		display.asyncExec( runnable );
	}

	public void syncExec() {
		if( checkDisposed())
			return;
		display.syncExec( runnable );
	}

	protected boolean isDisposed() {
		return checkDisposed();
	}

	public void dispose() {
		this.disposed = true;
	}
}
