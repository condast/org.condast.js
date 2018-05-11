package test.org.condast.openlayer;

import org.condast.commons.ui.xml.XMLFactoryBuilder;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	public static final String S_CUSTOM = "custom";
	
	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new FillLayout());       
        XMLFactoryBuilder builder = new XMLFactoryBuilder( parent, this.getClass());
        builder.build();
         Composite root = builder.getRoot();
		root.setData( RWT.CUSTOM_VARIANT, S_CUSTOM );
    }
}
