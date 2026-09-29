package org.condast.commons.js.legal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class LegalComposite extends Composite {
	private static final long serialVersionUID = 1L;

	public static final String S_TOS_URL = "/legal/TermsOfService.html";
	public static final String S_PRIVACY_URL = "/legal/Privacy.html";

	private Browser browser;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LegalComposite(Composite parent, int style) {
		super(parent, style);
		setLayout( new FillLayout());
		browser = new Browser( this, SWT.NONE );
	}

	public void setURL( String path ) {
		browser.setUrl(S_TOS_URL);		
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
