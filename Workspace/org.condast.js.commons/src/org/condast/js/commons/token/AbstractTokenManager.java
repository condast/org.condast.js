package org.condast.js.commons.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractTokenManager implements Closeable, ITokenManager{

	private Set<Long> tokens;
	
	protected AbstractTokenManager() {
		tokens = new TreeSet<>();
	}

	@Override
	public long request() {
		long seed = Long.MAX_VALUE/2;
		Random random = new Random(seed);
		long token = seed + random.nextLong(seed );
		tokens.add(token);
		return token;
	}

	/**
	 * Request a token from the servlet session 
	 * @param request
	 * @return
	 */
	@Override
	public long retrieve( HttpServletRequest request ) {
		HttpSession session = request.getSession();
		Object tkn = session.getAttribute(S_TOKEN);
		return (tkn != null )?(long) tkn: -1;
	}
	
	@Override
	public boolean check( long token ){
		return tokens.contains(token);
	}
	
	@Override
	public boolean clear( long token ) {
		return tokens.remove(token);
	}

	@Override
	public void close() throws IOException {
		this.tokens.clear();
	}
}
