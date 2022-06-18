package org.google.geo.mapping.model;

import com.google.code.geocoder.model.LatLng;

public interface ILocality {

	public String getName();
	
	public String getCountryCode();

	String getShortName();

	LatLng getLnglat();
}
