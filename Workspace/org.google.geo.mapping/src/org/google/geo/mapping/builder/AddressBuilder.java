package org.google.geo.mapping.builder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.google.geo.mapping.geocoder.AbstractGeoBuilder;

import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;

public class AddressBuilder extends AbstractGeoBuilder<Map<String, String>> {

	public static enum AddressAttributes{
		STREET,
		STREET_EXTENSION,
		NUMBER,
		POSTCODE,
		TOWN,
		STATE,
		COUNTRY,
		LATITUDE,
		LONGTITUDE,
		LNG_LAT;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
		
		public static String[] toKeys(){
			String[] results = new String[ AddressAttributes.values().length];
			int i=0;
			for( AddressAttributes addr: AddressAttributes.values() ){
				results[i] = addr.name();
				i++;
			}
			return results;
		}
	}

	private Map<String, String> results;
	
	private Logger logger = Logger.getLogger( AddressBuilder.class.getName() );
	
	public AddressBuilder() {
		super( "nl", 100 ); 
		results = new HashMap<String, String>();
	}

	public Map<String, String> parseRequest( String region, String postalcode, LatLng latlng ) throws IOException, InvalidKeyException{
		GeocoderRequest request = new GeocoderRequestBuilder().
				setAddress( postalcode ).
				setLocation( latlng ).getGeocoderRequest(); 
		return super.parse(request);
	}
	
	@Override
	protected boolean skipResult( GeocodeResponse response, GeocoderResult event) {
		logger.info( event.getFormattedAddress());
		if( response.getResults().size() == 1 )
			return false;
		boolean skip = !AbstractGeoBuilder.GeoTypes.POSTAL_CODE.contains( event.getTypes() ) ||
				!AbstractGeoBuilder.GeoTypes.POSTAL_CODE.contains( event.getTypes() );
		return skip;
	}

	@Override
	protected void onParseAddress(GeocoderAddressComponent event) {
		logger.info( event.getLongName());
		if( !GeoTypes.POSTAL_CODE.contains( event.getTypes() ))
			return;
		//GeocoderRequest geocoderRequest = builder.
		//		setLocation( location ).
		//		setRegion(region).
		//		setLanguage( language).getGeocoderRequest(); 
	
		if( GeoTypes.COUNTRY.contains( event.getTypes() )){
			countUp();
		}
	}

	@Override
	protected void onParseGeometry(GeocoderGeometry event) {
		logger.info( event.getLocationType().toString());
		//locality.setLnglat( new LngLat( event.getLocation().getLng().doubleValue(), event.getLocation().getLat().doubleValue()));
		countUp();
	}

	@Override
	protected Map<String, String> setResult() {
		return results;
	}
}
