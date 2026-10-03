package org.condast.js.commons.ui.widgets.entry;

import java.lang.reflect.Constructor;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.eclipse.rap.rwt.application.EntryPoint;

public class PageEntry<P extends Enum<P>> implements IPageEntry<P> {

	public static final String S_ENTRY_POITNT = "EntryPoint";

	private P page;
	
	private Class<P> clss;
	private String title;
	private String link;
	private String className;

	protected PageEntry( Class<P> clss, String[] split ) {
		this.clss = clss;
		String str = StringStyler.styleToEnum(split[0].trim());
		str = str.replace(";", "");
		this.page = P.valueOf(clss, str);
		this.className = ( split.length > 1)?split[1].trim(): StringStyler.prettyString( page.name()) + S_ENTRY_POITNT;
		this.title = ( split.length > 2)? split[2].trim():  StringStyler.prettyString( page.name());
		if( split.length > 3)
			this.link = split[3].trim();
	}

	@Override
	public P getPage() {
		return page;
	}

	protected void setPage(P page) {
		this.page = page;
	}

	@Override
	public String getTitle() {
		return title;
	}

	protected void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getLink() {
		return link;
	}

	@Override
	public String getClassName() {
		return className;
	}

	protected void setClassName(String className) {
		this.className = className;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EntryPoint getEntryPoint() {
		if( StringUtils.isEmpty( className ))
			return null;
		Class<EntryPoint> builderClass;
		EntryPoint bentryPoint = null;
		try {
			builderClass = (Class<EntryPoint>) clss.getClassLoader().loadClass( className );
			Constructor<EntryPoint> constructor = builderClass.getConstructor();
			bentryPoint = constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bentryPoint;
	}
}