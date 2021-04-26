package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.js.commons.controller.AbstractView.CommandTypes;

public class Command implements Map.Entry<String, String[]>, Comparable<Command>{

	private CommandTypes type;
	private String key;
	private Collection<String> value;
	private boolean completed;
	private boolean callback;//Is true when the function elicits a callback response in JAVA
	private Collection<Object> results;
	
	public Command( CommandTypes type, String key, Collection<String> value, boolean callback) {
		super();
		this.type = type;
		this.key = key;
		this.value = value;
		this.completed = false;
		this.callback = callback;
		this.results = new ArrayList<>();
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String[] getValue() {
		return value.toArray( new String[ value.size()]);
	}

	@Override
	public String[] setValue(String[] value) {
		this.value = Arrays.asList(value);
		return getValue();
	}

	public boolean isCallback() {
		return callback;
	}

	public void setCallback(boolean callback) {
		this.callback = callback;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Collection<Object> getResults() {
		return results;
	}

	public void setResults(Collection<Object> results) {
		this.results = results;
	}

    /**
	 * Create the correct string from the function enum
	 * @param function
	 * @param params
	 * @return
	 */
	public String getFunction(){
		return setFunction(this.key, getValue() );
	}
	
	@Override
	public int hashCode() {
		return setFunction(key, getValue()).hashCode();
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
		int result = 0;
		switch( type ){
		case EQUAL:
			result = key.compareTo(o.getKey());
			break;
		case EQUAL_ATTR:
			result = key.compareTo(o.getKey());
			if( result != 0 )
				break;
			result = (( value == null ) && ( o.getValue() == null ))?0: (( value == null ) && ( o.getValue() != null ))?-1:
				 (( value != null ) && ( o.getValue() == null ))?1:0;
			if(( result != 0) || ( value == null ))
				break;
			result = ( value.size() > o.getValue().length)?1:( value.size()< o.getValue().length)?-1:0;
			if( result != 0 )
				break;
			int index = 0;
			for( String str: value ) {
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
	public static String setFunction( String function, String[] params){
		StringBuffer buffer = new StringBuffer();
		buffer.append( function );
		buffer.append("(");
		if( !Utils.assertNull(params)){
			for( int i=0; i< params.length; i++ ){
				buffer.append( "'" + params[i] + "'" );
				if( i< params.length-1 )
					buffer.append(",");
			}
		}
		buffer.append(");");
		return buffer.toString();
	}
	

}