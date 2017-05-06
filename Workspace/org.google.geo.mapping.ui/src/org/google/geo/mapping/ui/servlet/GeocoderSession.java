package org.google.geo.mapping.ui.servlet;

import java.util.Map;

import org.condast.js.commons.session.AbstractPushSession;


public class GeocoderSession extends AbstractPushSession<Map<String, String>> {

	private static GeocoderSession session;
	
	private GeocoderSession() {
		super();
	}

	public static GeocoderSession getInstance(){
		if( session == null )
			session = new GeocoderSession();
		return session;
	}

	@Override
	public void dispose() {
		session = null;
		super.dispose();
	}	
	
	
}
