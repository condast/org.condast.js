package org.condast.js;

import org.condast.js.authentication.AuthenticationManager;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;
	private AuthenticationManager manager;

	@Override
    protected void createContents(Composite parent) {
		try{
			parent.setLayout( new GridLayout(1, false));
			manager = new AuthenticationManager(); 

			manager.setParentComposite(parent);
			manager.open();		
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
    }

}
