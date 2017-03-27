package org.google.geo.mapping.ui.view;

import java.util.EventObject;

import org.google.geo.mapping.ui.view.IEvaluationListener.EvaluationEvents;

public class EvaluationEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private EvaluationEvents ee;
	
	private String id;
	private T data;

	public EvaluationEvent( Object arg0, String id, EvaluationEvents ee ) {
		this( arg0, id, ee, null );
	}
	
	public EvaluationEvent( Object arg0, String id, EvaluationEvents ee, T data ) {
		super(arg0);
		this.id = id;
		this.ee = ee;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public T getData() {
		return data;
	}


	public EvaluationEvents getEvaluationEvent() {
		return ee;
	}
}
