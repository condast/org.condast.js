package org.condast.js.commons.controller;

import org.condast.js.commons.eval.IEvaluationListener;

public interface IJavascriptController {

	public enum DrawTypes{
		POINT;
	}

	public enum DrawEvents{
		DRAWEND;
	}

	boolean isInitialised();

	/**
	 * Evaluate the given expression
	 * @param query
	 * @param params
	 * @return
	 */
	Object[] evaluate(String query, String[] params);

	void addEvaluationListener(IEvaluationListener<Object[]> listener);

	void removeEvaluationListener(IEvaluationListener<Object[]> listener);

	void setQuery(String function);

	void setQuery(String function, String[] params);
	
	/**
	 * Default, when synchronisation between clients is not needed
	 */
	void synchronize();
}