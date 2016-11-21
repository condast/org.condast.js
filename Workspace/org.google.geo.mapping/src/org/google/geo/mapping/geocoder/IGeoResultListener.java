package org.google.geo.mapping.geocoder;

import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderResult;

public interface IGeoResultListener {

	public void notifyGeoResult( GeocoderResult event );

	public void notifyGeoResult( GeocoderAddressComponent event );

	public void notifyGeoResult( GeocoderGeometry event );

}
