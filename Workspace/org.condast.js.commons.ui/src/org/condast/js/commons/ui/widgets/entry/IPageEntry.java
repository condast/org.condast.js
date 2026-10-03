package org.condast.js.commons.ui.widgets.entry;

import org.eclipse.rap.rwt.application.EntryPoint;

public interface IPageEntry<P extends Enum<P>> {

	P getPage();

	String getTitle();

	String getLink();

	String getClassName();

	EntryPoint getEntryPoint();

}