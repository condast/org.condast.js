package org.condast.js.commons.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.condast.js.commons.utils.StringUtils;

public abstract class AbstractResourceParser {

	public enum Functions{
		CONTEXT,
		LINK,
		LABEL,
		VALUE,
		AUTHENTICATION,
		MAXVISIBLEACTIONS,
		SCRIPT,
		VAPID,
		WORKER;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}	
	}

	public enum Attributes{
		TITLE,
		HOME,
		CREATE,
		MIN,
		MAX,
		AUTHENTICATION,
		KEY;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}	
	}

	public static final String REGEX = "\\$\\{(.+?)\\}";

	protected AbstractResourceParser( ) {
		super();
	}

	protected abstract String onCreateLink( String link, String url, String arguments);

	protected abstract String onHandleLabel( String id, Attributes attr );

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
			
			AbstractResourceParser.Functions function = AbstractResourceParser.Functions.valueOf(split[0].toUpperCase());
			builder.append(str.substring(i, matcher.start()));
			Attributes attr = null;
			switch( function) {

			//Check for a link
			case LINK:
				String[] decode = split[1].split("[?]");
				if( decode.length >= 2 )
					builder.append( onCreateLink( split[0], decode[0], decode[1]));
				else
					builder.append( onCreateLink( split[0], decode[0], null ));	
				break;
			case LABEL:
				attr = Attributes.valueOf(split[2].toUpperCase());
				builder.append( onHandleLabel(split[1], attr ));
				break;
			default:
				builder.append( onHandleFunction( split ));
				break;
			}
			i = matcher.end();
		}
		builder.append(str.subSequence(i, str.length()));
		return builder.toString();	
	}
}