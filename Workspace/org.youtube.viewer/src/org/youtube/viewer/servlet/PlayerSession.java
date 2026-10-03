package org.youtube.viewer.servlet;

import java.util.Map;

import org.youtube.viewer.session.AbstractPushSession;

public class PlayerSession extends AbstractPushSession<Map<String, String>> {

	private static PlayerSession session;
	
	private PlayerSession() {
		super();
	}

	public static PlayerSession getInstance(){
		if( session == null )
			session = new PlayerSession();
		return session;
	}

	@Override
	public void dispose() {
		session = null;
		super.dispose();
	}	
	
	
}
