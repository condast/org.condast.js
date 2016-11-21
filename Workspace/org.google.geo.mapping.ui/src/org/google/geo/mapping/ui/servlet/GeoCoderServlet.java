package org.google.geo.mapping.ui.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.condast.commons.Utils;
import org.google.geo.mapping.ui.session.ISessionListener;

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
		session.stop();
		super.destroy();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String token = req.getParameter( ISessionListener.Parameters.TOKEN.toString() );
		if( Utils.assertNull( token) || ( !S_TOKEN.equals( token ))){
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
		session = GeocoderSession.getInstance();	
		session.addData( map );
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

}
