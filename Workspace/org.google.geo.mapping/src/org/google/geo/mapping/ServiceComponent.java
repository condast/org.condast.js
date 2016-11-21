package org.google.geo.mapping;

import java.util.Map;

import org.condast.commons.ds.IFillMapProvider;
import org.google.geo.mapping.geocoder.AddressFillMap;
import org.google.geo.mapping.geocoder.GeoCoderParser;

public class ServiceComponent implements IFillMapProvider<String>{

	private static final String S_GEOMAP_ID = "org.google.geo.mapping";
	
	public enum Requests{
		ADDRESS
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

}
