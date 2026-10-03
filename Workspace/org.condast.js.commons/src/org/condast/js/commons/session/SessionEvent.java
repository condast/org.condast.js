package org.condast.js.commons.session;

import java.util.EventObject;

public class SessionEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private T data;
	
	private ISessionListener.EventTypes type;

	public SessionEvent(Object arg0 ) {
		super(arg0);
	}

	public SessionEvent(Object arg0, T data ) {
		this( arg0, ISessionListener.EventTypes.UPDATE, data );
	}
	
	public SessionEvent(Object arg0, ISessionListener.EventTypes type, T data ) {
		super(arg0);
		this.type = type;
		this.data = data;
	}

	public ISessionListener.EventTypes getType() {
		return type;
	}

	public T getData() {
		return data;
	}
}
