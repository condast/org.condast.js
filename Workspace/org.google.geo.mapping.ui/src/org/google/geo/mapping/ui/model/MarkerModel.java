package org.google.geo.mapping.ui.model;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener.EvaluationEvents;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.google.geo.mapping.ui.controller.GeoCoderController;

public class MarkerModel {

	private static String S_MARKER_CLICKED_ID = "MarkerClickedId";
	private static String S_MARKER_CLICKED = "onMarkerClicked";

	public enum Functions{
		CREATE_MARKER,
		ADD_EATER_MARKER,
		ADD_MARKER,
		ADD_MARKER_WITH_IMAGE,
		REMOVE_MARKER,
		CLEAR_MARKERS,
		SHOW_MARKERS,
		SET_MARKER_ICON,
		FIT_BOUNDS,
		DELETE_MARKERS,
		MARKER_CLICKED;
		
		public String toString(){
			return StringStyler.toMethodString( super.toString());
		}
	}
	
	private Collection<LatLng> data;
	
	private GeoCoderController controller;
	private BrowserFunction markerClicked;
		
	public MarkerModel( GeoCoderController controller ) {
		this.data = new HashSet<LatLng>();
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

	public void createMarkers( String id, LatLng lnglat, String image) {
		String[] params = fillLngLatParams(4, lnglat);
		params[3] = image;
		data.add( lnglat );
		controller.setQuery(Functions.CREATE_MARKER.toString(), params );
	}	

	public void addMarker( String id, double latitude, double longtitude ) {
		this.addMarker( Functions.ADD_MARKER, new LatLng( id, latitude, longtitude ));
	}

	public void addMarker( String id, LatLng lnglat ) {
		this.addMarker( Functions.ADD_MARKER, new LatLng( id, lnglat.getLatitude(), lnglat.getLongitude() ));
	}

	public void addMarker( LatLng lnglat ) {
		this.addMarker( Functions.ADD_MARKER, lnglat );
	}

	public void addMarker( LatLng lnglat, String image ) {
		String[] params = fillLngLatParams(4, lnglat);
		params[3] = image;
		data.add( lnglat );
		controller.setQuery(Functions.ADD_MARKER_WITH_IMAGE.toString(), params );
	}

	protected void addMarker( Functions function, LatLng lnglat ) {
		data.add( lnglat );
		controller.setQuery( function.toString(), getLngLatParams(lnglat));
	}	

	public void removeMarker( String id ) {
		data.remove( id );
		controller.setQuery(Functions.CLEAR_MARKERS.toString() );
		for( LatLng lnglat: data )
			addMarker(lnglat.getId(), lnglat.getLatitude(), lnglat.getLongitude());
	}	

	/**
	 * Set the icon of the given marker to the selected image
	 * @param index
	 * @param image
	 */
	public void setMarkerIcon( int index, String image ){
		String[] params=  new String[2];
		params[0] = String.valueOf( index );
		params[1] = image;
		controller.setQuery(Functions.SET_MARKER_ICON.toString(), params );		
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
	
	private static String[] getLngLatParams( LatLng lnglat ){
		return fillLngLatParams(3, lnglat);
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
			controller.notifyEvaluation( new EvaluationEvent<Object[]>( this, S_MARKER_CLICKED_ID, EvaluationEvents.EVENT, arguments ));
			return super.function(arguments);
		}	
	}
}