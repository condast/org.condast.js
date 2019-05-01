package org.condast.js.commons.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractSynchroniser implements ISynchroniser {

	private Map<IJavascriptController, Boolean> controllers;
	
	public AbstractSynchroniser() {
		controllers = new HashMap<>();
	}
	
	/**
	 * Registers the participating controllers. If clear is true, then the
	 * pending commands are cleared if the controller's browser is not visible
	 * @param controller
	 * @param clear
	 */
	@Override
	public void registercontroller( IJavascriptController controller, boolean clear ) {
		this.controllers.put(controller, clear);
	}

	@Override
	public void unregistercontroller( IJavascriptController controller ) {
		this.controllers.remove(controller);
	}

	@Override
	public void dispose() {
		controllers.clear();
	}
	
	/**
	 * Synchronises the registered controllers. 
	 * NOTE: only one browser should be visible at at a certain time
	 */
	protected void synchronize() {
		Iterator<Map.Entry<IJavascriptController,Boolean>> iterator = controllers.entrySet().iterator();
		while( iterator.hasNext() ) {
			Map.Entry<IJavascriptController,Boolean> entry = iterator.next();
			IJavascriptController controller = entry.getKey();
			if( controller.isDisposed())
				continue;
			
			if( controller.isBrowserVisible() ) {
				if( controller.isInitialised())
					controller.synchronize();
			}else {
				if( entry.getValue())
					controller.clear();
			}
		}
	}

}
