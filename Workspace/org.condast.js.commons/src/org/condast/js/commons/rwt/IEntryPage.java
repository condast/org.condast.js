package org.condast.js.commons.rwt;

import org.eclipse.rap.rwt.application.EntryPoint;

public interface IEntryPage {

	String toString();

	String toPath();

	String getTitle();

	EntryPoint getEntryPoint(String domain );

}