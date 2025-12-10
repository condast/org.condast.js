package org.condast.postcode.api;

import jakarta.servlet.Servlet;
import jakarta.ws.rs.ApplicationPath;

import org.condast.commons.messaging.http.AbstractServletWrapper;
import org.condast.postcode.api.rest.PostCodeResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class RestServlet extends AbstractServletWrapper {

	//Same as portion behind /eetmee/ in the alias in plugin.xml
	public static final String S_CONTEXT_PATH = "vastegast";

	public RestServlet() {
		super( S_CONTEXT_PATH );
	}
	
	@Override
	protected Servlet onCreateServlet(String contextPath) {
		//RestApplication resourceConfig = new RestApplication();
		return (Servlet) new ServletContainer();
	}

//	@ApplicationPath(S_CONTEXT_PATH)
//	private class RestApplication extends ResourceConfig {

		//Loading classes is the safest way...
		//in equinox the scanning of packages may not work
//		private RestApplication() {
//			register( PostCodeResource.class );
//		}
//	}
}
