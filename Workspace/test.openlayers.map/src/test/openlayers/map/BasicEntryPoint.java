package test.openlayers.map;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import test.openlayers.map.swt.OpenLayersComposite;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        Composite comp = new OpenLayersComposite(parent, SWT.NONE);
        comp.setLayoutData(new GridData( SWT.FILL, SWT.FILL, true, true ));
    }
}
