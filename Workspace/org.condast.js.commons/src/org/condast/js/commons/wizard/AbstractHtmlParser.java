package org.condast.js.commons.wizard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public abstract class AbstractHtmlParser {

	public enum Resources{
		HEADER,
		NAVIGATION,
		INDEX,
		FOOTER;

		@Override
		public String toString() {
			return super.toString();
		}
		
		public String toFile() {
			return "/resources/" + super.toString().toLowerCase() + ".ht";
		}
	}

	public static final String S_HEADER = "<!DOCTYPE html><html ";
	public static final String S_LANGUAGE = "lang='";
	public static final String S_FOOTER = "</html>";

	public static final String S_DISPOSED = "<html><h1><b>This page is Disposed</b><h1></html>";

	public static final String S_RESOURCES = "/resources/";

	public static final String REGEX = "\\$\\{(.+?)\\}";

	public enum Functions{
		LINK,
		LABEL,
		VALUE,
		AUTHENTICATION,
		SCRIPT;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}	
	}

	public enum Authentication{
		ID,
		TOKEN,
		IDENTIFIER;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}		
	}

	public enum Attributes{
		MIN,
		MAX,
		AUTHENTICATION;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}	
	}

	private Browser browser;
	private Class<?> clss;

	public AbstractHtmlParser( Browser browser, Class<?> clss ) {
		super();
		this.browser = browser;
		this.clss = clss;
		new BrowserFunction(browser, Functions.LINK.toString()) {

			@Override
			public Object function(Object[] arguments) {
				onHandleLinks((String) arguments[0]);
				return super.function(arguments);
			}	
		};
	}

	protected abstract void onHandleLinks( String link );

	protected abstract String onHandleLabel( String id, Attributes attr );

	protected abstract String onHandleAuthentication( String id, Authentication attr );

	protected abstract String onHandleValues( Functions function, String id, Attributes attr );

	protected abstract String onHandleFunction( Functions function, String id, Attributes attr );

	protected abstract String onHandleScript( Class<?> clss, String path );

	public String create( Resources resource, Class<?> clss ) throws IOException {
		InputStream in = clss.getResourceAsStream(resource.toFile());
		return (in == null )? "":parse( in);
	}

	public String parse( InputStream in ) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return parse( result.toString("UTF-8"));
	}

	protected String parse( String str ) throws IOException {
		StringBuilder builder = new StringBuilder();
        Pattern pattern = Pattern.compile( REGEX );
        Matcher matcher = pattern.matcher(str);

        int i=0;
        while (matcher.find()) {
        	String[] split = matcher.group(1).split("[.]");
        	Functions function = Functions.valueOf(split[0].toUpperCase());
    		builder.append(str.substring(i, matcher.start()));
    		Attributes attr = null;
    		switch( function) {
        	case LINK:
        		String replacement = "javascript:" + split[0] + "(\"" + split[1] + "\")";
        		builder.append(replacement);
        		break;
        	case SCRIPT:
        		String path = (split.length == 2 )? split[1]: split[1] + "/" + split[2]; 
        		path = S_RESOURCES + path.replace("^", ".");
         		builder.append( onHandleScript( clss, path ));
        		break;
        	case AUTHENTICATION:
        		Authentication auth = Authentication.valueOf(split[2].toUpperCase());
        		builder.append( onHandleAuthentication(split[1], auth ));
        		break;
        	case LABEL:
        		attr = Attributes.valueOf(split[2].toUpperCase());
        		builder.append( onHandleLabel(split[1], attr ));
        		break;
        	case VALUE:
        		attr = Attributes.valueOf(split[2].toUpperCase());
        		builder.append( onHandleValues(function, split[1], attr ));
        		break;
        	default:
        		attr = Attributes.valueOf(split[2].toUpperCase());
        		builder.append( onHandleFunction(function, split[1], attr ));
        		break;
        	}
        	i = matcher.end();
        }
        builder.append(str.subSequence(i, str.length()));
		return builder.toString();	
	}

	public void createMainPage( Resources resource ) {
		createPage(clss.getResourceAsStream( resource.toFile() ));
	}

	public void createMainPage( ) {
		this.createMainPage(Resources.INDEX);
	}

	public void createPage( InputStream in ) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append( create(Resources.HEADER, clss));
			builder.append("\n");
			builder.append("<body onload='load()'>\n");
			builder.append( create(Resources.NAVIGATION, clss));
			builder.append( parse( in ));
			builder.append("\n");
	        builder.append( create(Resources.FOOTER, clss));
	        builder.append("</body></html>");
			this.browser.setText( builder.toString() );
			this.browser.requestLayout();	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setVisible(boolean visible) {
		this.browser.setVisible(visible);
	}
}
