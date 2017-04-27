package org.condast.js.commons.controller;

import org.condast.js.commons.eval.IEvaluationListener;

public interface IJavascriptController {

	boolean isInitialised();

	void addEvaluationListener(IEvaluationListener<Object[]> listener);

	void removeEvaluationListener(IEvaluationListener<Object[]> listener);

	Object evaluate(String query);

}