package org.condast.commons.js.legal.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.condast.commons.js.legal.core.LegalData;
import org.condast.commons.legal.LegalUtils;
import org.condast.commons.legal.LegalUtils.Version;
import org.condast.commons.parser.AbstractResourceParser;
import org.condast.commons.strings.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = Servlet.class, 
scope=ServiceScope.PROTOTYPE,
property= "osgi.http.whiteboard.servlet.pattern=/legal")
public class LegalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_LOGIN = "Login";

	public static final String S_RESOURCE_FILE = "/legal/index.html";

	public LegalServlet() { /* NOTHING */ }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String type = req.getParameter(LegalUtils.Parameters.TYPE.toString());
			LegalData.Documents document = LegalData.Documents.getDocument(type);
			String domain = req.getParameter(LegalUtils.Parameters.DOMAIN.toString());
			String versionStr = req.getParameter(LegalUtils.Parameters.VERSION.toString());
			LegalUtils.Version version = StringUtils.isEmpty(versionStr)?Version.VERSION_1_0: Version.getVersion(versionStr);
			FileParser parser = new FileParser( domain );
			String path = document.toPath(Locale.getDefault(), version );
			InputStream resource =  this.getClass().getResourceAsStream( path);
			String str = parser.parse(resource );
			resp.getWriter().write( str );
		} catch (IOException e) {
			e.printStackTrace();
			resp.sendError( 0);
		}
	}

	private class FileParser extends AbstractResourceParser{

		private String domain;
		
		public FileParser(String domain) {
			super();
			this.domain = domain;
		}

		@Override
		protected String getToken() {
			return String.valueOf(-1);
		}

		@Override
		protected String onHandleTitle(String subject, Attributes attr) {
			String result = null;
			switch( attr ){
			case HTML:
				result = "Condast Mail";
				break;
			case PAGE:
				result = "Condast Mail";
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			return S_LOGIN;
		}

		@Override
		protected String onCreateLink(String link, String url, String arguments) {
			return domain;
		}

		@Override
		protected String onHandleApplication() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String onHandleBody(String body, Attributes attr) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
