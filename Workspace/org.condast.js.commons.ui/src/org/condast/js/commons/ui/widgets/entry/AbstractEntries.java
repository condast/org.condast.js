package org.condast.js.commons.ui.widgets.entry;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.condast.commons.strings.StringUtils;

public class AbstractEntries<P extends Enum<P>> {

	public static final String S_PAGE_RESOURCE = "/design/pages.txt";

	private Map<P,PageEntry<P>> pages;
	
	private Class<P> clss;

	protected AbstractEntries( Class<P> clss ) {
		this( clss, S_PAGE_RESOURCE);
	}

	protected AbstractEntries( Class<P> clss, String path ) {
		this( clss, clss.getResourceAsStream(path));
	}
	
	protected AbstractEntries( Class<P> clss, InputStream in ) {
		this.clss = clss;
		pages = new HashMap<>();
		Scanner scanner = new Scanner( in );
		try {
			while( scanner.hasNextLine()) {
				String line=  scanner.nextLine();
				if( StringUtils.isEmpty(line) || StringUtils.isComment(line))
					continue;
				String[] split = line.split("[|]");
				PageEntry<P> entry = new PageEntry<P>( this.clss, split);
				pages.put(entry.getPage(), entry);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			scanner.close();
		}
	}
	public IPageEntry<P> getPageEntry( P page ) {
		return this.pages.get(page);
	}
}