package org.google.geo.mapping.geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.community.ICommunityQuery;
import org.condast.commons.na.filler.FillMapException;
import org.condast.commons.na.filler.IFillMapProvider;

import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderResult;

public class AddressFillMap implements IFillMapProvider<String>{

	public enum Fields{
		UNKNOWN,
		STREET,
		STREET_EXTENSION,
		NUMBER,
		POSTCODE,
		TOWN,
		COUNTY,
		STATE,
		COUNTRY,
		ROOFTOP,
		LATITUDE,
		LONGTITUDE,
		LNG_LAT;

		@Override
		public String toString() {
			String retval = super.toString();
			switch( this ){
			case STREET:
				retval = "route";
				break;
			case STREET_EXTENSION:
				//address.setStreetExtension( entry.getValue());
				break;
			case NUMBER:
				//address.setStreetExtension( entry.getValue());
				break;
			case POSTCODE:
				retval = "postal_code";
				break;
			case TOWN:
				retval = "locality";
				break;
			case COUNTY:
				retval = "administrative_area_level_2";
				break;
			case STATE:
				retval = "administrative_area_level_1";
				break;
			case COUNTRY:
				retval = name().toLowerCase();
				break;
			case LNG_LAT:
				retval = name().toLowerCase();
				break;
			default:
				break;
			}
			return retval;
		}

		public static Fields toValidType( String str ){
			if( Utils.assertNull( str ))
				return null;
			for( Fields addr: values() ){
				if( addr.toString().equals( str ))
					return addr;
			}
			return Fields.UNKNOWN;
		}

		public static boolean isValidKey( String str ){
			if( Utils.assertNull( str ))
				return false;
			for( Fields addr: values() ){
				if( addr.name().equals( str ))
					return true;
			}
			return false;
		}

		public static String[] toKeys(){
			String[] results = new String[ Fields.values().length];
			int i=0;
			for( Fields addr: values() ){
				results[i] = addr.name();
				i++;
			}
			return results;
		}
	}

	private GeoCoderParser coder;
	private String id;
	private Map<String, String> results;
	private Collection<String> keyset;

	private Logger logger = Logger.getLogger( this.getClass().getName());

	private IGeoResultListener listener = new IGeoResultListener() {

		@Override
		public void notifyGeoResult(GeocoderResult event) {
			logger.fine( event.toString());
		}

		@Override
		public void notifyGeoResult(GeocoderAddressComponent event) {
			parseResults(event, keyset );
		}

		@Override
		public void notifyGeoResult(GeocoderGeometry event) {
			parseResults(event, keyset);
		}

	};

	public AddressFillMap( String id, GeoCoderParser coder ) {
		this.coder = coder;
		this.id = id;
		this.keyset = new ArrayList<String>();
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the location of the givben postcode and house number
	 * @throws FillMapException 
	 */
	@Override
	public LatLng getLocation(String postcode, int houseNumber) throws FillMapException {
		String[] keys = new String[2];
		keys[0] = Fields.LATITUDE.name();
		keys[1] = Fields.LONGTITUDE.name();

		String[] params = new String[4];
		params[0] = postcode;
		params[1] = String.valueOf( houseNumber );
		Map<String, String> result = fillMap( LOCATION_ID, params, keys);
		Iterator<Map.Entry<String, String>> iterator = result.entrySet().iterator();
		LatLng latLng = new LatLng( postcode + "-" + houseNumber );
		while( iterator.hasNext() ){
			Map.Entry<String, String> entry = iterator.next();
			Fields key = Fields.valueOf( entry.getKey() );
			switch( key){
			case LATITUDE:
				latLng.setLatitude( Double.parseDouble(entry.getValue() ));
				break;
			case LONGTITUDE:
				latLng.setLongitude(Double.parseDouble(entry.getValue() ));
				break;
			default:
				break;
			}
		}
		return latLng;
	}

	@Override
	public Map<String, String> fillMap(String request, String[] params, String[] keys) {
		this.keyset.clear();
		if( Utils.assertNull( request ) || ( !request.equals(id )))
			return null;
		if( !Utils.assertNull(keys ))
			this.keyset.addAll( Arrays.asList( keys ));
		results = new HashMap<String, String>();
		coder.addlistener(listener);
		try {
			coder.getAddress(params[0], params[1], params[2], params[3]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		coder.removelistener(listener);
		return results;
	}

	protected boolean parseResults( GeocoderAddressComponent gcac, Collection<String> keys ){
		logger.fine( gcac.toString());
		String str = ( String )gcac.getTypes().iterator().next();
		Fields key = Fields.toValidType( str );
		if(( keys != null ) && ( !keys.contains( key.name())))
			return false;
		int index = 0;
		if( Fields.UNKNOWN.equals( key ))
			return false;
		switch( key){
		case POSTCODE:
			results.put(key.name(), gcac.getLongName());
			break;
		case NUMBER:
			results.put(key.name(), gcac.getLongName());
			break;
		case STREET:
			results.put(key.name(), gcac.getLongName());
			break;
		case STREET_EXTENSION:
			results.put(key.name(), gcac.getLongName());
			break;
		case TOWN:
			results.put(key.name(), gcac.getLongName());
			break;
		case COUNTY:
			results.put(key.name(), gcac.getLongName());
			break;
		case STATE:
			results.put(key.name(), gcac.getLongName());
			break;
		case COUNTRY:
			results.put(key.name(), gcac.getLongName());
			break;
		default:
			break;
		}
		return ( index >= Fields.values().length );
	}

	protected boolean parseResults( GeocoderGeometry gcgm, Collection<String> keys ){
		logger.fine( gcgm.toString());
		String str = ( String )gcgm.getLocationType().toString();
		Fields key = Fields.toValidType( str );
		boolean check = ( keys == null ) || ( keys.contains( key.name()));
		if( !check ){	
			check = ( Fields.ROOFTOP.equals( key ))? keys.contains( Fields.LNG_LAT.name() ): false;
		}
		if(!check )
			return false;
		int index = 0;
		if( Fields.UNKNOWN.equals( key ))
			return false;
		switch( key){
		case ROOFTOP:
			results.put( Fields.LATITUDE.name(), gcgm.getLocation().getLat().toString());
			results.put( Fields.LONGTITUDE.name(), gcgm.getLocation().getLng().toString());
			break;
		default:
			break;
		}
		return ( index >= Fields.values().length );
	}

	@Override
	public ICommunityQuery getCommunityQuery() {
		return null;
	}

}
