package org.google.geo.mapping.ui.controller;

public interface IControllerVisitor {

	/**
	 * Add an optional initialisation query, that will
	 * be performed immediately after setting up the 
	 * index page
	 * @return
	 */
	public String addInitQuery();
}
