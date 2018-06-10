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

	void addEvaluationListener(IEvaluationListener<Object[]> listener);

	void removeEvaluationListener(IEvaluationListener<Object[]> listener);

	void setQuery(String function);

	void executeQuery();

	void setQuery(String function, String[] params);
	
	/**
	 * Multiple clients can delay execution by waiting until all the data is
	 * collected. When the given amount of clients have synchronized,
	 * then the controller will execute 
	 * @param delay
	 */
	void synchronize( int clients );

	/**
	 * Default, when synchronisation between clients is not needed
	 */
	void synchronize();
}