package org.condast.postcode.api;

import java.io.FileNotFoundException;
import java.util.Map;

import org.condast.commons.na.community.ICommunityQuery;
import org.condast.commons.na.filler.FillMapException;
import org.condast.commons.na.filler.IFillMapProvider;
import org.condast.postcode.api.model.AddressFillMap;
import org.condast.postcode.api.names.CommunityQuery;

public class ServiceComponent implements IFillMapProvider<String>{

	private static final String S_POSTCODE_API_ID = "org.condast.postcode.api";
	
	public enum Requests{
		ADDRESS
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
	public ICommunityQuery getCommunityQuery() {
		try {
			return CommunityQuery.getDefaultQuery();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
