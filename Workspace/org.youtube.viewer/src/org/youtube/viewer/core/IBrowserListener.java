package org.youtube.viewer.core;

import org.condast.commons.strings.StringStyler;

public interface IBrowserListener {

	public enum BrowserEvents{
		UNKNOWN,
		INITIALISED,
		INTERVAL;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
	}
	
	public void notifyBrowserEvent( BrowserEvent event );
}
