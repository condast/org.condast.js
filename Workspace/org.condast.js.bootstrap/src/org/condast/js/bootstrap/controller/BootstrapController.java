package org.condast.js.bootstrap.controller;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.controller.AbstractJavascriptController;
import org.eclipse.swt.browser.Browser;

public class BootstrapController extends AbstractJavascriptController{

	public static final String S_INDEX_HTML = "/resources/minimal.html";
	public static final String S_BARE_HTML = "/resources/minimal2.html";

	public static final String S_INITIALISTED_ID = "BootstrapInitialisedId";
	public static final String S_IS_INITIALISTED = "isInitialised";

	public static final String S_COMMENT_LINE = "//";
	public static final String S_COMMENT_START = "/*";
	public static final String S_COMMENT_END = "*/";
	public static final String S_SCRIPT_START = "<script>";
	public static final String S_SCRIPT_END = "</script>";
	
	public enum Pages{
		MINIMAL,
		MINIMAL2,
		INDEX,
		BARE,
		BLOG,
		CAROUSEL,
		COVER,
		DASHBOARD,
		GRID,
		JUMBOTRON,
		JUMBOTRON_NARROW,
		NAVBAR,
		NAVBAR_FIXED_TOP,
		NAVBAR_STATIC_TOP;

		@Override
		public String toString() {
			String str = "/resources/" + name().toLowerCase(); 
			str = str.replaceAll("_", "-");
			if( ordinal() < BLOG.ordinal() )
				str += ".html";
			else
				str += "/index.html";
			return str;
		}

		public static String[] getItems(){
			String[] items = new String[ values().length ];
			for( int i=0; i< items.length; i++ ){
				items[i] = Pages.values()[i].name();
			}
			return items;
		}
	}
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public BootstrapController( Browser browser ) {
		super( browser, S_INITIALISTED_ID );
	}

	public void setBrowser( Pages page ) {
		super.setBrowser( BootstrapController.class.getResourceAsStream( page.toString() ));
	}

	@Override
	protected void onLoadCompleted() {
		logger.info("COMPLETED");
	}

	public void addWidgets( InputStream in ) {
		super.getBrowser().evaluate( parse(in));
	}

	public void render( InputStream in ) {
		super.getBrowser().evaluate( parse(in));
	}

	protected String parse( InputStream in ) {
		Scanner scanner = new Scanner( in );
		StringBuffer buffer = new StringBuffer();
		boolean commentBlock = false;
		boolean script = false;
		try{
			while( scanner.hasNextLine() ){
				String line = scanner.nextLine().trim();
				//Check script start and end
				if( line.startsWith( S_SCRIPT_START )){
					script = true;
					continue;
				}else if( line.startsWith( S_SCRIPT_END )){
					script = false;
					continue;
				}
				if(!script) continue;
				
				//Check comments
				if( line.startsWith( S_COMMENT_START )){
					commentBlock = true;
					continue;
				}else if ( commentBlock ){
					if( line.contains(S_COMMENT_END)){
						commentBlock = false;
						String str = line.substring( line.indexOf( S_COMMENT_END) + 2);
						if( !StringUtils.isEmpty(str))
							buffer.append(str);
					}
					continue;
				}
				if(commentBlock) continue;

				if(!line.startsWith( S_COMMENT_LINE ))
					buffer.append( line );
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		finally{
			scanner.close();
		}
		logger.info( buffer.toString());
		return buffer.toString();
	}
	
	public void setPage( Pages page ){
		setBrowser( LoadTypes.TEXT, page.toString());
	}

}
