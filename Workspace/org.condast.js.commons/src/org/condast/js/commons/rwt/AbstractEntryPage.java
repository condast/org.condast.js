package org.condast.js.commons.rwt;

import java.util.Objects;

import org.condast.commons.strings.StringStyler;
import org.eclipse.rap.rwt.application.EntryPoint;

public abstract class AbstractEntryPage<E extends Enum<E>> implements IEntryPage, Comparable<E> {

    private E page;
	private final String name;

    protected AbstractEntryPage(E page) {
        this.page = page;
    	this.name = Objects.requireNonNull(page.name());
    }

    public final String name() {
        return name;
    }


    protected E getPage() {
		return page;
	}

	@Override
    public final String toPath() {
        return "/" + StringStyler.xmlStyleString(name);
    }

    @Override
    public final String getTitle() {
        return StringStyler.prettyString(name);
    }

    @Override
    public final String toString() {
        return toPath();
    }

    @Override
    public final boolean equals(IEntryPage page) {
        return page != null && toPath().equals(page.toPath());
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof AbstractEntryPage page))
            return false;

        return toPath().equals(page.toPath());
    }

    @Override
    public final int hashCode() {
        return toPath().hashCode();
    }

    protected abstract EntryPoint getEntryPoint();
    
    @Override
    public EntryPoint getEntryPoint(String domain) {
    	return  this.name.equals( domain)? getEntryPoint(): null;
    }

	@Override
	public int compareTo(E arg0) {
		return this.name.compareTo( arg0.name());
	}   
}
