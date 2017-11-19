package org.openlayer.map.model;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.data.latlng.LatLng;

public class Geometry {

	private String type;
	private Map<String, String> coordinates;

	protected Geometry(String type) {
		super();
		this.type = type;
		this.coordinates = new HashMap<String, String>();
	}

	public Geometry(String type, LatLng latlng) {
		this( type );
		this.coordinates.put( String.valueOf( latlng.getLongitude()), String.valueOf( latlng.getLatitude()));
	}

	public Geometry(String type, LatLng[] latlngs) {
		this( type );
		for( LatLng latlng: latlngs)
			this.coordinates.put( String.valueOf( latlng.getLongitude()), String.valueOf( latlng.getLatitude()));
	}

	public String getType() {
		return type;
	}

	public Map<String, String> getCoordinates() {
		return coordinates;
	}
}
