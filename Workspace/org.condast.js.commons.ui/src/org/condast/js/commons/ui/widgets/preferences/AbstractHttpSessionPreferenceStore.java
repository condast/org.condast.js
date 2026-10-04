package org.condast.js.commons.ui.widgets.preferences;

import java.util.logging.Logger;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import org.condast.commons.preferences.IPreferenceStore;

public abstract class AbstractHttpSessionPreferenceStore<T extends Object> implements IPreferenceStore<String,T>{

	private static final String S_INFO_SESSION_CREATED = "Session created.";
	private static final String S_INFO_SESSION_DEDTROYED = "Session destroyed.";

	private HttpSession store;
	private boolean open;
	private String name;

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	private HttpSessionListener listener = new HttpSessionListener(){

		@Override
		public void sessionCreated(HttpSessionEvent arg0) {
			logger.info(S_INFO_SESSION_CREATED);
			onSessionCreated(arg0);
		}

		@Override
		public void sessionDestroyed(HttpSessionEvent arg0) {
			logger.info(S_INFO_SESSION_DEDTROYED);
			onSessionDestroyed(arg0);
		}

	};

	protected AbstractHttpSessionPreferenceStore( String name ) {
		this.name = name;
	}

	protected AbstractHttpSessionPreferenceStore( String name, HttpSession store, boolean clear ) {
		this( name );
		this.setStore(store);
		this.setDefaults();
		if( clear )
			this.clear();
	}

	protected abstract void setDefaults();

	protected abstract void onSessionCreated( HttpSessionEvent arg0 );

	protected abstract void onSessionDestroyed( HttpSessionEvent arg0 );

	protected HttpSession getStore() {
		return store;
	}

	public void setStore(HttpSession store) {
		this.store = store;
		this.store.getServletContext().addListener( listener );
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

	@SuppressWarnings("unchecked")
	@Override
	public T getSettings( String key) {
		return (T) store.getAttribute(name + "." + key);
	}

	protected T getSettings( Enum<?> key) {
		return this.getSettings( key.name() );
	}

	protected void putSettings( Enum<?> key, T value) {
		this.putSettings( key.name(), value);
	}

	@Override
	public void putSettings( String key, T value) {
		store.setAttribute( name + "." + key, value);
	}
}
