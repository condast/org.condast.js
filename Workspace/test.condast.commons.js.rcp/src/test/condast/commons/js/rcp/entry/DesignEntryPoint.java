package test.condast.commons.js.rcp.entry;

import org.condast.commons.ui.xml.XMLFactoryBuilder;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class DesignEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;	
		
	@Override
	protected void createContents(Composite parent) {
		parent.setLayout(new FillLayout());       
		XMLFactoryBuilder builder = new XMLFactoryBuilder( parent, this.getClass());
		builder.build();
	}
}
