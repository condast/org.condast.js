package org.condast.js.commons.ui.widgets.search;

import java.util.Collection;

public interface ISearchResultsComposite<T extends Object> {

	void addSelectionListener(ISearchSelectionListener<T> listener);

	void removeSelectionListener(ISearchSelectionListener<T> listener);

	Collection<T> performQuery(String query);

	Collection<T> getInput();
}