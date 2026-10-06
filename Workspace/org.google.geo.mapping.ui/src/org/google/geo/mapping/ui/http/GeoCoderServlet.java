package org.google.geo.mapping.ui.http;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.session.ISessionListener;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = Servlet.class, 
scope=ServiceScope.PROTOTYPE,
property= "osgi.http.whiteboard.servlet.pattern=/" + GeoCoderServlet.S_CONTEXT_PATH + "/*")
public class GeoCoderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String S_CONTEXT_PATH = "geo";

	private static final String S_TOKEN = "9812365834502355000";

	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	private GeocoderSession session;
	
	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public void destroy() {
			super.destroy();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String token = req.getParameter( ISessionListener.Parameters.TOKEN.toString() );
		if( StringUtils.isEmpty( token) || ( !S_TOKEN.equals( token ))){
			super.doGet(req, resp);
			return;
		}
		Enumeration<String> attrs = req.getParameterNames();
		Map<String, String> map = new HashMap<String, String>();
		
		while( attrs.hasMoreElements()){
			String attr = attrs.nextElement();
			if( !ISessionListener.Parameters.TOKEN.toString().equals(attr))
				map.put( attr, URLDecoder.decode( req.getParameter(attr), "UTF-8"));
		}
		logger.info("DO GET " + map.toString());
		//session = new GeocoderSession.getInstance();	
		session.addData( map );
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}

}
