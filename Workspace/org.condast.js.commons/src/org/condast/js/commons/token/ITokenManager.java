package org.condast.js.commons.token;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public interface ITokenManager {

	public static final String S_TOKEN = "token";
	
	public static final String S_TOKEN_ARG = "?" + S_TOKEN + "=";

	
	long request();

	/**
	 * Request a token from the servlet session 
	 * @param request
	 * @return
	 */
	long retrieve(HttpServletRequest request);

	boolean check(long token);

	boolean clear(long token);

	void close() throws IOException;

}