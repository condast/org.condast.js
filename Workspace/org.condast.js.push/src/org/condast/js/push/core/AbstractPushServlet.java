package org.condast.js.push.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.condast.commons.auth.AuthenticationData.Authentication;
import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.parser.AbstractFileParser;
import org.condast.js.commons.parser.AbstractFileParser.Attributes;
import org.condast.js.push.core.IPushListener.Calls;

public abstract class AbstractPushServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String S_PATH = "/resources";
	private static final String S_PUSH_FILE = "push.html";
	private static final String S_MIME_JAVASCRIPT = "text/javascript";

	private String title;
	private long idCounter;
	private int token;
	
	private Collection<IPushListener> listeners;
	
	protected AbstractPushServlet( String title) {
		super( );
		this.idCounter = 0;
		this.title = title;
		this.listeners = new ArrayList<>();
	}

	protected String getTitle() {
		return title;
	}

	protected long getIdCounter() {
		return idCounter;
	}

	protected int getToken() {
		return token;
	}

	public void addPushListener( IPushListener listener ) {
		this.listeners.add(listener);
	}

	public void removePushListener( IPushListener listener ) {
		this.listeners.remove(listener);
	}
	
	protected void notifylisteners( PushEvent event) {
		for( IPushListener listener: this.listeners)
			listener.notifyPushEvent(event);
	}

	protected abstract String onSetContext(String context, String application, String service);

	protected abstract String onGetPublicKey(String id, Attributes attr);

	protected boolean subscribe( HttpServletRequest req, HttpServletResponse resp ) {
		String userid = req.getParameter(Authentication.ID.toString());
		if( StringUtils.isEmpty( userid ))
			return false;
		long id = Long.valueOf(userid);
		String token = req.getParameter(Authentication.TOKEN.toString());
		Scanner scanner = null;
		StringBuilder builder = new StringBuilder();
		try {
			scanner = new Scanner( req.getInputStream());
			while( scanner.hasNextLine())
				builder.append(scanner.nextLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			scanner.close();
		}
		notifylisteners( new PushEvent( this, Calls.SUBSCRIBE, idCounter, id, token, builder.toString()));
		return true;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		String path = req.getPathInfo();
		if( StringUtils.isEmpty(path))
			path = "/" + S_PUSH_FILE;
		path = S_PATH + path;
		
		String mimeType = ( path.endsWith("js"))?S_MIME_JAVASCRIPT:null;
		resp.setContentType(mimeType);
		try {
			Parser parser = new Parser( AbstractPushServlet.class);
			String result = parser.parse( AbstractPushServlet.class.getResourceAsStream( path ));
			resp.getWriter().write( result );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			super.doGet(req, resp);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userid = req.getParameter(Authentication.ID.toString());
		if( StringUtils.isEmpty( userid ))
			return;
		String path = req.getPathInfo().replace("/", "");
		boolean result = false;
		if( !StringUtils.isEmpty(path) && Calls.isValidCall(path)) {
			Calls call  = Calls.valueOf(path.toUpperCase());
			switch( call ) {
			case SUBSCRIBE:
			case UPDATE:
				result = subscribe(req, resp);
				break;
			default: 
				break;
			}		
		}
		if( !result )
			super.doPost(req, resp);
	}

	private class Parser extends AbstractFileParser{

		public Parser(Class<?> clss) {
			super(clss);
		}
	
		@Override
		protected String onHandleContext(String context, String application, String service) {
			return onSetContext(context, application, service);
		}

		@Override
		protected void onHandleLinks(String link) {
			// NOTHING		
		}

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			String result = attr.toString();
			switch( attr ) {
			case TITLE:
				result = title;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onHandleAuthentication(String id, Authentication attr) {
			String result = attr.toString();
			switch( attr ) {
			case TOKEN:
				result = String.valueOf( token );
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onHandleValues(Functions function, String id, Attributes attr) {
			// NOTHING
			return null;
		}

		@Override
		protected String onHandleFunction(Functions function, String id, Attributes attr) {
			String result = null;
			switch( function) {
			case WORKER:
				result = String.valueOf(++idCounter);
				token = Long.toHexString(idCounter).hashCode();
				break;
			case VAPID:
				result = onGetPublicKey(id, attr);
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onHandleScript(Class<?> clss, String path) {
			// NOTHING
			return null;
		}
	}
}
