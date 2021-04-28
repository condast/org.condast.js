package org.openlayer.map.control;

import java.text.DecimalFormat;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener.EvaluationEvents;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.openlayer.map.controller.OpenLayerController;

public class TransformModel {

	private static String S_MARKER_CLICKED_ID = "MarkerClickedId";
	private static String S_MARKER_CLICKED = "onMarkerClicked";

	public enum Functions{
		DO_BOUNCE,
		DO_PAN,
		DO_ROTATE,
		DO_ZOOM;
		
		@Override
		public String toString(){
			return StringStyler.toMethodString( super.toString());
		}
	}
	
	private OpenLayerController controller;
	private BrowserFunction markerClicked;
		
	public TransformModel( OpenLayerController controller ) {
		this.controller = controller;
		this.markerClicked = new MarkerClicked( this.controller.getBrowser() );	
	}

	public void doPan( LatLng lnglat) {
		String[] params = fillLngLatParams(3, lnglat);
		controller.setQuery(Functions.DO_PAN.toString(), params, false, false );
	}	

	public void doBounce( LatLng lnglat) {
		String[] params = fillLngLatParams(3, lnglat);
		controller.setQuery(Functions.DO_BOUNCE.toString(), params, false, false );
	}	

	public void doRotate( float degrees ) {
		String[] params=  new String[1];
		params[0] = String.valueOf( degrees );
		controller.setQuery(Functions.DO_ROTATE.toString(), params, false, false );
	}	

	public void doZoom( int zoom ) {
		String[] params=  new String[1];
		params[0] = String.valueOf( zoom );
		controller.setQuery(Functions.DO_ZOOM.toString(), params, false,false );
	}	

	public void dispose(){
		this.markerClicked.dispose();
	}
	
	private static String[]fillLngLatParams( int size, LatLng lnglat ){
		String[] params = new String[size];
		params[0] = lnglat.getId();
		DecimalFormat df = new DecimalFormat("#.########");
		params[1] = df.format( lnglat.getLatitude() ).replace(",", ".");
		params[2] = df.format( lnglat.getLongitude() ).replace(",", ".");
		return params;
	}

	private class MarkerClicked extends BrowserFunction{
		
		MarkerClicked(Browser browser) {
			super(browser, S_MARKER_CLICKED);
		}

		@Override
		public Object function(Object[] arguments) {
			controller.notifyEvaluation( new EvaluationEvent<Object>( this, S_MARKER_CLICKED_ID, EvaluationEvents.EVENT, arguments ));
			return super.function(arguments);
		}	
	}
}