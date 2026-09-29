package org.condast.commons.js.legal.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = LegalWebResources.class )
@HttpWhiteboardResource(pattern="/legal/web/*", prefix="/WEB-INF")
public class LegalWebResources {

	public static final String S_LEGAL_RESOURCE = "legal";

	private Logger logger = Logger.getLogger(LegalWebResources.class.getName());

	public LegalWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
