package org.openlayer.map.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = WebResources.class )
@HttpWhiteboardFilterName( MapFilter.S_FILTER_NAME )
@HttpWhiteboardResource(pattern="/openlayer-resource/*", prefix="/WEB-INF")
public class WebResources {

	private Logger logger = Logger.getLogger(WebResources.class.getName());

	public WebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
