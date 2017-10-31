package org.condast.js.commons.controller;

import org.condast.js.commons.eval.IEvaluationListener;

public interface IJavascriptController {

	boolean isInitialised();

	void addEvaluationListener(IEvaluationListener<Object[]> listener);

	void removeEvaluationListener(IEvaluationListener<Object[]> listener);

	Object evaluate(String query);

	void setQuery(String function);

	void executeQuery();

	void setQuery(String function, String[] params);
}