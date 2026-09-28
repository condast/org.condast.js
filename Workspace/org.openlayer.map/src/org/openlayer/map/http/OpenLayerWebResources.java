package org.openlayer.map.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = OpenLayerWebResources.class )
@HttpWhiteboardResource(pattern="/openlayer/web/*", prefix="/WEB-INF")
public class OpenLayerWebResources {

	private Logger logger = Logger.getLogger(OpenLayerWebResources.class.getName());

	public OpenLayerWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
