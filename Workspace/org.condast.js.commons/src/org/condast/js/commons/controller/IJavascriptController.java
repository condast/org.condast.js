package org.condast.js.commons.controller;

import org.condast.js.commons.eval.IEvaluationListener;
import org.eclipse.swt.browser.Browser;

public interface IJavascriptController {

	public enum DrawTypes{
		POINT;
	}

	public enum DrawEvents{
		DRAWEND;
	}

	boolean isInitialised();

	/**
	 * Returns true if the browser is currently visible
	 * @return
	 */
	public boolean isBrowserVisible();

	/**
	 * Returns true if the controller is disposed
	 * @return
	 */
	boolean isDisposed();

	/**
	 * Clears the pending commands 
	 */
	void clear();

	/**
	 * Evaluate the given expression
	 * @param query
	 * @param params
	 * @return
	 */
	Object[] evaluate(String query);

	/**
	 * Evaluate the given expression
	 * @param query
	 * @param params
	 * @return
	 */
	Object[] evaluate(String query, String[] params);

	void addEvaluationListener(IEvaluationListener<Object> listener);

	void removeEvaluationListener(IEvaluationListener<Object> listener);

	void setQuery(String function);

	void setQuery(String function, String[] params);
	
	/**
	 * Default, when synchronisation between clients is not needed
	 */
	void synchronize();

	/**
	 * returns true if the controller has not pening commands
	 * @return
	 */
	boolean isEmpty();

	/**
	 * Get the widget
	 * @return
	 */
	Browser getBrowser();
}