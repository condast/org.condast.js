package org.openlayer.map.control;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.plane.FieldData;
import org.condast.js.commons.controller.IJavascriptController;

public class GeoView {

	public static final int DEF_ZOOM = 7;
	
	public static final float DEF_HORIZONTAL = 0.001f;
	public static final float DEF_VERTICAL = 0.001f;
	
	public enum Queries{
		ZOOM,
		ZOOMIN,
		ZOOMOUT,
		JUMP;

		@Override
		public String toString() {
			return super.name().toLowerCase();
		}	
	}
	
	private FieldData fieldData;
	
	private IJavascriptController controller;

	public GeoView( IJavascriptController controller) {
		this( controller, null );
	}
	
	public GeoView( IJavascriptController controller, FieldData fieldData) {
		super();
		this.controller = controller;
		this.fieldData = fieldData;	
	}
	
	public FieldData getFieldData() {
		return fieldData;
	}

	public void setFieldData(FieldData fieldData) {
		this.fieldData = fieldData;
	}

	public LatLng getLatLng() {
		return this.fieldData.getCoordinates();
	}
	
	public void setLongitude( double longitude ){
		LatLng location = new LatLng( fieldData.getLatitude(), longitude );
		this.fieldData.setCoordinates(location);
		jump();		
	}

	public void setLatitude( double latitude ){
		LatLng location = new LatLng( latitude, fieldData.getLongitude() );
		this.fieldData.setCoordinates(location);
		jump();		
	}

	public void setLatlng(LatLng latlng) {
		this.fieldData.setCoordinates( latlng );
	}

	public void locate(LatLng latlng) {
		this.fieldData.setCoordinates( LatLngUtils.transform(latlng, this.fieldData.getLength()/20, -this.fieldData.getWidth()/2));
		this.fieldData.setZoom(17);
		//IField field = new Field( this.fieldData);
		//this.fieldData.setCoordinates( field.getCentre());
		jump();
	}

	public int getZoom() {
		return this.fieldData.getZoom();
	}
	public void setZoom(int zoom) {
		this.fieldData.setZoom(zoom);
	}
	
	public String up(){
		LatLng latlng = this.fieldData.getCoordinates();
		double latitude = latlng.getLatitude() + DEF_VERTICAL;
		this.setLatlng( new LatLng( latitude, latlng.getLongitude() ));
		return jump();
	}
	
	public String down(){
		LatLng latlng = this.fieldData.getCoordinates();
		double latitude = latlng.getLatitude() - DEF_VERTICAL;
		if( latitude < 0 )
			latitude = 0;
		this.setLatlng( new LatLng( latitude, latlng.getLongitude() ));
		return jump();
	}

	public String left(){
		LatLng latlng = this.fieldData.getCoordinates();
		double longitude = latlng.getLongitude() - DEF_HORIZONTAL;
		if( longitude < 0 )
			longitude = 0;
		this.setLatlng( new LatLng( latlng.getLatitude(), longitude ));
		return jump();
	}

	public String right(){
		LatLng latlng = this.fieldData.getCoordinates();
		double longitude = latlng.getLongitude() + DEF_HORIZONTAL;
		this.setLatlng( new LatLng( latlng.getLatitude(), longitude ));
		return jump();
	}

	public String zoom(){
		String[] params = new String[1];
		params[0] = String.valueOf( this.fieldData.getZoom() );
		String query = Queries.ZOOM.toString();
		controller.setQuery( query, params, false);
		return query;
	}

	public String zoomin() {
		int zoom = this.fieldData.getZoom();
		this.fieldData.setZoom(++zoom);
		String query = Queries.ZOOMIN.toString();
		controller.setQuery( query );
		return query;
	}

	public String zoomout() {
		int zoom = this.fieldData.getZoom();
		if( zoom > 0)
			zoom--;
		this.fieldData.setZoom(--zoom);
		String query = Queries.ZOOMOUT.toString();
		controller.setQuery( query );
		return query;
	}

	public String init(){
		return jump();
	}

	public String jump(){
		String[] params = new String[3];
		LatLng latlng = this.fieldData.getField().getCentre();
		params[0] = String.valueOf( latlng.getLatitude() );
		params[1] = String.valueOf( latlng.getLongitude());
		params[2] = String.valueOf( this.fieldData.getZoom());
		String query = Queries.JUMP.toString();
		controller.setQuery( query, params, false );
		return query;
	}
}