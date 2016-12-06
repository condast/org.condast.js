package org.condast.postcode.api;

import java.util.Map;

import org.condast.commons.ds.IFillMapProvider;
import org.condast.postcode.api.model.AddressFillMap;

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
		Requests req = Requests.valueOf( request );
		Map<String, String> results = null;
		switch( req ){
		case ADDRESS:
			AddressFillMap amap = new AddressFillMap( req.name() );
			results = amap.fillMap(request, params, keys);
			break;
		default:
			break;
		}
		return results;
	}

}
