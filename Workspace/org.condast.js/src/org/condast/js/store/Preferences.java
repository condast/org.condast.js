package org.condast.js.store;

import org.condast.commons.Utils;
import org.condast.commons.preferences.AbstractStore;


public class Preferences extends AbstractStore<String, Object>{

	public static final String NAENTRY = "fieldlab"; 

	private enum Attributes{
		LOGGED_IN,
		COMPANY;
	}
	
	private static Preferences preferences = new Preferences();
	
	private Preferences(){
		super( NAENTRY );
	}

	public static Preferences getInstance(){
		return preferences;
	}

	public void clear() {
	}

	public boolean isLoggedin(){
		Object obj = super.getSettings( Attributes.LOGGED_IN.name() );
		if( obj == null )
			return false;
		return (boolean)obj;	
	}
	
	public boolean setLoggedIn( boolean choice ){
		super.putSettings( Attributes.LOGGED_IN.name(), new Boolean( choice ));
		return choice;
	}
	
	public String getOrganisation(){
		Object obj = super.getSettings( Attributes.COMPANY.name() );
		if( obj == null )
			return null;;
		String query = (String) obj;
		return Utils.assertNull(query)? null: query;		
	}
	
	public void setROrganisation(String organisation) {
		super.putSettings( Attributes.COMPANY.name(), organisation );
	}

	@Override
	public Object getSettings(Enum<?> key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putSettings(Enum<?> name, Object value) {
		// TODO Auto-generated method stub
		
	}
}
