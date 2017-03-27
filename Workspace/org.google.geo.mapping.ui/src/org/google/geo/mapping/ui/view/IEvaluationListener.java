package org.google.geo.mapping.ui.view;

public interface IEvaluationListener<T extends Object> {

	public enum EvaluationEvents{
		EVENT,
		SUCCEEDED,
		FAILED
	}
	
	public void notifyEvaluation( EvaluationEvent<T> event );
}
