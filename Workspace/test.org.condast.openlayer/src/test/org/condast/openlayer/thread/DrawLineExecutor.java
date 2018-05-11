package test.org.condast.openlayer.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;

public class DrawLineExecutor {

	private Field field;
	private Timer timer;
	
	private Collection<ITimerListener> listeners;
	
	public DrawLineExecutor( Field field, int start, int time ) {
		timer = new Timer();
		this.field = field;
		this.listeners = new ArrayList<ITimerListener>();
		timer.schedule(new InterruptTask(), start, time);
	}

	public void addListener( ITimerListener listener ) {
		this.listeners.add( listener );
	}

	public void removeListener( ITimerListener listener ) {
		this.listeners.remove( listener );
	}
	
	public void shutdown() {
		this.timer.cancel();
	}
	
	private class InterruptTask extends TimerTask {

		public void run() {
			try{
				LatLng location = field.random();
				for( ITimerListener listener: listeners)
					listener.notifyChanged( new TimerEvent(this, location));
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
			finally{
			}
		}
	}	

}
