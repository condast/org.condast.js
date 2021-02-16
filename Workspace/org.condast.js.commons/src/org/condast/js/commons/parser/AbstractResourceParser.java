package org.condast.js.commons.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.condast.js.commons.utils.StringUtils;

public abstract class AbstractResourceParser {

	public static final String S_HEADER = "<!DOCTYPE html><html ";
	public static final String S_LANGUAGE = "lang='";
	public static final String S_FOOTER = "</html>";

	public static final String S_DISPOSED = "<html><h1><b>This page is Disposed</b><h1></html>";

	public static final String S_RESOURCES = "/resources/";

	public static final String REGEX = "\\$\\{(.+?)\\}";

	public static final String S_LINK = "link";

	private Class<?> clss;

	protected AbstractResourceParser( Class<?> clss ) {
		super();
		this.clss = clss;
	}

	protected Class<?> getClss() {
		return clss;
	}

	protected abstract String onCreateLink( String link, String url, String arguments);

	protected String onHandleFunction( String[] split ) {
		return split[0];
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
		if( StringUtils.isEmpty(str))
			return builder.toString();

		Pattern pattern = Pattern.compile( REGEX );
		Matcher matcher = pattern.matcher(str);

		int i=0;
		while (matcher.find()) {
			String[] split = matcher.group(1).split("[.]");

			//regular jquery
			if( split.length < 2) 
				continue;
			
			//Check for a link
			builder.append(str.substring(i, matcher.start()));
			if( split[0].equals(S_LINK)) {
				String[] decode = split[1].split("[?]");
				if( decode.length >= 2 )
					builder.append( onCreateLink( split[0], decode[0], decode[1]));
				else
					builder.append( onCreateLink( split[0], decode[0], null ));					
			}else
				builder.append( onHandleFunction(split));

			i = matcher.end();
		}
		builder.append(str.subSequence(i, str.length()));
		return builder.toString();	
	}
}