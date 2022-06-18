package org.google.geo.mapping.geocoder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Collection;

import org.condast.commons.Utils;

import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;

public abstract class AbstractGeoBuilder<T extends Object> {

	public enum GeoTypes{
		POSTAL_CODE,
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

	private boolean skipResults = false;
	private boolean completed;
	
	private GeocodeResponse response;
	
	private T result;
	private GeocoderStatus status;
	
	private int maxCount, counter;
	
	private String clientId;
	private String clientKey;
	private String language;
	
	private IGeoResultListener listener = new IGeoResultListener() {
		
		@Override
		public void notifyGeoResult(GeocoderResult event) {
			skipResults = completed || skipResult( response, event );
		}

		@Override
		public void notifyGeoResult(GeocoderGeometry event) {
			if(!skipResults)
				onParseGeometry(event);

		}
		
		@Override
		public void notifyGeoResult(GeocoderAddressComponent event) {
			if(!skipResults )
				onParseAddress( event);
		}
		
	};

	protected AbstractGeoBuilder( String language, int maxCount ) {
		this( null, null, language, maxCount );
	}
	
	protected AbstractGeoBuilder( String clientId, String clientKey, String language, int maxCount ) {
		this.clientId = clientId;
		this.clientKey = clientKey;
		this.language = language;
		this.completed = false;
		this.counter = 0;
		this.maxCount = maxCount;
	}

	protected void countUp(){
		this.counter++;
		this.setCompleted( this.counter >= maxCount );
	}
	
	protected boolean isCompleted() {
		return completed;
	}

	protected abstract T setResult();
	
	protected void setCompleted(boolean completed) {
		this.completed = completed;
		this.result = setResult();
	}

	/**
	 * If true, the result does not provide good geoetyry results
	 * @param event
	 * @return
	 */
	protected abstract boolean skipResult( GeocodeResponse response, GeocoderResult event );

	/**
	 * Parse the address component
	 * @param event
	 */
	protected abstract void onParseAddress( GeocoderAddressComponent event );

	/**
	 * parse the geometry of the result
	 * @param event
	 */
	protected abstract void onParseGeometry( GeocoderGeometry event );
	
	protected T parse( GeocoderRequest request ) throws IOException, InvalidKeyException{
		GeoCoderParser parser = new GeoCoderParser( this.clientId, this.clientKey );
		if( !Utils.assertNull(language ))
			request.setLanguage(language);
		response = parser.parseRequest(request);
		status = response.getStatus();
		if( !GeocoderStatus.OK.equals( response.getStatus()))
			return null;
				
		parser.addlistener(listener);
		parser.notifyListeners(response);
		parser.removelistener(listener);
		return result;
	}
	
	protected GeocodeResponse getResponse() {
		return response;
	}
	
	public GeocoderStatus getStatus() {
		return status;
	}

	public T getResult() {
		return result;
	}
}