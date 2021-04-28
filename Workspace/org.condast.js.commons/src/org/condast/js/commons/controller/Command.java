package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.controller.AbstractView.CommandTypes;

public class Command implements Map.Entry<String, String[]>, Comparable<Command>{

	public enum DefaultCommands{
		ATOMIC,
		ATOMIC_END;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
		
		public static Command toCommand( DefaultCommands defcom ) {
			Command command = new Command( CommandTypes.SEQUENTIAL, defcom.toString(), new ArrayList<String>(), false, false );
			return command;
		}

		public static boolean isDefaultCommand( Command command ) {
			if(( command == null ) || ( StringUtils.isEmpty(command.getKey())))
				return false;
			for( DefaultCommands defcom: values() ) {
				if( defcom.toString().equals(command.getKey()))
					return true;
			}
			return false;
		}

		public static boolean isAtomic( Command command ) {
			if(( command == null ) || ( StringUtils.isEmpty(command.getKey())))
				return false;
			return DefaultCommands.ATOMIC.toString().equals(command.getKey());
		}

		public static boolean isAtomicEnd( Command command ) {
			if(( command == null ) || ( StringUtils.isEmpty(command.getKey())))
				return false;
			return DefaultCommands.ATOMIC_END.toString().equals(command.getKey());
		}

	}
	
	private CommandTypes type;
	private String key;
	private Collection<String> value;
	private boolean array;
	private boolean completed;
	private boolean callback;//Is true when the function elicits a callback response in JAVA
	private Collection<Object> results;
	
	public Command( CommandTypes type, String key, Collection<String> value, boolean array, boolean callback) {
		super();
		this.type = type;
		this.array = array;
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

	public boolean isArray() {
		return array;
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
		return setFunction(this.key, getValue(), array );
	}
	
	@Override
	public int hashCode() {
		return setFunction(key, getValue(), array ).hashCode();
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