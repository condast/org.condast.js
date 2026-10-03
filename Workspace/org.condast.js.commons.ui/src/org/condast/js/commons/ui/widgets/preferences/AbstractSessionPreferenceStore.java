package org.condast.js.commons.ui.widgets.preferences;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.condast.commons.preferences.IPreferenceStore;
import org.condast.commons.strings.StringUtils;
import org.eclipse.rap.rwt.service.SettingStore;

public abstract class AbstractSessionPreferenceStore implements IPreferenceStore<String,String>{

	private SettingStore store;
	private boolean open;
	private String name;

	protected AbstractSessionPreferenceStore( String name, SettingStore store, boolean clear ) {
		this.name = name;
		this.store = store;
		this.setDefaults();
		if( clear )
			this.clear();
	}

	protected abstract void setDefaults();


	@Override
	public void clear(String key) {
		try {
			this.store.removeAttribute(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * clear the settings and reset them to default settings
	 */
	protected abstract void clear();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean open() {
		this.open = ( this.store != null );
		return this.open;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void close() {
		this.open = false;
	}


	@Override
	public String getSettings( String key) {
		String s = store.getAttribute( name + "." + key );
		return s;
	}

	protected String getSettings( Enum<?> key) {
		String s = this.getSettings( key.name() );
		return s;
	}


	@Override
	public Map<String, String> getProperties() {
		Map<String, String> settings = new HashMap<>();
		Enumeration<String> enumeration = store.getAttributeNames();
		while( enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			settings.put(key, store.getAttribute(key));
		}
		return settings;
	}

	protected void putSettings( Enum<?> key, String value) {
		this.putSettings( key.name(), value);
	}

	@Override
	public void putSettings( String key, String value) {
		try {
			store.setAttribute( name + "." + key, value);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set a boolean, represented by a bit on an int value
	 * @param name
	 * @param position
	 * @param choice
	 */
	@Override
	public void setBoolean( String name, int position, boolean choice ) {
		String str = getSettings( name );
		int options = StringUtils.isEmpty(str)?0: Integer.parseInt(str);
		int mask = 1<<position;
		if ( choice )
			options |=mask;
		else
			mask ^= 0xFF;
			options &= mask;

		putSettings(name, String.valueOf( options ));
	}
}
