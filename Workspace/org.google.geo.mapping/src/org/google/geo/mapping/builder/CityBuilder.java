package org.google.geo.mapping.builder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Collection;

import org.google.geo.mapping.geocoder.AbstractGeoBuilder;
import org.google.geo.mapping.model.ILocality;

import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderComponent;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;

public class CityBuilder extends AbstractGeoBuilder<ILocality> {

	public enum Types{
		LOCALITY,
		COUNTRY,
		POLITICAL,;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
		
		public boolean contains( Collection<String> types ){
			return types.contains( this.toString() );
		}
	}
	
	private Locality locality;
	
	public CityBuilder() {
		this( "nl" ); 
	}

	public CityBuilder( String language ) {
		super( language, 3 ); 
		locality = new Locality();
	}

	public ILocality parseRequest( String region, String locality ) throws IOException, InvalidKeyException{
		GeocoderRequest request = new GeocoderRequestBuilder().
				setAddress( locality ).getGeocoderRequest(); 
		//geocoderRequest.addComponent(GeocoderComponent.POSTAL_CODE, postalcode );
		request.addComponent(GeocoderComponent.COUNTRY, region );
		request.addComponent(GeocoderComponent.LOCALITY, locality);
		return super.parse(request);
	}
	
	@Override
	protected boolean skipResult( GeocodeResponse response, GeocoderResult event) {
		if( response.getResults().size() == 1 )
			return false;
		return false;
	}

	@Override
	protected void onParseAddress(GeocoderAddressComponent event) {
		if( Types.LOCALITY.contains( event.getTypes() )){
			locality.setName( event.getLongName());
			locality.setShortName( event.getShortName());
			countUp();
		}
		if( Types.COUNTRY.contains( event.getTypes() )){
			locality.setCountryCode( event.getShortName());
			countUp();
		}
	}

	@Override
	protected void onParseGeometry(GeocoderGeometry event) {
		locality.setLnglat( event.getLocation() );
		countUp();
	}

	@Override
	protected ILocality setResult() {
		return locality;
	}

	private class Locality implements ILocality{

		 private String name;
		 private String shortName;
		 private String countryCode;
		 private LatLng lnglat;
		 
		@Override
		public String getName() {
			return name;
		}

		protected void setName(String name) {
			this.name = name;
		}

		@Override
		public String getShortName() {
			return shortName;
		}
		
		private void setShortName(String shortName) {
			this.shortName = shortName;
		}

		private void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		@Override
		public String getCountryCode() {
			return countryCode;
		}

		@Override
		public LatLng getLnglat() {
			return lnglat;
		}

		public void setLnglat(LatLng lnglat) {
			this.lnglat = lnglat;
		}
	}
}
