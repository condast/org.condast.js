package test.org.condast.openlayer.thread;

import java.util.EventObject;

import org.condast.commons.data.latlng.LatLng;

public class TimerEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private LatLng location;
	
	public TimerEvent( Object arg0, LatLng current ) {
		super(arg0);
		this.location = current;
	}

	public LatLng getLocation(){
		return this.location;
	}
}
