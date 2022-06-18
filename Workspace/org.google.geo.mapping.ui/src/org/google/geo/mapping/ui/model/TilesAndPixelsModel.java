package org.google.geo.mapping.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.session.ISessionListener;
import org.google.geo.mapping.ui.controller.GeoCoderController;

public class TilesAndPixelsModel {

	public static final int DEFAULT_TILE_SIZE = 256;
	
	public enum Functions{
		SET_TILE_SIZE,
		SET_LOCATION,
		SET_ZOOM,
		CREATE_LOCATION_INFO,
		ALERT_ZOOM;;
		
		public String toString(){
			return StringStyler.toMethodString( super.toString());
		}
	}

	private GeoCoderController controller;
	
	private int tileSize;
	private Collection<ISessionListener<Map<String, String>>> listeners;
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );	

	public TilesAndPixelsModel( GeoCoderController controller ) {
		super();
		this.controller = controller;
		this.listeners = new ArrayList<ISessionListener<Map<String, String>>>();
		this.tileSize = DEFAULT_TILE_SIZE;
	}

	public void addSessionListener( ISessionListener<Map<String, String>> listener ){
		this.listeners.add( listener );
	}

	public void removeSessionListener( ISessionListener<Map<String, String>> listener ){
		this.listeners.remove( listener );
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize( int tileSize ){
		this.tileSize = tileSize;
		String[] params=  new String[1];
		params[0] = String.valueOf( tileSize );		
		controller.setQuery(Functions.SET_TILE_SIZE.toString(), params, false);
	}


	public void setLocation( LatLng lnglat, int zoom ){
		String[] params=  new String[3];
		params[0] = String.valueOf( lnglat.getLatitude() );
		params[1] = String.valueOf( lnglat.getLongitude() );
		params[2] = String.valueOf( zoom );
		controller.setQuery(Functions.SET_LOCATION.toString(), params, false);
	}

	public void setZoom( int zoom ){
		String[] params=  new String[1];
		params[0] = String.valueOf( zoom );
		controller.setQuery(Functions.SET_ZOOM.toString(), params, false);
	}

	public void createLocationInfo( String name, String description, LatLng lnglat, int zoom ){
		String[] params=  new String[5];
		params[0] = name;
		params[1] = description;
		params[2] = String.valueOf( lnglat.getLatitude() );
		params[3] = String.valueOf( lnglat.getLongitude() );
		params[4] = String.valueOf( zoom );
		logger.info("locationinfo: " + params[0] +" " + params[1] +" "+ params[2] +" "+ params[3] +" "+ params[4]);
		controller.setQuery(Functions.CREATE_LOCATION_INFO.toString(), params, false);
	}

	public void alertZoom(){
		controller.setQuery(Functions.ALERT_ZOOM.toString());
	}

}
