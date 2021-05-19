package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.Utils;
import org.condast.commons.strings.StringUtils;

public abstract class AbstractView<E extends Enum<E>> {

	public static final String S_WINDOW = "window";

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
	
	private String module;

	protected AbstractView( IJavascriptController controller) {
		this( null, controller );
	}
	
	protected AbstractView( String module, IJavascriptController controller) {
		this.controller = controller;
		this.module = module;
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
		controller.setQuery( CommandTypes.EQUAL, query );
		return query;				
	}
	
	protected abstract CommandTypes getCommandType( E command );
	
	protected Collection<String> getParameters( E command ){
		Collection<String> parameters = new ArrayList<String>();
		return parameters;
	}

	protected String perform( E command){
		return this.perform( command, new ArrayList<String>(), false);
	}
	
	protected String perform( E command, Collection<String> parameters, boolean array ){
		return perform( getCommandType( command ), command, parameters.toArray( new String[ parameters.size() ]), array);
	}

	protected String perform(  E command, String[] parameters, boolean array ){
		return this.perform( CommandTypes.SEQUENTIAL, command, parameters, array );
	}
	
	protected String perform( CommandTypes type, E command, String[] parameters, boolean array ){
		String query =  StringUtils.isEmpty(module)? command.toString(): module + "." + command.toString();
		query = query.replace("()", "");
		if( Utils.assertNull(parameters))
			controller.setQuery( type, query);
		else
			controller.setQuery( type, query, parameters, array);
		return query;
	}
}