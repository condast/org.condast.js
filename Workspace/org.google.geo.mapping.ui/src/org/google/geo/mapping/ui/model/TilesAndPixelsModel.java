package org.google.geo.mapping.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.lnglat.LngLat;
import org.condast.commons.strings.StringStyler;
import org.google.geo.mapping.ui.session.ISessionListener;
import org.google.geo.mapping.ui.view.EvaluationEvent;
import org.google.geo.mapping.ui.view.GeoCoderBrowser;
import org.google.geo.mapping.ui.view.IEvaluationListener;

public class TilesAndPixelsModel {

	public static final int DEFAULT_TILE_SIZE = 256;
	
	public enum Functions{
		SET_TILE_SIZE,
		SET_LOCATION,
		CREATE_LOCATION_INFO,
		ALERT_ZOOM;;
		
		public String toString(){
			return StringStyler.toMethodString( super.toString());
		}
	}

	private GeoCoderBrowser browser;
	
	private int tileSize;
	private Collection<ISessionListener<Map<String, String>>> listeners;
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	private IEvaluationListener<Map<String,String>> listener = new IEvaluationListener<Map<String, String>>(){

		@Override
		public void notifyEvaluation(EvaluationEvent<Map<String,String>> event) {
		
			logger.info( "query performed: " + event.getEvaluationEvent());}
	};
	

	public TilesAndPixelsModel( GeoCoderBrowser browser ) {
		super();
		this.browser = browser;
		this.listeners = new ArrayList<ISessionListener<Map<String, String>>>();
		this.browser.addEvaluationListener( listener );
		//this.browser.addSessionListener( slistener);
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
		browser.setQuery(Functions.SET_TILE_SIZE.toString(), params);
	}

	public void synchronize(){
		browser.executeQuery();
	}

	public void setLocation( LngLat lnglat, int zoom ){
		String[] params=  new String[3];
		params[0] = String.valueOf( lnglat.getLatitude() );
		params[1] = String.valueOf( lnglat.getLongtitude() );
		params[2] = String.valueOf( zoom );
		browser.setQuery(Functions.SET_LOCATION.toString(), params);
	}

	public void createLocationInfo( String name, String description, LngLat lnglat, int zoom ){
		String[] params=  new String[5];
		params[0] = name;
		params[1] = description;
		params[2] = String.valueOf( lnglat.getLatitude() );
		params[3] = String.valueOf( lnglat.getLongtitude() );
		params[4] = String.valueOf( zoom );
		logger.info("locationinfo: " + params[0] +" " + params[1] +" "+ params[2] +" "+ params[3] +" "+ params[4]);
		browser.setQuery(Functions.CREATE_LOCATION_INFO.toString(), params);
	}

	public void alertZoom(){
		browser.setQuery(Functions.ALERT_ZOOM.toString());
	}

}
