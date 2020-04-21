package org.condast.js.commons.wizard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public abstract class AbstractHtmlWizard {

	public static final String S_HEADER = "<!DOCTYPE html><html ";
	public static final String S_LANGUAGE = "lang='";
	public static final String S_FOOTER = "</html>";

	public static final String S_DISPOSED = "<html><h1><b>This page is Disposed</b><h1></html>";

	public static final String REGEX = "\\$\\{(.+?)\\}";

	public enum Functions{
		LINK,
		LABEL,
		VALUE;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}	
	}

	public enum Attributes{
		MIN,
		MAX;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}	
	}

	private String header;
	private String footer;
	private Browser browser;

	public AbstractHtmlWizard( Browser browser ) {
		this( browser, Locale.ENGLISH);
	}

	public AbstractHtmlWizard( Browser browser, Locale locale ) {
		this( browser, locale.getLanguage());
	}

	public AbstractHtmlWizard( Browser browser, String language ) {
		this( browser, S_HEADER + S_LANGUAGE + language + "'>", S_FOOTER);
	}

	public AbstractHtmlWizard( Browser browser, String header, String footer ) {
		super();
		this.header = header;
		this.footer = footer;
		this.browser = browser;
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

	protected abstract String onHandleValues( Functions function, String id, Attributes attr );

	public void createHeader( InputStream in ) throws IOException {
		header = parse( in );
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
		builder.append( header);
		builder.append("\n");
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
        	case LABEL:
        		attr = Attributes.valueOf(split[2].toUpperCase());
        		builder.append( onHandleLabel(split[1], attr ));
        		break;
        	case VALUE:
        		attr = Attributes.valueOf(split[2].toUpperCase());
        		builder.append( onHandleValues(function, split[1], attr ));
        		break;
        	default:
        		break;
        	}
        	i = matcher.end();
        }
        builder.append(str.subSequence(i, str.length()));
        builder.append("\n");
        builder.append( footer);
		return builder.toString();	
	}

	public void createPage( String str ) throws IOException {
		this.browser.setText( str );
		this.browser.requestLayout();	
	}

	public void createPage( InputStream in ) throws IOException {
		createPage( parse( in ));
	}

	public void setVisible(boolean visible) {
		this.browser.setVisible(visible);
	}
}
