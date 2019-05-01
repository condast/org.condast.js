package org.condast.js.commons.controller;

public interface ISynchroniser {

	/**
	 * Registers the participating controllers. If clear is true, then the
	 * pending commands are cleared if the controller's browser is not visible
	 * @param controller
	 * @param clear
	 */
	void registercontroller(IJavascriptController controller, boolean clear);

	void unregistercontroller(IJavascriptController controller);

	void dispose();

}