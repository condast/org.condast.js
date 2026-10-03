package org.condast.js.commons.http;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

	private static final String S_X_FORWARDED_FOR = "X-Forwarded-For";
	private static final String S_UNKNOWN = "unknown";
	private static final String S_PROXY_CLIENT_IP = "Proxy-Client-IP";
	private static final String S_WL_PROXY_CLIENT_IP = "WL-" + S_PROXY_CLIENT_IP;
	private static final String S_HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
	private static final String S_HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

	/**
	 * Retrieves the client's ip-address
	 * @See: https://stackoverflow.com/questions/4678797/how-do-i-get-the-remote-address-of-a-client-in-servlet
	 * @param request
	 * @return
	 */
	public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader( S_X_FORWARDED_FOR);
        if (ip == null || ip.length() == 0 || S_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader( S_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || S_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader( S_WL_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || S_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(S_HTTP_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || S_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader( S_HTTP_X_FORWARDED_FOR);
        }
        if (ip == null || ip.length() == 0 || S_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
