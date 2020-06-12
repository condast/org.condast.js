package org.condast.js.push.ui;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class PushWidget extends Browser {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PushWidget(Composite parent, int style) {
		super(parent, style);
		super.setVisible(false);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
