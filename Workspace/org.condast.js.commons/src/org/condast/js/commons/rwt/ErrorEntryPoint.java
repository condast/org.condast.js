package org.condast.js.commons.rwt;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ErrorEntryPoint extends AbstractEntryPoint {

	private static final long serialVersionUID = 1L;
	public static final String S_ILLEGAL_ACCESS = "Illegal access";

	@Override
	protected void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		Label label = new Label( parent, SWT.None );
		label.setText(S_ILLEGAL_ACCESS);
		
	}


}
