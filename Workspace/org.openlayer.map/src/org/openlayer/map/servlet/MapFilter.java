package org.openlayer.map.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MapFilter implements Filter {

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		/* NOTHING */
	}
	
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (isForbidden(request)){
			logger.warning("Attempting to access openlayer from: " + request.getRemoteAddr() + ": ");
			logger.warning("Local address: " + request.getLocalAddr());
                return;
        }
        else
            chain.doFilter(request, response);
    }

	/**
	 * If the first three numbers of the ip address match, then 
	 * the call is considered local
	 * @param req
	 * @return
	 */
	private boolean isForbidden( ServletRequest req ){
		String local = req.getLocalAddr();
		if( local.lastIndexOf(".") > 0){
			local = local.substring(0, local.lastIndexOf("."));
			return !req.getRemoteAddr().startsWith(local);
		}
		return !req.getRemoteAddr().equals(local);
	}

	@Override
	public void destroy() {
	  /* NOTHING */
	}
}
