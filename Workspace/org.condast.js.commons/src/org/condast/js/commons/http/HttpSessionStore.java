package org.condast.js.commons.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.condast.commons.preferences.AbstractManagedStore;
import org.condast.commons.strings.StringUtils;

public class HttpSessionStore<V extends Object> extends AbstractManagedStore<String, V> {

	private static final String S_WRN_SAME_UI_SESSION = "The selected Http Session is already used.";

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	private HttpSession session;

	public HttpSessionStore() {
		super();
	}

	public HttpSessionStore( HttpSession session ) {
		super(session.getId());
		setSession( session );
	}

	public void setSession(HttpSession session) {
		if(( this.session != null ) && ( this.session.equals( session )))
			logger.warning(S_WRN_SAME_UI_SESSION);
		this.session = session;
	}

	/**
	 * use with caution, as it clears ALL the attributes
	 */
	protected void clear(){
		Enumeration<String> enm = this.session.getAttributeNames();
		List<String> attrs = new ArrayList<>();
		while( enm.hasMoreElements() )
			attrs.add(enm.nextElement() );
		attrs.remove(0);//used internally by RWT
		for( String attr: attrs )
			this.session.removeAttribute( attr );
	}

	@Override
	public void clear( String key ){
		this.session.removeAttribute(key );
	}

	protected void clear( Enum<?> enm ){
		this.session.removeAttribute( enm.name() );
	}

	@Override
	public boolean open() {
		if( this.session == null )
			return false;
		return super.open();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V getSettings(String key) {
		if(!isOpen() )
			return null;
		Object obj = session.getAttribute(key);
		return ( obj == null )? null: (V) obj;
	}

	@Override
	protected void onPutSettings(String key, V value) {
		if( StringUtils.isEmpty( key ))
			return;
		if( value == null )
			session.removeAttribute( key );
		else
			session.setAttribute(key, value);
	}

	/**
	 * Set a boolean, represented by a bit on an int value
	 * @param name
	 * @param position
	 * @param choice
	 */
	@Override
	public void setBoolean( String name, int position, boolean choice ) {
		Integer optionsselect = (Integer) session.getAttribute( name );
		int options = (optionsselect == null) ?0: optionsselect;
		int mask = 1<<position;
		if ( choice )
			options |=mask;
		else
			mask ^= 0xFF;
			options &= mask;

		session.setAttribute(name, options );
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,V> getProperties() {
		Map<String,V> props = new HashMap<>();
		Enumeration<String> enm = session.getAttributeNames();
		while( enm.hasMoreElements() ) {
			String key = enm.nextElement();
			props.put( key, (V) session.getAttribute(key ));
		}
		return props;
	}
}
