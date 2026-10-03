package org.condast.js.bootstrap.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = BootstrapWebResources.class )
@HttpWhiteboardResource(pattern="/bootstrap/web/*", prefix="/WEB-INF")
public class BootstrapWebResources {

	public static final String S_BOOTSTRAP_RESOURCE = "bootstrap-resource";

	private Logger logger = Logger.getLogger(BootstrapWebResources.class.getName());

	public BootstrapWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
