package org.condast.js.commons.ui.utils;

import java.io.IOException;

import org.condast.commons.strings.StringUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;

public class RWTUtils {

	/**
	 * Redirect the
	 * @param url
	 * @return
	 */
	public static boolean reload(){
		Client client = RWT.getClient();
		JavaScriptExecutor executor = client.getService( JavaScriptExecutor.class );
		if( executor == null  )
			return false;
		executor.execute( "window.reload();" );
		return true;
	}

	/**
	 * Redirect the
	 * @param url
	 * @return
	 */
	public static String createURL( String url ){
		return "window.location = \"" + url + "\";";
	}

	/**
	 * Redirect the
	 * @param url
	 * @return
	 */
	public static boolean redirect( String url ){
		String path = createURL(url);
		if( StringUtils.isEmpty(path))
			return false;
		Client client = RWT.getClient();
		JavaScriptExecutor executor = client.getService( JavaScriptExecutor.class );
		if( executor == null  )
			return false;
		executor.execute( path );
		return true;
	}

	/**
	 * Redirect the
	 * @param url
	 * @return
	 */
	public static boolean redirect( String url, long token ){
		Client client = RWT.getClient();
		JavaScriptExecutor executor = client.getService( JavaScriptExecutor.class );
		if( executor == null  )
			return false;
		executor.execute( "window.location = \"" + url + "\";" );
		return true;
	}

	/**
	 * Redirect the
	 * @param url
	 * @return
	 */
	public static boolean forward( String url ){
		try {
			RWT.getResponse().sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Redirect the
	 * @param url
	 * @return
	 */
	public static boolean jump( String url ){
		Client client = RWT.getClient();
		JavaScriptExecutor executor = client.getService( JavaScriptExecutor.class );
		if( executor == null  )
			return false;
		executor.execute( "parent.window.location.href=\"" + url + "\";" );
		return true;
	}


	/**
	 * Redirect the
	 * @param url
	 * @return
	 */
	public static boolean jump( String url, long token, String domain, long userId, long security ){
		Client client = RWT.getClient();
		StringBuilder builder = new StringBuilder();
		builder.append(url);
		builder.append("?");
		builder.append("domain=");
		builder.append(domain);
		builder.append("&token=");
		builder.append(token);
		builder.append("&userid=");
		builder.append(userId);
		builder.append("&security=");
		builder.append(security);
		JavaScriptExecutor executor = client.getService( JavaScriptExecutor.class );
		if( executor == null  )
			return false;
		executor.execute( "parent.window.location.href=\"" +builder.toString() + "\";" );
		return true;
	}

}
