package org.google.geo.mapping;

import java.util.Map;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.community.ICommunityQuery;
import org.condast.commons.na.filler.FillMapException;
import org.condast.commons.na.filler.IFillMapProvider;
import org.google.geo.mapping.geocoder.AddressFillMap;
import org.google.geo.mapping.geocoder.GeoCoderParser;

public class ServiceComponent implements IFillMapProvider<String>{

	private static final String S_GEOMAP_ID = "org.google.geo.mapping";
	
	public enum Requests{
		ADDRESS,
		LOCATION
	}
	public void activate(){ /* NOTHING */ }

	public void deactivate(){ /* NOTHING */ }

	@Override
	public String getId() {
		return S_GEOMAP_ID;
	}

	@Override
	public Map<String, String> fillMap( String request, String[] params, String[] keys) {
		GeoCoderParser parser = new GeoCoderParser();
		Requests req = Requests.valueOf( request );
		Map<String, String> results = null;
		switch( req ){
		case ADDRESS:
			AddressFillMap amap = new AddressFillMap( req.name(), parser) ;
			results = amap.fillMap(request, params, keys);
			break;
		default:
			break;
		}
		return results;
	}

	@Override
	public LatLng getLocation(String postcode, int houseNumber) {
		GeoCoderParser parser = new GeoCoderParser();
		AddressFillMap amap = new AddressFillMap( Requests.LOCATION.name(), parser );
		try {
			return amap.getLocation(postcode, houseNumber);
		} catch (FillMapException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ICommunityQuery getCommunityQuery() {
		return null;
	}

}
