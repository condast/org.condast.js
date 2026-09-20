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
	
	private static AuthenticationData data = new AuthenticationData();
	
	protected AuthenticationData( Object[] data) {
		this.id = Double.valueOf((double) data[0]).longValue();
		this.token = Double.valueOf((double) data[1]).longValue();
		this.identifier = (String) data[2];
	}

	protected AuthenticationData( Map<Authentication, String> data) {
		this.id = Double.valueOf( data.get( Authentication.ID)).longValue();
		this.token = Double.valueOf(data.get( Authentication.TOKEN)).longValue();
		this.identifier = data.get(Authentication.IDENTIFIER);
	}

	protected AuthenticationData( String[] data) {
		this.id = Double.valueOf( data[0]).longValue();
		this.token = Double.valueOf(data[1]).longValue();
		this.identifier = data[2];
	}

	protected AuthenticationData(long id, long token, String identifier) {
		super();
		this.id = id;
		this.token = token;
		this.identifier = identifier;
	}

	protected AuthenticationData() {
		super();
		this.id = 0;
		this.token = 0;
		this.identifier = "???";
	}

	public static AuthenticationData getInstance( ) {
		return data;
	}

	public Map<Authentication, String> getData(){
		Map<Authentication, String> params = new HashMap<>();
		params.put( Authentication.ID, String.valueOf( id));
		params.put( Authentication.TOKEN, String.valueOf( token ));
		params.put( Authentication.IDENTIFIER, identifier);
		return params;
	}

	public void setData( Map<Authentication, String> data ){
		this.id = Long.parseLong( data.get( Authentication.ID ));
		this.token = Long.parseLong( data.get( Authentication.TOKEN ));
		this.identifier = data.get( Authentication.IDENTIFIER );
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
