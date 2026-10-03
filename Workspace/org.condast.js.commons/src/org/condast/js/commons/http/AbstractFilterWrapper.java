package org.condast.js.commons.http;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class AbstractFilterWrapper implements Filter {

	public static final String S_CONTEXT_PATH = "rest";

	public static final String S_FILTER_MSG = "Filtering REST: ";

	Filter servletContainer;

	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	protected AbstractFilterWrapper() {
		this( S_CONTEXT_PATH );
	}

	/**
	 * The context path should be the same as the alias in plugin.xml.
	 * In that case the servlet will start in:
	 * http://{url}:{port}/{context-path}/...
	 * @param contextPath
	 */
	protected AbstractFilterWrapper( String contextPath ) {
		servletContainer = this.onCreateFilter(contextPath);
	}

	protected abstract Filter onCreateFilter( String contextPath );

	@Override
	public void destroy() {
		servletContainer.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		logger.info( S_FILTER_MSG + request.getRemoteAddr());
	}
}
