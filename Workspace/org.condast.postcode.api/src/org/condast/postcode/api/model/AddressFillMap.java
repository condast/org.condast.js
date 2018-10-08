package org.condast.postcode.api.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.condast.commons.Utils;
import org.condast.commons.na.community.ICommunityQuery;
import org.condast.commons.na.filler.FillMapException;
import org.condast.commons.na.filler.IFillMapProvider;
import org.condast.commons.strings.StringStyler;
import org.condast.postcode.api.names.CommunityQuery;

public class AddressFillMap implements IFillMapProvider<String>{

	public static final String S_REGEX_SPLIT_ALPHA_NUMERIC = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";
	public enum Fields{
		UNKNOWN,
		STREET,
		STREET_EXTENSION,
		NUMBER,
		HOUSE_NUMBER_ADDITION,
		POSTCODE,
		ADDRESS_TYPE,
		SURFACE_AREA,
		TOWN,
		COUNTY,
		STATE,
		COUNTRY,
		PURPOSES,
		LATITUDE,
		LONGTITUDE,
		RD_X,
		RD_Y,
		BAG_ADDRESSABLE_OBJECT_ID,
		BAG_NUMBER_DESIGNATION,
		EXCEPTION_ID,
		EXCEPTION;

		@Override
		public String toString() {
			String retval = super.toString();
			switch( this ){
			case STREET:
				retval = name().toLowerCase();
				break;
			case STREET_EXTENSION:
				break;//Not used in NL
			case NUMBER:
				retval = "houseNumber";
				break;
			case POSTCODE:
				retval = "postcode";
				break;
			case TOWN:
				retval = "city";
				break;
			case COUNTY:
				retval = "municipality";
				break;
			case STATE:
				retval = "province";
				break;
			case COUNTRY:
				retval = name().toLowerCase();
				break;
			case LONGTITUDE:
				retval = "longitude";
				break;
			case LATITUDE:
				retval = name().toLowerCase();
				break;
			default:
				retval = StringStyler.toMethodString( name() );
				break;
			}
			return retval;
		}

		public static Fields toValidType( String str ){
			if( Utils.assertNull( str ))
				return null;
			for( Fields addr: values() ){
				if( addr.name().equals( str ))
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

	private String id;
	private Map<String, String> results;
	private Collection<String> keyset;

	public AddressFillMap( String id ) {
		this.id = id;
		this.keyset = new ArrayList<String>();
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get a community query object
	 * @return
	 */
	@Override
	public ICommunityQuery getCommunityQuery() {
		try {
			return CommunityQuery.getDefaultQuery();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Map<String, String> fillMap(String request, String[] params, String[] keys) throws FillMapException {
		this.keyset.clear();
		if( Utils.assertNull( request ) || ( !request.equals(id )))
			return null;
		if( !Utils.assertNull(keys ))
			this.keyset.addAll( Arrays.asList( keys ));
		results = new HashMap<String, String>();
		if( Utils.assertNull( params[3]))
			return results;
		String[] split = params[3].split(S_REGEX_SPLIT_ALPHA_NUMERIC);
		String extension = (split.length == 1 )?null: split[1];
		try {
			Map<String, String> parsed = PostCodeParser.findAddresses(params[2], Integer.parseInt(split[0]), extension );
			if( parsed.get( Fields.EXCEPTION_ID.toString()) != null )
				throw new PostCodeException( parsed, params[2], params[3] );
			parseResults( Arrays.asList( keys ), parsed );
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map.Entry<Fields, String> getEntry( Fields key, Map<String, ?> rawData ){
		Iterator<?> iterator = rawData.entrySet().iterator();
		while( iterator.hasNext() ){
			Map.Entry<String, ?> entry = (Entry<String, ?>) iterator.next();
			if( key.toString().equals( entry.getKey()))
				return new FieldEntry( entry.getKey(), entry.getValue() );
		}
		return null;
	}
	
	protected boolean parseResults( Collection<String> keys, Map<String, String> rawData ){
		int index = 0;
		for( String key: keys ){
			Fields field = Fields.toValidType( key );
			if( field == null )
				continue;
			Map.Entry<Fields, String> entry = getEntry( field, rawData ); 
			if( entry == null )
				continue;
			results.put( field.name(), entry.getValue());
			index++;
		}
		return ( index >= Fields.values().length );
	}

	private class FieldEntry<V extends Object> implements Map.Entry<Fields, String>{

		private String key;
		private V value;
		
		FieldEntry( String key, V value ) {
			this.key = key;
			this.value = value;
		}

		@Override
		public Fields getKey() {
			return Fields.toValidType( key );
		}

		@Override
		public String getValue() {
			if( value instanceof String )
				return (String) value;
			if( Fields.NUMBER.toString().equals( key)){
				Double dbl = new Double( (double) value );
				return Integer.toString( dbl.intValue() );
			}
			if( value instanceof Double)
				return Double.toString((double) value );
			return ( value == null )? null: value.toString();
		}

		@Override
		public String setValue( String value) {
			throw new NullPointerException( );
		}
	}
	
	private class PostCodeException extends FillMapException{
		private static final long serialVersionUID = 1L;

		private String exceptionId;

		public PostCodeException( Map<String, String> rawData, String postcode, String number) {
			this( rawData.get( Fields.EXCEPTION_ID.toString()), 
					rawData.get( Fields.EXCEPTION.toString() ) + "[" + postcode + ", " + number + "]" );
		}

		private PostCodeException( String exceptionId, String message) {
			super(message);
			this.exceptionId = exceptionId;
		}

		@Override
		public String getMessage() {
			return this.exceptionId + ": " + super.getMessage();
		}
	}
}
