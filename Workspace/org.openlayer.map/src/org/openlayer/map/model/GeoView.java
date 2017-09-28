package org.openlayer.map.model;

import org.condast.commons.latlng.LatLng;
import org.condast.js.commons.controller.IJavascriptController;

public class GeoView {


	public static final int DEF_ZOOM = 17;
	
	public static final float DEF_HORIZONTAL = 0.001f;
	public static final float DEF_VERTICAL = 0.001f;
	
	private LatLng latlng;
	private int zoom;
	
	private IJavascriptController controller;
	
	public GeoView( IJavascriptController controller, LatLng latlng) {
		super();
		this.controller = controller;
		zoom = DEF_ZOOM;
		this.latlng = latlng;	
	}
	
	public LatLng getLatLng() {
		return latlng;
	}
	
	public int getZoom() {
		return zoom;
	}
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
	
	public void setLongitude( double longitude ){
		this.latlng = new LatLng( latlng.getLatitude(), longitude );
		jump();		
	}

	public void setLatitude( double latitude ){
		this.latlng = new LatLng( latitude, latlng.getLongitude() );
		jump();		
	}

	public String up(){
		double latitude = latlng.getLatitude() + DEF_VERTICAL;
		this.latlng = new LatLng( latitude, latlng.getLongitude() );
		return jump();
	}
	
	public String down(){
		double latitude = latlng.getLatitude() - DEF_VERTICAL;
		if( latitude < 0 )
			latitude = 0;
		this.latlng = new LatLng( latitude, latlng.getLongitude() );
		return jump();
	}

	public String left(){
		double longitude = latlng.getLongitude() - DEF_HORIZONTAL;
		if( longitude < 0 )
			longitude = 0;
		this.latlng = new LatLng( latlng.getLatitude(), longitude );
		return jump();
	}

	public String right(){
		double longitude = latlng.getLongitude() + DEF_HORIZONTAL;
		this.latlng = new LatLng( latlng.getLatitude(), longitude );
		return jump();
	}

	public String zoom(){
		String query = "zoom(" + this.zoom + ");";
		controller.setQuery( query );
		return query;
	}

	public String zoomout() {
		this.zoom ++;
		return zoom();
	}

	public String zoomin() {
		if( this.zoom > 0)
			this.zoom--;
		return zoom();
	}

	public String init(){
		return jump() + zoom();
	}

	public String jump(){
		String query = "jump(" + this.latlng.getLongitude() + ", " + this.latlng.getLatitude() + ");";
		controller.setQuery( query );
		return query;
	}
}