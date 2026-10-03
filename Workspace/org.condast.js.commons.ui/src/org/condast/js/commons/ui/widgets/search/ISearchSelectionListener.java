package org.condast.js.commons.ui.widgets.search;

public interface ISearchSelectionListener<T extends Object> {

	/**
	 * A search selection composite can spawn the following events:
	 * - NEW; add a new entry
	 * - PREPARE: prepare
	 * - RESULTS: the results of the search were obtained
	 * @author Kees
	 *
	 */
	public enum SearchEvents{
		NEW,
		PREPARE,
		RESULTS;
	}

	public void notifySearchResults( SearchEvent<T> event );
}
