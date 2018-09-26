package test.openlayers.map;

import java.io.InputStream;
import java.util.Scanner;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	//private static final String S_INDEX_LOCATION = "/openlayer1/index.html";
	private static final String S_INDEX_LOCATION = "/resources/index.html";
	
	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        Browser browser = new Browser(parent, SWT.NONE);
        //browser.setUrl( S_INDEX_LOCATION );
        browser.setText( readInput(getClass().getResourceAsStream(S_INDEX_LOCATION)));
        browser.setLayoutData(new GridData( SWT.FILL, SWT.FILL, true, true ));
    }
	
	protected String readInput( InputStream in ){
		StringBuffer buffer = new StringBuffer();
		Scanner scanner = new Scanner( in );
		try{
		while( scanner.hasNextLine() )
			buffer.append( scanner.nextLine() );
		}
		finally{
			scanner.close();
		}
		return buffer.toString();
	}

}
