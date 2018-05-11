package test.org.condast.openlayer.thread;

public interface ITimerListener {

	public enum NotificationEvents{
		ACTIVE,
		LOCATION,
		WAYPOINTS,
		UPDATE, 
		TRAJECTORY;
	}
	public void notifyChanged( TimerEvent event );
}
