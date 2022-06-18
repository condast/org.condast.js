package org.condast.js.commons.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.js.commons.controller.AbstractView.CommandTypes;

public class Command implements Map.Entry<String, String[]>, Comparable<Command>{
	
	private CommandTypes type;
	private String command;
	private Collection<String> parameters;
	private boolean array;
	private boolean results;

	public Command( CommandTypes type, String key, Collection<String> paramaters, boolean array) {
		this( type, key, paramaters, array, false );
	}
	
	public Command( CommandTypes type, String key, Collection<String> paramaters, boolean array, boolean results) {
		super();
		this.type = type;
		this.array = array;
		this.command = key;
		this.parameters = paramaters;
		this.results = results;
	}

	@Override
	public String getKey() {
		return command;
	}

	@Override
	public String[] getValue() {
		return parameters.toArray( new String[ parameters.size()]);
	}

	@Override
	public String[] setValue(String[] value) {
		this.parameters = Arrays.asList(value);
		return getValue();
	}

	public boolean isArray() {
		return array;
	}

	/**
	 * Returns true if the command generates results
	 */
    public boolean hasResults() {
		return results;
	}

	public void setResults(boolean results) {
		this.results = results;
	}

	/**
	 * Create the correct string from the function enum
	 * @param function
	 * @param params
	 * @return
	 */
	public String getFunction(){
		return setFunction(this.command, getValue(), array );
	}
	
	@Override
	public int hashCode() {
		return setFunction(command, getValue(), array ).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if( super.equals(obj))
			return true;
		if(!( obj instanceof Command ))
			return false;
		Command command = (Command) obj;
		return ( this.compareTo(command) == 0 );
	}

	@Override
	public int compareTo(Command o) {
		if( super.equals(o))
			return 0;
		//Always put the commands with results to the end
		if( this.results && !o.hasResults())
			return -1;
		else if( !this.results && o.hasResults())
			return 1;
		
		int result = 0;
		switch( type ){
		case EQUAL:
			result = command.compareTo(o.getKey());
			break;
		case EQUAL_ATTR:
			result = command.compareTo(o.getKey());
			if( result != 0 )
				break;
			result = (( parameters == null ) && ( o.getValue() == null ))?0: (( parameters == null ) && ( o.getValue() != null ))?-1:
				 (( parameters != null ) && ( o.getValue() == null ))?1:0;
			if(( result != 0) || ( parameters == null ))
				break;
			result = ( parameters.size() > o.getValue().length)?1:( parameters.size()< o.getValue().length)?-1:0;
			if( result != 0 )
				break;
			int index = 0;
			for( String str: parameters ) {
				result = str.compareTo(o.getValue()[index++]);
				if( result != 0)
					break;
			}
			break;
		default:
			result = 1;
		}
		return result;
	}
	
    /**
	 * Create the correct string from the function enum
	 * @param function
	 * @param params
	 * @return
	 */
	public static String setFunction( String function, String[] params, boolean array ){
		StringBuffer buffer = new StringBuffer();
		buffer.append( function );
		buffer.append("(");
		if( !Utils.assertNull(params)){
			for( int i=0; i< params.length; i++ ){
				if(!array )
					buffer.append( "'" + params[i] + "'" );
				else
					buffer.append( params[i] );					
				if( i< params.length-1 )
					buffer.append(",");
			}
		}
		buffer.append(");");
		return buffer.toString();
	}
	

}