package org.condast.js.commons.token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractTokenManager {

	public static String S_TOKEN = "token";
	
	private Set<Long> tokens;
	
	protected AbstractTokenManager() {
		tokens = new TreeSet<>();
	}

	public long request() {
		long seed = Long.MAX_VALUE/2;
		Random random = new Random(seed);
		long token = seed + random.nextInt();
		tokens.add(token);
		return token;
	}

	/**
	 * Request a token from the servlet session 
	 * @param request
	 * @return
	 */
	public long request( HttpServletRequest request ) {
		HttpSession session = request.getSession();
		Object tkn = session.getAttribute(S_TOKEN);
		long token = -1;
		if (tkn != null )
			token = (long) tkn;
		else {
			token = request();
			session.setAttribute( S_TOKEN, token);
		}
		return token;
	}
	
	public boolean check( long token ){
		return tokens.contains(token);
	}
	
	public boolean clear( long token ) {
		return tokens.remove(token);
	}
}
