package org.condast.js.commons.ui.widgets.search;

import java.util.EventObject;

import org.condast.js.commons.ui.widgets.search.ISearchSelectionListener.SearchEvents;

public class SearchEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private SearchEvents se;
	public String query;
	private T data;

	public SearchEvent(Object source, SearchEvents se ) {
		this( source, se, null, null );
	}

	public SearchEvent(Object source, T data ) {
		this( source, null, null, data );
	}

	public SearchEvent(Object source, SearchEvents se, String query ) {
		this( source, se, query, null );
	}

	public SearchEvent(Object source, String query, T data ) {
		this( source, SearchEvents.RESULTS, query, data );
	}

	public SearchEvent(Object source, SearchEvents se, T data) {
		this( source, se, null, data );
	}

	public SearchEvent(Object source, SearchEvents se, String query, T data) {
		super(source);
		this.se = se;
		this.query = query;
		this.data = data;
	}

	public SearchEvents getSearchEvent() {
		return se;
	}

	public String getQuery() {
		return query;
	}

	public T getData() {
		return data;
	}
}