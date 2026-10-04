package org.google.geo.mapping.ui.http;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.session.ISessionListener;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class GeoCoderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
