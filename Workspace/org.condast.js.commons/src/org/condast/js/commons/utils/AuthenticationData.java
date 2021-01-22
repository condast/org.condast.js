package org.condast.js.commons.utils;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationData{

	public enum Authentication{
		ID,
		TOKEN,
		IDENTIFIER;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}		
	}
	
	private long id;
	private long token;
	private String identifier;
	
	public AuthenticationData( Object[] data) {
		this.id = new Double((double) data[0]).longValue();
		this.token = new Double((double) data[1]).longValue();
		this.identifier = (String) data[2];
	}

	public AuthenticationData( Map<Authentication, String> data) {
		this.id = new Double( data.get( Authentication.ID)).longValue();
		this.token = new Double(data.get( Authentication.TOKEN)).longValue();
		this.identifier = data.get(Authentication.IDENTIFIER);
	}

	public AuthenticationData( String[] data) {
		this.id = new Double( data[0]).longValue();
		this.token = new Double(data[1]).longValue();
		this.identifier = data[2];
	}

	public AuthenticationData(long id, long token, String identifier) {
		super();
		this.id = id;
		this.token = token;
		this.identifier = identifier;
	}
	
	public Map<String, String> toMap(){
		Map<String, String> params = new HashMap<>();
		params.put( Authentication.ID.toString(), String.valueOf( id));
		params.put( Authentication.TOKEN.toString(), String.valueOf( token ));
		params.put( Authentication.IDENTIFIER.toString(), identifier);
		return params;
	}

	public String get( Authentication auth ) {
		String result = this.identifier;
		switch( auth ) {
		case ID:
			result = String.valueOf(id);
			break;
		case TOKEN:
			result = String.valueOf(token);
			break;
		default:
			break;
		}
		return result;
	}
	
	public long getId() {
		return id;
	}

	public long getToken() {
		return token;
	}

	public String getIdentifier() {
		return identifier;
	}
}
