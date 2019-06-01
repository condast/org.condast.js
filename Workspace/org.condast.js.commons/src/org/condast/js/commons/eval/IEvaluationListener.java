package org.condast.js.commons.eval;

import org.condast.commons.strings.StringStyler;

public interface IEvaluationListener<T extends Object> {

	/**
	 * These events are generated when parsing javascript/html
	 * @author Kees
	 *
	 */
	public enum EventTypes{
		DRAWEND,
		ADD_SHAPE,
		ADDEND_SHAPE,
		SELECTED;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(super.toString());
		}
	}

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
