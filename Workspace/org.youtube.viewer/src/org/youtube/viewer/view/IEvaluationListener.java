package org.youtube.viewer.view;

public interface IEvaluationListener<T extends Object> {

	public enum EvaluationEvents{
		SUCCEEDED,
		FAILED
	}
	
	public void notifyEvaluation( EvaluationEvent<T> event );
}
