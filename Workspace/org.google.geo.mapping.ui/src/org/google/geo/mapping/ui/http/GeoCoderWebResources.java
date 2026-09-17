package org.google.geo.mapping.ui.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = GeoCoderWebResources.class )
@HttpWhiteboardResource(pattern="/geo/web/*", prefix="/WEB-INF")
public class GeoCoderWebResources {

	public static final String S_GEOCODER_RESOURCE = "geo-resource";

	private Logger logger = Logger.getLogger(GeoCoderWebResources.class.getName());

	public GeoCoderWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
