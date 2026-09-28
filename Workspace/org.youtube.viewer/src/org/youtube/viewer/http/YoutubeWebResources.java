package org.youtube.viewer.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = YoutubeWebResources.class )
@HttpWhiteboardResource(pattern="/youtube/web/*", prefix="/WEB-INF")
public class YoutubeWebResources {

	private Logger logger = Logger.getLogger(YoutubeWebResources.class.getName());

	public YoutubeWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
