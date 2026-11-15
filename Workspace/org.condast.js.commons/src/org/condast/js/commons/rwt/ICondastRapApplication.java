package org.condast.js.commons.rwt;

import org.eclipse.rap.rwt.application.Application;

public interface ICondastRapApplication {

	/**
	 * This interface mimics RAP's BasicsApplication, but allows for application specific contributions
	 * @param domain
	 * @param application
	 */
	void configure(String domain, Application application);
}
