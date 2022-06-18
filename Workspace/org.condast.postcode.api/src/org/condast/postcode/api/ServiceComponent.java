package org.condast.postcode.api;

import java.io.FileNotFoundException;
import java.util.Map;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.community.ICommunityQuery;
import org.condast.commons.na.filler.FillMapException;
import org.condast.commons.na.filler.IFillMapProvider;
import org.condast.commons.na.location.CommunityQuery;
import org.condast.postcode.api.model.AddressFillMap;

public class ServiceComponent implements IFillMapProvider<String>{

	private static final String S_POSTCODE_API_ID = "org.condast.postcode.api";
	
	private CommunityQuery query; 
	
	public enum Requests{
		ADDRESS,
		LOCATION
	}
	public void activate(){ /* NOTHING */ }

	public void deactivate(){ /* NOTHING */ }

	@Override
	public String getId() {
		return S_POSTCODE_API_ID;
	}

	@Override
	public Map<String, String> fillMap( String request, String[] params, String[] keys) throws FillMapException {
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
	
	@Override
	public LatLng getLocation(String postcode, int houseNumber) {
		AddressFillMap amap = new AddressFillMap( Requests.LOCATION.name() );
		try {
			return amap.getLocation(postcode, houseNumber);
		} catch (FillMapException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ICommunityQuery getCommunityQuery() {
		try {
			if( query == null ) {
				query = (CommunityQuery) CommunityQuery.getDefaultQuery();
				query.prepare(null, null);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
