package test.condast.community;

import java.util.concurrent.ExecutorService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import test.condast.community.suite.TestSuite;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private ExecutorService service;
		
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		TestSuite suite = new TestSuite();
		suite.performTests();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		service.shutdown();
		Activator.context = null;
	}

}
