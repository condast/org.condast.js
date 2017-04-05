package org.condast.js.commons.eval;

public interface IEvaluationListener<T extends Object> {

	public enum EvaluationEvents{
		EVENT,
		SUCCEEDED,
		FAILED
	}
	
	public void notifyEvaluation( EvaluationEvent<T> event );
}
