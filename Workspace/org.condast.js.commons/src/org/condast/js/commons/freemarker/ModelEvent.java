package org.condast.js.commons.freemarker;

import java.util.EventObject;

public class ModelEvent extends EventObject {
	private static final long serialVersionUID = -2738380292868195528L;

	private String id;
	
	private String value;
	
	private boolean hasChanged;

	public ModelEvent(Object arg0, String id ) {
		this( arg0, id, null );
	}
	
	public ModelEvent(Object arg0, String id, String value ) {
		super(arg0);
		this.id = id;
		this.value = value;
		this.hasChanged = false;
	}

	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}
	
	public boolean hasChanged() {
		return hasChanged;
	}

	public void setValue( String value) {
		this.value = value;
		this.hasChanged = true;
	}
	
	public void append(String append) {
		this.value += append;
		this.hasChanged = true;
	}

}
