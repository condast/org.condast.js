package test.condast.commons.js.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = TestJSWebResources.class )
@HttpWhiteboardResource(pattern="/test/*", prefix="/WEB-INF")
public class TestJSWebResources {

	public static final String S_BOOTSTRAP_RESOURCE = "bootstrap-resource";

	private Logger logger = Logger.getLogger(TestJSWebResources.class.getName());

	public TestJSWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
