package org.google.geo.mapping.geocoder;

import java.io.IOException;
import java.security.InvalidKeyException;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;

/**
 * @See: https://code.google.com/archive/p/geocoder-java/
 * @author Kees
 *
 */
public class GeoCoder {

	private String clientId;
	private String clientKey;
	
	public void getAddress( String language, String address ) throws IOException{
		final Geocoder geocoder = new Geocoder(); 
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().
				setAddress( address).
				setLanguage( language).getGeocoderRequest(); 
		GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
		geocoderResponse.getResults();
	}

	public void getAddressFromAPI( String language, String address ) throws IOException, InvalidKeyException{
		final Geocoder geocoder = new Geocoder(clientId, clientKey); 
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().
				setAddress( address).
				setLanguage( language).getGeocoderRequest(); 
		GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
		geocoderResponse.getResults();
	}
}
