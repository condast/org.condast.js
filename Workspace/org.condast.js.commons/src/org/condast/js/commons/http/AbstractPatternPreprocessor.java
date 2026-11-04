package org.condast.js.commons.http;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.http.whiteboard.Preprocessor;

/**
 * The OSGI Filters only filter on patterns of registered servlets. If you want to put a filter on
 * other filter patterns, suchs as RAP entries, you need a preprocessor. This class mimicks a filter for these
 * patterns
 */
public abstract class AbstractPatternPreprocessor implements Preprocessor {

	public static final String S_RAP_ENTRY_FILTER = "/rap/*";

	private static final String S_ALL = "/*";
	private static final String S_PREFIX = "/*";
	private static final String S_SEPARATOR = "/";

    private static final String S_RWT_RESOURCES = "rwt-resources";

	private final String filterPattern;
	
	private boolean filterRap;

	protected AbstractPatternPreprocessor( ) {
		this( false, S_RAP_ENTRY_FILTER);
	}

	protected AbstractPatternPreprocessor( boolean filterRap ) {
		this( filterRap, S_RAP_ENTRY_FILTER);
	}

	protected AbstractPatternPreprocessor(boolean filterRap, String filterPattern) {
		this.filterPattern = Objects.requireNonNull(filterPattern);
	}

	@Override
	public void init(FilterConfig config)
			throws ServletException {
	}

	@Override
	public final void doFilter(
			ServletRequest request,
			ServletResponse response,
			FilterChain chain)
					throws IOException, ServletException {

		HttpServletRequest http = (HttpServletRequest) request;

		String path = http.getRequestURI();
		String context = http.getContextPath();

		if (!context.isEmpty() && path.startsWith(context))
			path = path.substring(context.length());
		
		//If the filter is sensitive to consecutive RAP calls, then these can be ignored
		if( this.filterRap && path.contains(S_RWT_RESOURCES)) {
			chain.doFilter(request, response);
			return;
		}
		
		HttpSession session = http.getSession(true);
		if( onCheckSession(session)) {
			chain.doFilter(request, response);
			return;			
		}
		
		boolean proceed = true;
		if (matches(path))
			proceed = onFilterRequest(request, response, chain);
		if( proceed )
			chain.doFilter(request, response);
	}

	/**
	 * Sometimes a check can be overridden if the session has already established
	 * that the call was valid. If this returns true further checking is not necessary 
	 * @param session
	 * @return
	 */
	protected boolean onCheckSession( HttpSession session ) {
		return false;
	}
	
	protected boolean matches(String path) {

		if (S_ALL.equals(filterPattern)) {
			return true;
		}

		if (filterPattern.endsWith(S_PREFIX)) {
			String prefix = filterPattern.substring(
					0, filterPattern.length() - S_PREFIX.length());

			return path.equals(prefix) ||
					path.startsWith(prefix + S_SEPARATOR);
		}

		return path.equals(filterPattern);
	}

	protected abstract boolean onFilterRequest(
			ServletRequest request,
			ServletResponse response,
			FilterChain chain)
					throws IOException, ServletException;

	@Override
	public void destroy() {
	}
}