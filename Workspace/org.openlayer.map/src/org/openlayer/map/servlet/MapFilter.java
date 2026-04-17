package org.openlayer.map.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

@Component(scope=ServiceScope.PROTOTYPE,
property= MapFilter.S_OSGI_FILTER_PATTERN)
@HttpWhiteboardFilterPattern( MapFilter.S_CONTEXT_PATH)
public class MapFilter implements Filter {

	public static final String S_CONTEXT_PATH = "/openlayer/*";
	public static final String S_OSGI_FILTER_PATTERN = "osgi.http.whiteboard.filter.pattern=" + S_CONTEXT_PATH;

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
