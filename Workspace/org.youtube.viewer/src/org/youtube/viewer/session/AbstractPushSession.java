package org.youtube.viewer.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractPushSession<T> {

	public static final int DEFAULT_TIMEOUT = 5000;
	private Display display;
	
	private ServerPushSession session;
	private ExecutorService es;
	private boolean refresh;
	private int sleep;
	
	private Collection<ISessionListener<T>> listeners;

	private LinkedList<T> data;
	private boolean dispose;

	protected AbstractPushSession() {
		this( DEFAULT_TIMEOUT );
	}
	
	protected AbstractPushSession( int sleep ) {
		listeners = new ArrayList<ISessionListener<T>>();
		this.dispose = false;
		data = new LinkedList<T>();
		this.sleep = sleep;
		es = Executors.newCachedThreadPool();
	}

	private Runnable runnable = new Runnable() {
		public void run() {
			while(!refresh){
				try{
					Thread.sleep( sleep );
				}
				catch( InterruptedException ex ){
					ex.printStackTrace();
				}
			}
			if(( display == null ) || ( display.isDisposed()))
				return;
			display.asyncExec( new Runnable() {
				public void run() {
					if( dispose )
						return;
					for(ISessionListener<T> listener: listeners){
						while( !data.isEmpty())
							listener.notifySessionChanged( new SessionEvent<T>( this, data.removeFirst() ));
					}
					session.stop();
					start();
				}
			});
		};
	};

	public void addSessionListener( ISessionListener<T> listener ){
		this.listeners.add( listener );
	}

	public void removeSessionListener( ISessionListener<T> listener ){
		this.listeners.remove( listener );
	}

	public void init( Display display ){
			this.display = display;
	}

	public synchronized void addData( T dt ){
		if( dispose )
			return;
		data.add(dt);
		this.refresh = true;
	}
	
	public void start(){
		session = new ServerPushSession();
		session.start();
		es.execute(runnable);
	}

	public void stop(){
		es.shutdown();
	}

	public synchronized void dispose(){
		this.dispose = true;
		this.listeners.clear();
		this.display = null;
		this.stop();
	}
}
