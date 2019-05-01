package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.Utils;
import org.condast.js.commons.controller.IJavascriptController;

public abstract class AbstractView<E extends Enum<E>> {
	
	private IJavascriptController controller;

	protected AbstractView( IJavascriptController controller) {
		this.controller = controller;
	}
	
	protected IJavascriptController getController() {
		return controller;
	}

	/**
	 * Clear the interactions
	 * @return
	 */
	public String clear( E clear ) {
		String query = clear.toString();
		controller.setQuery( query );
		return query;				
	}
	
	protected Collection<String> getParameters( E command ){
		Collection<String> parameters = new ArrayList<String>();
		return parameters;
	}
	
	protected String perform( E command, Collection<String> parameters ){
		String query = command.toString();
		if( Utils.assertNull(parameters))
			controller.setQuery( query);
		else
			controller.setQuery( query, parameters.toArray( new String[ parameters.size() ]));
		return query;
	}

	public void synchronize(){
		controller.synchronize();
	}
}