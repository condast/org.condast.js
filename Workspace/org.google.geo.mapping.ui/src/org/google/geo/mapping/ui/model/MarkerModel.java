package org.google.geo.mapping.ui.model;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.condast.commons.lnglat.LngLat;
import org.condast.commons.strings.StringStyler;
import org.google.geo.mapping.ui.view.EvaluationEvent;
import org.google.geo.mapping.ui.view.GeoCoderBrowser;
import org.google.geo.mapping.ui.view.IEvaluationListener;

public class MarkerModel {

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
	
	private GeoCoderBrowser browser;
	
	private IEvaluationListener<Map<String,String>> listener = new IEvaluationListener<Map<String, String>>(){

		@Override
		public void notifyEvaluation(EvaluationEvent<Map<String,String>> event) {
		}
	};
	
	public MarkerModel( GeoCoderBrowser browser ) {
		this.data = new HashSet<LngLat>();
		this.browser = browser;
		this.browser.addEvaluationListener( listener );
	}

	public void clearMarkers() {
		browser.setQuery(Functions.CLEAR_MARKERS.toString() );
	}	

	public void showMarkers() {
		browser.setQuery(Functions.SHOW_MARKERS.toString() );
	}	

	public void deleteMarkers() {
		data.clear();
		browser.setQuery(Functions.DELETE_MARKERS.toString() );
	}	

	public void createMarkers( String id, LngLat lnglat, String image) {
		String[] params=  new String[4];
		params[0] = lnglat.getId();
		DecimalFormat df = new DecimalFormat("#.########");
		params[1] = df.format( lnglat.getLatitude() );
		params[2] = df.format( lnglat.getLongtitude() );
		params[3] = image;
		data.add( lnglat );
		browser.setQuery(Functions.CREATE_MARKER.toString(), params );
	}	

	public void addEaterMarker( LngLat lnglat ) {
		this.addMarker( Functions.ADD_EATER_MARKER, lnglat );
	}

	public void addMarker( String id, double latitude, double longtitude ) {
		this.addMarker( Functions.ADD_MARKER, new LngLat( id, latitude, longtitude ));
	}

	public void addMarker( String id, LngLat lnglat ) {
		this.addMarker( Functions.ADD_MARKER, new LngLat( id, lnglat.getLatitude(), lnglat.getLongtitude() ));
	}

	public void addMarker( LngLat lnglat ) {
		this.addMarker( Functions.ADD_MARKER, lnglat );
	}

	protected void addMarker( Functions function, LngLat lnglat ) {
		String[] params=  new String[3];
		params[0] = lnglat.getId();
		DecimalFormat df = new DecimalFormat("#.########");
		params[1] = df.format( lnglat.getLatitude() );
		params[2] = df.format( lnglat.getLongtitude() );
		data.add( lnglat );
		browser.setQuery( function.toString(), params);
	}	

	public void removeMarker( String id ) {
		data.remove( id );
		browser.setQuery(Functions.CLEAR_MARKERS.toString() );
		for( LngLat lnglat: data )
			addMarker(lnglat.getId(), lnglat.getLatitude(), lnglat.getLongtitude());
	}	

	public void fitBounds( int zoom  ) {
		String[] params=  new String[1];
		params[0] = String.valueOf( zoom );
		browser.setQuery( Functions.FIT_BOUNDS.toString(), params );
	}

	public void synchronize(){
		browser.executeQuery();
	}
}