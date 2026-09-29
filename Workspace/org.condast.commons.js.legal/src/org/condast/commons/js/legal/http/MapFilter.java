//@See: http://blog.vogella.com/2017/04/20/access-osgi-services-via-web-interface/
package org.condast.commons.js.legal.http;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.condast.commons.strings.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

@Component(scope=ServiceScope.PROTOTYPE)
@HttpWhiteboardFilterPattern( MapFilter.S_LEGAL)
public class MapFilter implements Filter {

	private static final String S_LOCAL_HOST = "127.0.0.1";
	protected static final String S_LEGAL = "/legal";
	private static final String S_LEGAL_WEB = S_LEGAL + "/web";

	private static final String S_ERR_ILLEGAL_ACCESS = "This page cannot be accessed.";

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Map<String, HttpSession> addresses;

	public MapFilter() {
		super();
		addresses = new HashMap<>();
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) arg0;

		HttpServletResponse resp = (HttpServletResponse) arg1;
		resp.setHeader("Cache-Control", "private, no-store, no-cache,must-revalidate");
		resp.setHeader("Pragma", "no-cache");

		String path = req.getRequestURI();
		if( StringUtils.isEmpty(path))
			return;
		
		try {
			//Pass calls that are not for this application
			if(!path.contains(S_LEGAL)) {
				arg2.doFilter(arg0, arg1);
				return;
			}
		} catch (Exception e) {
			arg2.doFilter(arg0, arg1);
			logger.warning( e.getMessage());
			return;
		}
		
		//WORKAROUND
		String[] split = path.split("[\\[]");
		if( split.length > 1)
			path = split[0];
		
		//Filter local calls or a call to index
		String remote = req.getRemoteAddr();
		HttpSession session = null;
		if( S_LOCAL_HOST.equals( remote) || path.endsWith(S_LEGAL) ||
				path.startsWith(S_LEGAL_WEB)) {
			if(!addresses.containsKey(remote)) {
				session = req.getSession(true);
				addresses.put(remote, session);
			}
			
			arg2.doFilter(arg0, arg1);
			return;
		}

		if( path.startsWith(S_LEGAL) && addresses.containsKey(remote)) {
			arg2.doFilter(arg0, arg1);
			return;
		}

		//The request fails on the conditions described above. See if these are follow up calls,
		//and allow them if so
		session = req.getSession(false);
		if( session != null ) {
			arg2.doFilter(arg0, arg1);
			return;
		}

		String local = req.getLocalAddr();
		Writer writer = arg1.getWriter();
		String msg = S_ERR_ILLEGAL_ACCESS + "=> " + remote + ": " + path;
		writer.write(msg);
		logger.info(msg + "[" + local + "]" );
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void destroy() {
		addresses.clear();
	}

	@WebListener
	public class SessionListener implements HttpSessionListener {

	    @Override
	    public void sessionCreated(HttpSessionEvent event) {
	        logger.info("session created: ");
	    }

	    @Override
	    public void sessionDestroyed(HttpSessionEvent event) {
	    	addresses.entrySet().removeIf( e-> e.getValue().equals( event.getSession()));
			logger.info("session destroyed: " );
	    }
	}
}
