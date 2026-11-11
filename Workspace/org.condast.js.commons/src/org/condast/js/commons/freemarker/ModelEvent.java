package org.condast.js.commons.freemarker;

import java.util.EventObject;

import freemarker.template.TemplateModel;

public class ModelEvent extends EventObject {
	private static final long serialVersionUID = -2738380292868195528L;

	private String id;
	
	private String value;
	private  TemplateModel model;
	
	private boolean hasChanged;

	public ModelEvent(Object arg0, String id, TemplateModel model ) {
		this( arg0, id, null, model );
	}
	
	public ModelEvent(Object arg0, String id, String value, TemplateModel model ) {
		super(arg0);
		this.id = id;
		this.value = value;
		this.model = model;
		this.hasChanged = false;
	}

	public String getId() {
		return id;
	}

	public TemplateModel getModel() {
		return model;
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
