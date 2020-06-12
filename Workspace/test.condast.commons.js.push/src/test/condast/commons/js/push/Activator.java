package test.condast.commons.js.push;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import test.condast.commons.js.push.suite.TestSuite;

public class Activator implements BundleActivator {

	public static final String BUNDLE_ID = "test.condast.commons.js.push";

	private static BundleContext context;

	private TestSuite suite = TestSuite.getInstance();

	private Logger logger = Logger.getLogger(this.getClass().getName());

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		logger.info("ACTIVATED: " + BUNDLE_ID);
		//suite.runTests();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
