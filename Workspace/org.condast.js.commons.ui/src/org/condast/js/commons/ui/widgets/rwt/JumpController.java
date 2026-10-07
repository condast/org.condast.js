package org.condast.js.commons.ui.widgets.rwt;

import javax.servlet.http.HttpSession;

import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;

public class JumpController<D extends Object> {

	public enum Operations{
		CREATE,
		READ,
		UPDATE,
		DELETE,
		DONE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isValid( String str ) {
			String test = StringStyler.styleToEnum(str);
			for( Operations attr: values()) {
				if( attr.name().equals(test))
					return true;
			}
			return false;
		}

		public String toAttribute(){
			return StringStyler.xmlStyleString(name());
		}

		public static Operations getAttribute( String str ){
			return Operations.valueOf( StringStyler.styleToEnum(str));
		}
	}

	public enum Attributes{
		USER_ID,
		TOKEN,
		PATH,
		SECURITY;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isValid( String str ) {
			String test = StringStyler.styleToEnum(str);
			for( Attributes attr: values()) {
				if( attr.name().equals(test))
					return true;
			}
			return false;
		}

		public String toAttribute(){
			return StringStyler.xmlStyleString(name());
		}

		public static Operations getAttribute( String str ){
			return Operations.valueOf( StringStyler.styleToEnum(str));
		}
	}

	public void jump( JumpEvent<D> event ) {
		HttpSession session = RWT.getUISession().getHttpSession();
		session.setAttribute(event.getDestinationPath(), event);
		jump( event.getDestinationPath(), event.getToken());
	}

	public JumpEvent<D> getEvent( String path ){
		return getEvent(path, true );
	}
	
	@SuppressWarnings("unchecked")
	public JumpEvent<D> getEvent( String path, boolean clear ){
		HttpSession session = RWT.getUISession().getHttpSession();
		JumpEvent<D> event = (JumpEvent<D>) session.getAttribute( path );
		if( clear )
			session.setAttribute(path, null);
		return event;
	}
	
	public static boolean jump( String path, long token ) {
		if( token < 0 )
			return false;
		HttpSession session = RWT.getUISession().getHttpSession();
		session.setAttribute( Attributes.TOKEN.name(), token);
		session.setAttribute(Attributes.PATH.name(), path);
		return RWTUtils.redirect( path  + "?" + Attributes.TOKEN.name().toLowerCase() + "=" + token );
	}

}
