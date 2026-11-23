package org.condast.js.commons.rwt;

import java.util.Set;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.EntryPoint;

public interface IRapApplicationProvider {

	public String getDomain();

    void configure( String domain, Application application);

	Set<IEntryPage> getPages();

	boolean hasEntryPoint(String page);

    EntryPoint getEntryPoint(String page);
}
