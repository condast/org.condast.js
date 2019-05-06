package org.google.geo.mapping.ui.servlet;

import java.util.Map;

import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
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
	
	
}
