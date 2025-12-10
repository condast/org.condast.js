package org.google.geo.mapping.ui.servlet;

import java.util.Map;

import org.condast.commons.ui.session.SessionEvent;
import org.condast.js.commons.session.AbstractSessionHandler;
import org.eclipse.swt.widgets.Display;


public class GeocoderSession extends AbstractSessionHandler<Map<String, String>> {

	public  GeocoderSession( Display display) {
		super( display);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void onHandleSession(SessionEvent<Map<String, String>> sevent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onHandleSession(org.condast.js.commons.session.SessionEvent<Map<String, String>> sevent) {
		// TODO Auto-generated method stub
		
	}	
	
	
}
