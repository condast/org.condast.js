package org.youtube.viewer.core;

import java.util.EventObject;

import org.youtube.viewer.core.IBrowserListener.BrowserEvents;

public class BrowserEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private BrowserEvents type;
	private Object[] data;
	
	public BrowserEvent(Object arg0, BrowserEvents type, Object[] data ) {
		super(arg0);
		this.data = data;
		this.type = type;
	}

	public BrowserEvents getType() {
		return type;
	}


	public Object[] getData() {
		return data;
	}
}
