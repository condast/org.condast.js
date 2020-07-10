package org.condast.js.commons.wizard;

import java.io.IOException;
import java.io.InputStream;
import org.condast.js.commons.parser.AbstractFileParser;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public abstract class AbstractHtmlParser extends AbstractFileParser{

	public enum Resources{
		HEADER,
		NAVIGATION,
		INDEX,
		FOOTER;

		@Override
		public String toString() {
			return super.toString();
		}
		
		public String toFile() {
			return "/resources/" + super.toString().toLowerCase() + ".ht";
		}
	}

	private Browser browser;

	protected AbstractHtmlParser( Browser browser, Class<?> clss ) {
		super( clss );
		this.browser = browser;
		new BrowserFunction(browser, Functions.LINK.toString()) {

			@Override
			public Object function(Object[] arguments) {
				onHandleLinks((String) arguments[0]);
				return super.function(arguments);
			}	
		};
	}

	public String create( Resources resource, Class<?> clss ) throws IOException {
		InputStream in = clss.getResourceAsStream(resource.toFile());
		return (in == null )? "":parse( in);
	}
	
	public void createMainPage( Resources resource ) {
		createPage( super.getClass().getResourceAsStream( resource.toFile() ));
	}

	public void createMainPage( ) {
		this.createMainPage(Resources.INDEX);
	}

	public void createPage( InputStream in ) {
		try {
			Class<?> clss = super.getClass();
			StringBuilder builder = new StringBuilder();
			builder.append( create(Resources.HEADER, clss));
			builder.append("\n");
			builder.append("<body onload='load()'>\n");
			builder.append( create(Resources.NAVIGATION, clss));
			builder.append("\n");
			builder.append( parse( in ));
			builder.append("\n");
	        builder.append( create(Resources.FOOTER, clss));
	        builder.append("\n</body>\n</html>");
			this.browser.setText( builder.toString() );
			this.browser.requestLayout();	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setVisible(boolean visible) {
		this.browser.setVisible(visible);
	}
}
