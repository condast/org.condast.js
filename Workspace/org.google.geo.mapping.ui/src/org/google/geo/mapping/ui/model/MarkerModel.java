package org.google.geo.mapping.ui.model;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;

import org.condast.commons.lnglat.LngLat;
import org.condast.commons.strings.StringStyler;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.google.geo.mapping.ui.controller.GeoCoderController;
import org.google.geo.mapping.ui.view.EvaluationEvent;
import org.google.geo.mapping.ui.view.IEvaluationListener.EvaluationEvents;

public class MarkerModel {

	private static String S_MARKER_CLICKED_ID = "MarkerClickedId";
	private static String S_MARKER_CLICKED = "onMarkerClicked";

	public enum Functions{
		CREATE_MARKER,
		ADD_EATER_MARKER,
		ADD_MARKER,
		REMOVE_MARKER,
		CLEAR_MARKERS,
		SHOW_MARKERS,
		FIT_BOUNDS,
		DELETE_MARKERS;
		
		public String toString(){
			return StringStyler.toMethodString( super.toString());
		}
	}
	
	private Collection<LngLat> data;
	
	private GeoCoderController controller;
	private BrowserFunction markerClicked;
		
	public MarkerModel( GeoCoderController controller ) {
		this.data = new HashSet<LngLat>();
		this.controller = controller;
		this.markerClicked = new MarkerClicked( this.controller.getBrowser() );	
	}

	public void clearMarkers() {
		controller.setQuery(Functions.CLEAR_MARKERS.toString() );
	}	

	public void showMarkers() {
		controller.setQuery(Functions.SHOW_MARKERS.toString() );
	}	

	public void deleteMarkers() {
		data.clear();
		controller.setQuery(Functions.DELETE_MARKERS.toString() );
	}	

	public void createMarkers( String id, LngLat lnglat, String image) {
		String[] params = fillLngLatParams(4, lnglat);
		data.add( lnglat );
		controller.setQuery(Functions.CREATE_MARKER.toString(), params );
	}	

	public void addEaterMarker( LngLat lnglat ) {
		this.addMarker( Functions.ADD_EATER_MARKER, lnglat );
	}

	public void addMarker( String id, double latitude, double longtitude ) {
		this.addMarker( Functions.ADD_MARKER, new LngLat( id, latitude, longtitude ));
	}

	public void addMarker( String id, LngLat lnglat ) {
		this.addMarker( Functions.ADD_MARKER, new LngLat( id, lnglat.getLatitude(), lnglat.getLongitude() ));
	}

	public void addMarker( LngLat lnglat ) {
		this.addMarker( Functions.ADD_MARKER, lnglat );
	}

	protected void addMarker( Functions function, LngLat lnglat ) {
		data.add( lnglat );
		controller.setQuery( function.toString(), getLngLatParams(lnglat));
	}	

	public void removeMarker( String id ) {
		data.remove( id );
		controller.setQuery(Functions.CLEAR_MARKERS.toString() );
		for( LngLat lnglat: data )
			addMarker(lnglat.getId(), lnglat.getLatitude(), lnglat.getLongitude());
	}	

	public void fitBounds( int zoom  ) {
		String[] params=  new String[1];
		params[0] = String.valueOf( zoom );
		controller.setQuery( Functions.FIT_BOUNDS.toString(), params );
	}

	public void synchronize(){
		controller.executeQuery();
	}
	
	public void dispose(){
		this.markerClicked.dispose();
	}
	
	private static String[] getLngLatParams( LngLat lnglat ){
		return fillLngLatParams(3, lnglat);
	}

	private static String[]fillLngLatParams( int size, LngLat lnglat ){
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
			System.out.println("marker Clicked");
			controller.notifyEvaluation( new EvaluationEvent<Object[]>( this, S_MARKER_CLICKED_ID, EvaluationEvents.EVENT, arguments ));
			return super.function(arguments);
		}	
	}
}