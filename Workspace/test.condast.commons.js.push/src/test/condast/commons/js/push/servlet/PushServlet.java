package test.condast.commons.js.push.servlet;

import org.condast.commons.config.Config;
import org.condast.js.commons.parser.AbstractFileParser.Attributes;
import org.condast.js.push.core.AbstractPushServlet;
import test.condast.commons.js.push.core.Dispatcher;

/*
 * @See: https://www.eclipse.org/jetty/documentation/current/framework-jetty-osgi.html
 * @see: https://examples.javacodegeeks.com/enterprise-java/jetty/jetty-osgi-example/
 */
public class PushServlet extends AbstractPushServlet {
	private static final long serialVersionUID = 1L;
		
	private Config config;
	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	public PushServlet() {
		super( Dispatcher.TITLE );
		config = new Config();
		this.addPushListener(dispatcher);
	}
	
	@Override
	protected String onSetContext(String context, String application, String service) {
		String serverContext = config.getServerContext();
		return serverContext+application+"/" + service + "/push" ;
	}

	@Override
	protected String onGetPublicKey(String id, Attributes attr) {
		return Dispatcher.S_PUBLIC_KEY;
	}	
}