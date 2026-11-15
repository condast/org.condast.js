package org.condast.js.commons.rwt;

import java.util.Set;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.EntryPoint;

public interface IRapApplicationProvider {

	public String getDomain();

	Set<IEntryPage> getPages();

    void configure( String domain, Application application);

    EntryPoint getEntryPoint(String page);

	boolean hasEntryPoint(String page);
}
