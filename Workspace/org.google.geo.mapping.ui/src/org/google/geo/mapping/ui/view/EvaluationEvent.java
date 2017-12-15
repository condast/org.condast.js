package org.google.geo.mapping.ui.view;

import java.util.EventObject;

import org.eclipse.swt.browser.Browser;
import org.google.geo.mapping.ui.view.IEvaluationListener.EvaluationEvents;

public class EvaluationEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private EvaluationEvents ee;
	
	private T data;

	public EvaluationEvent( Browser arg0, EvaluationEvents ee ) {
		this( arg0, null, ee );
	}
	
	public EvaluationEvent( Browser arg0, T data, EvaluationEvents ee ) {
		super(arg0);
		this.ee = ee;
		this.data = data;
	}

	public T getData() {
		return data;
	}


	public EvaluationEvents getEvaluationEvent() {
		return ee;
	}
}
