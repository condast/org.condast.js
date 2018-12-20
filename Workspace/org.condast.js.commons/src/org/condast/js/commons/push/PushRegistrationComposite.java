package org.condast.js.commons.push;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class PushRegistrationComposite extends Composite {
	private static final long serialVersionUID = 1L;

	public static final String S_RESOURCE = "/resources/index.html";
	
	public enum Fields{
		SERVICE_WORKER,
		SERVER_REGISTRATION;

		@Override
		public String toString() {
			return super.toString();
		}	
	}
	
	private Browser browser;
	
	/**
	 * A push registration consists of two activities:
	 * 1: register a service worker in the browser
	 * 2: register the subscription to your server
	 */
	private String serviceWorker;
	private String serverRegistration;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @throws IOException 
	 */
	public PushRegistrationComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(parent, style);
	}

	public void createComposite( Composite parent, int style ){
		this.setLayout(new FillLayout());
		browser = new Browser( this, SWT.NONE );
	}
		
	public String getServiceWorker() {
		return serviceWorker;
	}

	public void setServiceWorker(String serviceWorker) {
		this.serviceWorker = serviceWorker;
	}

	public void setInput( String path ) {
		String result = parseFile(S_RESOURCE);
		browser.setText( result );				
	}

	public void setUrl( String url ) {
		browser.setUrl(url );				
	}

	
	public String getServerRegistration() {
		return serverRegistration;
	}

	public void setServerRegistration(String serverRegistration) {
		this.serverRegistration = serverRegistration;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents sub-classing of SWT components
	}

	protected String parseFile( String path ) {
		Scanner scanner = new Scanner( this.getClass().getResourceAsStream(path));
		try {
			StringBuffer buffer = new StringBuffer();
			while( scanner.hasNextLine()) {
				String line = scanner.nextLine();
				int start = line.indexOf("${");
				if( start < 0 ) {
					buffer.append(line +"\n");
					continue;
				}
				int end = line.indexOf("}");
				String identifier = line.substring(start+2, end);
				Fields field = Fields.valueOf(identifier.toString() );
				CharSequence replace = null;
				switch( field ) {
				case SERVICE_WORKER:
					replace = this.serviceWorker;
					break;
				case SERVER_REGISTRATION:
					replace = this.serverRegistration;
					break;
				}
				CharSequence source = line.substring(start, end+1);
				line = line.replace(source, replace);
				buffer.append(line +"\n");
			}
			return buffer.toString();
		}
		finally {
			scanner.close();
		}
	}
}
