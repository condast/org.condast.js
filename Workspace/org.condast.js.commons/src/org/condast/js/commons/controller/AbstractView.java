package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.Utils;

public abstract class AbstractView<E extends Enum<E>> {

	/**
	 * There are three types of commands:
	 * 1: sequential: when commands always have to be added
	 * 2: equal, when commands may be replaced or ignored if the same command is added again
	 * 3: equal-attr, as 2, but only when the attributes are the same 
	 * @author Kees
	 *
	 */
	public enum CommandTypes{
		SEQUENTIAL,
		EQUAL,
		EQUAL_ATTR;
	}

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
		controller.setQuery( CommandTypes.EQUAL, query, false );
		return query;				
	}
	
	protected abstract CommandTypes getCommandType( E command );
	
	protected Collection<String> getParameters( E command ){
		Collection<String> parameters = new ArrayList<String>();
		return parameters;
	}

	protected String perform( E command, boolean callback){
		return this.perform( command, new ArrayList<String>(), callback);
	}
	
	protected String perform( E command, Collection<String> parameters, boolean callback ){
		return perform( getCommandType( command ), command, parameters.toArray( new String[ parameters.size() ]), callback);
	}

	protected String perform(  E command, String[] parameters, boolean callback ){
		return this.perform( CommandTypes.SEQUENTIAL, command, parameters, callback );
	}
	
	protected String perform( CommandTypes type, E command, String[] parameters, boolean callback ){
		String query = command.toString();
		if( Utils.assertNull(parameters))
			controller.setQuery( type, query, callback);
		else
			controller.setQuery( type, query, parameters, callback);
		return query;
	}
}