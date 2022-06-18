package org.condast.js.push.core;

import java.util.EventObject;

public class PushEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private long id;
	
	private IPushListener.Calls call;
	
	private long userId;
	private String token;
	private String data;
	
	public PushEvent(Object source, IPushListener.Calls call, long id, long userId, String token, String data ) {
		super(source);
		this.id = id;
		this.userId = userId;
		this.token = token;
		this.call = call;
		this.data = data;
	}

	public long getId() {
		return id;
	}

	public long getUserId() {
		return userId;
	}

	public String getToken() {
		return token;
	}

	public IPushListener.Calls getCall() {
		return call;
	}

	public String getData() {
		return data;
	}
}
