package org.condast.js.commons.session;

import java.util.EventObject;

public class SessionEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private T data;
	
	public SessionEvent(Object arg0, T data ) {
		super(arg0);
		this.data = data;
	}

	public T getData() {
		return data;
	}
}
