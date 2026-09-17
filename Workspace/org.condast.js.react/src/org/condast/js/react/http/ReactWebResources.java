package org.condast.js.react.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = ReactWebResources.class )
@HttpWhiteboardResource(pattern="/react/web/*", prefix="/WEB-INF")
public class ReactWebResources {

	public static final String S_REACT_RESOURCE = "react-resource";

	private Logger logger = Logger.getLogger(ReactWebResources.class.getName());

	public ReactWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
