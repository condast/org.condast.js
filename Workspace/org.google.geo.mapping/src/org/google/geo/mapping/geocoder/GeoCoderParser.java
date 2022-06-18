package org.google.geo.mapping.geocoder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collection;

import org.google.geo.mapping.geocoder.AddressFillMap.Fields;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderComponent;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;

/**
 * @See: https://code.google.com/archive/p/geocoder-java/
 * @author Kees
 *
 */
public class GeoCoderParser {

	private String clientId;
	private String clientKey;
	
	private Collection<IGeoResultListener> listeners;
	
	public GeoCoderParser() {
		listeners = new ArrayList<IGeoResultListener>();
	}

	public GeoCoderParser( String clientId, String clientKey ) {
		this.clientId = clientId;
		this.clientKey = clientKey;
		listeners = new ArrayList<IGeoResultListener>();
	}

	public void addlistener( IGeoResultListener listener ){
		this.listeners.add( listener );
	}

	public void removelistener( IGeoResultListener listener ){
		this.listeners.remove( listener );
	}

	protected void notifylisteners( GeocoderResult event ){
		for( IGeoResultListener listener: listeners)
			listener.notifyGeoResult(event);
	}

	protected void notifylisteners( GeocoderAddressComponent event ){
		for( IGeoResultListener listener: listeners)
			listener.notifyGeoResult(event);
	}

	protected void notifylisteners( GeocoderGeometry event ){
		for( IGeoResultListener listener: listeners)
			listener.notifyGeoResult(event);
	}

	public GeocodeResponse parseRequest( GeocoderRequest request ) throws IOException{
		Geocoder geocoder = new Geocoder(); 		
		return geocoder.geocode( request);
	}

	public void notifyListeners( GeocodeResponse response ){
		for( GeocoderResult result: response.getResults()){
			notifylisteners( result );
			for( GeocoderAddressComponent gcac: result.getAddressComponents()){
				notifylisteners( gcac);
			}
			GeocoderGeometry geometry = result.getGeometry();
			notifylisteners(geometry);
		}
	}
	
	public GeocodeResponse getAddress( String language, String region, String postalcode, String number ) throws IOException{
		Geocoder geocoder = new Geocoder(); 
		String address = postalcode + ", " + number;
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().
				setRegion( region ).
				setAddress( address).
				setLanguage( language).getGeocoderRequest(); 
		geocoderRequest.addComponent(GeocoderComponent.POSTAL_CODE, postalcode );
		geocoderRequest.addComponent(GeocoderComponent.COUNTRY, region.toUpperCase() );
		GeocodeResponse response = geocoder.geocode(geocoderRequest);
		for( GeocoderResult result: response.getResults()){
			for( GeocoderAddressComponent gcac: result.getAddressComponents()){
				String type = gcac.getTypes().iterator().next();
				if( !Fields.POSTCODE.toString().equals(type))
					continue;
				if( !gcac.getShortName().contains(postalcode))
						continue;
				notifylisteners( gcac);
			}
			GeocoderGeometry geometry = result.getGeometry();
			LatLng location = geometry.getLocation();
			try {
				getAddressFromLocation(language, region, location);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
			notifylisteners(geometry);
		}
		return  geocoder.geocode(geocoderRequest);
	}

	public void getAddressFromAPI( String language, String address, String region ) throws IOException, InvalidKeyException{
		Geocoder geocoder = new Geocoder(clientId, clientKey); 
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().
				setAddress( address).
				setLanguage( language).getGeocoderRequest(); 
		GeocodeResponse response = geocoder.geocode(geocoderRequest);
		LatLng location = null;
		for( GeocoderResult result: response.getResults()){
			for( GeocoderAddressComponent gcac: result.getAddressComponents())
				notifylisteners( gcac);
			GeocoderGeometry geometry = result.getGeometry();
			location = geometry.getLocation();
			getAddressFromLocation(language, region, location);
			notifylisteners(geometry);
		}
	}

	public void getAddressFromLocation( String language, String region, LatLng location ) throws IOException, InvalidKeyException{
		Geocoder geocoder = new Geocoder(); 
		GeocoderRequestBuilder builder = new GeocoderRequestBuilder();
		GeocoderRequest geocoderRequest = builder.
				setLocation( location ).
				setRegion(region).
				setLanguage( language).getGeocoderRequest(); 
		GeocodeResponse response = geocoder.geocode(geocoderRequest);
		for( GeocoderResult result: response.getResults()){
			for( GeocoderAddressComponent gcac: result.getAddressComponents())
				notifylisteners( gcac);
			notifylisteners(result.getGeometry());
		}
	}
}
