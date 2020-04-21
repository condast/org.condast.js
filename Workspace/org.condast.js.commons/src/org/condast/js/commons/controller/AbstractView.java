package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.Utils;

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

	protected String perform( E command ){
		return this.perform(command, new ArrayList<String>());
	}
	
	protected String perform( E command, Collection<String> parameters ){
		return perform( command, parameters.toArray( new String[ parameters.size() ]));
	}

	protected String perform( E command, String[] parameters ){
		String query = command.toString();
		if( Utils.assertNull(parameters))
			controller.setQuery( query);
		else
			controller.setQuery( query, parameters);
		return query;
	}

	protected void synchronize(){
		controller.synchronize();
	}
}