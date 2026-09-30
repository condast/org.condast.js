package org.openlayer.map.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = OpenLayerResources.class )
@HttpWhiteboardResource(pattern="/openlayer/*", prefix="/WEB-INF")
public class OpenLayerResources {

	private Logger logger = Logger.getLogger(OpenLayerResources.class.getName());

	public OpenLayerResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
