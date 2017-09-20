package org.condast.js.commons.eval;

public interface IEvaluationListener<T extends Object> {

	/**
	 * These events are generated when parsing javascript/html
	 * @author Kees
	 *
	 */
	public enum EvaluationEvents{
		EVENT,
		INITIALISED,
		CHANGED,
		SUCCEEDED,
		FAILED
	}
	
	public void notifyEvaluation( EvaluationEvent<T> event );
}
