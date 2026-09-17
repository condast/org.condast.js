package org.youtube.viewer.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = YouTubeWebResources.class )
@HttpWhiteboardResource(pattern="/youtube/web/*", prefix="/WEB-INF")
public class YouTubeWebResources {

	public static final String S_YOUTUBE_RESOURCE = "youtube-resource";

	private Logger logger = Logger.getLogger(YouTubeWebResources.class.getName());

	public YouTubeWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
