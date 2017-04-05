package org.condast.js.react.controller;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.controller.AbstractJavascriptController;
import org.eclipse.swt.browser.Browser;

public class ReactController extends AbstractJavascriptController{

	public static final String S_INDEX_HTML = "/react/index.html";
	public static final String S_INITIALISTED_ID = "ReactInitialisedId";
	public static final String S_IS_INITIALISTED = "isInitialised";

	public static final String S_COMMENT_LINE = "//";
	public static final String S_COMMENT_START = "/*";
	public static final String S_COMMENT_END = "*/";
	public static final String S_SCRIPT_START = "<script>";
	public static final String S_SCRIPT_END = "</script>";
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public ReactController( Browser browser ) {
		super( browser, S_INITIALISTED_ID, S_INDEX_HTML );
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

}
