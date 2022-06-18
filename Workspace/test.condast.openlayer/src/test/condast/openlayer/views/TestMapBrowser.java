package test.condast.openlayer.views;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.images.IDefaultMarkers.Markers;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.openlayer.map.control.IconsView;
import org.openlayer.map.control.NavigationView;
import org.openlayer.map.controller.OpenLayerController;

public class TestMapBrowser extends Browser {
	private static final long serialVersionUID = 1L;

	public static String S_ERR_NO_FIELD_DATA = "The vessel does not have any field data: ";
	public static String S_ERR_NO_GPS_SIGNAL = "NO GPS SIGNAL";

	public static final int DEFAULT_SCAN_DELAY = 20;//20 update pulses

	private OpenLayerController mapController;

	private LatLng centre;
	
	private SessionHandler handler;
	
	private boolean busy;
			
	private ProgressListener plistener = new ProgressListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void completed(ProgressEvent event) {
			try{
				logger.info("Browser activated" );
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}

		@Override
		public void changed(ProgressEvent event) {
		}
	};
	
	private IEvaluationListener<Object> listener= e->onNotifyEvaluation(e);

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public TestMapBrowser(Composite parent, int style) {
		super(parent, style);
		this.mapController = new OpenLayerController( this );
		this.mapController.addEvaluationListener(listener);
		this.addProgressListener(plistener);
		this.handler = new SessionHandler( this.getDisplay() );
		this.busy = false;
	}

	private void onNotifyEvaluation(EvaluationEvent<Object> event) {
		try {
			if(!OpenLayerController.S_CALLBACK_ID.equals(event.getId()))
				return;
			if( Utils.assertNull( event.getData()))
				return;
			Collection<Object> eventData = Arrays.asList(event.getData());
			StringBuilder builder = new StringBuilder();
			builder.append("Map data: ");
			for( Object obj: eventData ) {
				if( obj != null )
					builder.append(obj.toString());
				builder.append(", ");
			}
			logger.fine(builder.toString());
			
			String str = (String) event.getData()[0];
			if( NavigationView.Commands.isValue(str)) {
				NavigationView.Commands cmd = NavigationView.Commands.valueOf(StringStyler.styleToEnum(str));
				switch( cmd ) {
				case GET_GEO_LOCATION:
					Object[] arr = (Object[]) event.getData()[2];
					centre = new LatLng( "home", (double)arr[0], (double)arr[1]);
					return;
				default:
					break;
				}
			}
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		finally {
			busy = false;
		}
	}

	public void setInput() {
		NavigationView view = new NavigationView( this.mapController );
		view.getLocation();
	}
	
	private Markers getMarker() {
		Markers[] markers = Markers.values();
		int index = (int) ( markers.length * Math.random());
		return markers[index];
	}

	public void updateMarkers() {
		handler.addData(S_ERR_NO_FIELD_DATA);
	}
	
	private synchronized void handleUpdates() {
		if(( busy ) || ( centre == null ))
			return;
		logger.info("Handling icons");
		busy = true;
		IconsView icons = new IconsView( mapController );
		icons.clearIcons();
		for( int i=0; i< 1; i++ ) {
			double x = 100*Math.random();
			double y = 100*Math.random();
			LatLng location = LatLngUtils.transform(centre, x, y);
			icons.addMarker(location, getMarker(), Character.forDigit(i,10));
		}
	}
	
	public void dispose() {
		this.mapController.removeEvaluationListener(listener);
		this.mapController.dispose();
		this.removeProgressListener(plistener);
		super.dispose();
	}
	
	private class SessionHandler extends AbstractSessionHandler<String>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<String> sevent) {
			handleUpdates();
		}
		
	}
}
