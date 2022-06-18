package org.condast.js.commons.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.condast.commons.parser.AbstractResourceParser;
import org.condast.js.commons.utils.AuthenticationData;

public abstract class AbstractFileParser {

	public static final String S_HEADER = "<!DOCTYPE html><html ";
	public static final String S_LANGUAGE = "lang='";
	public static final String S_FOOTER = "</html>";

	public static final String S_DISPOSED = "<html><h1><b>This page is Disposed</b><h1></html>";

	public static final String S_RESOURCES = "/resources/";

	private Class<?> clss;

	protected AbstractFileParser( Class<?> clss ) {
		super();
		this.clss = clss;
	}

	protected Class<?> getClss() {
		return clss;
	}

	protected abstract String onHandleContext( String context, String application, String service);

	protected String onCreateLink( String id, String type, String url ) {
		return url;
	}

	protected abstract void onHandleLinks( String link );

	protected abstract String onHandleLabel( String id, AbstractResourceParser.Attributes attr );

	protected abstract String onHandleAuthentication( String id, AuthenticationData.Authentication attr );

	protected abstract String onHandleValues( AbstractResourceParser.Functions function, String id, AbstractResourceParser.Attributes attr );

	protected abstract String onHandleFunction( AbstractResourceParser.Functions function, String id, AbstractResourceParser.Attributes attr );

	protected abstract String onHandleScript( Class<?> clss, String path );

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
		Pattern pattern = Pattern.compile( AbstractResourceParser.REGEX );
		Matcher matcher = pattern.matcher(str);

		int i=0;
		while (matcher.find()) {
			String[] split = matcher.group(1).split("[.]");

			//regular jquery
			if( split.length < 2)
				continue;

			AbstractResourceParser.Functions function = AbstractResourceParser.Functions.valueOf(split[0].toUpperCase());
			builder.append(str.substring(i, matcher.start()));
			AbstractResourceParser.Attributes attr = null;
			String path = null;
			switch( function) {
			case CONTEXT:
				if (split.length>2)
					path = split[2]; 
				builder.append( onHandleContext(split[0], split[1], path));
				break;
			case LINK:
				String replacement = "javascript:" + split[0] + "(\"" + split[1] + "\")";
				String[] decode = split[1].split("[?]");
				builder.append( onCreateLink( split[0], decode[0], replacement));
				break;
			case SCRIPT:
				path = (split.length == 2 )? split[1]: split[1] + "/" + split[2]; 
				path = S_RESOURCES + path.replace("^", ".");
				builder.append( onHandleScript( clss, path ));
				break;
			case AUTHENTICATION:
				AuthenticationData.Authentication auth = AuthenticationData.Authentication.valueOf(split[2].toUpperCase());
				builder.append( onHandleAuthentication(split[1], auth ));
				break;
			case LABEL:
				attr = AbstractResourceParser.Attributes.valueOf(split[2].toUpperCase());
				builder.append( onHandleLabel(split[1], attr ));
				break;
			case VALUE:
				attr = AbstractResourceParser.Attributes.valueOf(split[2].toUpperCase());
				builder.append( onHandleValues(function, split[1], attr ));
				break;
			default:
				if( split.length > 2)
					attr = AbstractResourceParser.Attributes.valueOf(split[2].toUpperCase());
				builder.append( onHandleFunction(function, split[1], attr ));
				break;
			}
			i = matcher.end();
		}
		builder.append(str.subSequence(i, str.length()));
		return builder.toString();	
	}
}