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

/*
	protected String parse( String str ) throws IOException {
		StringBuilder builder = new StringBuilder();
        Pattern pattern = Pattern.compile( REGEX );
        Matcher matcher = pattern.matcher(str);

        int i=0;
        while (matcher.find()) {
        	String[] split = matcher.group(1).split("[.]");
        	Functions function = Functions.valueOf(split[0].toUpperCase());
    		builder.append(str.substring(i, matcher.start()));
    		Attributes attr = null;
    		switch( function) {
        	case CONTEXT:
        		builder.append( onHandleContext(split[0], split[1], split[2]));
        		builder.append("/");
        		break;
        	case LINK:
        		String replacement = "javascript:" + split[0] + "(\"" + split[1] + "\")";
        		String[] decode = split[1].split("[?]");
        		builder.append( onCreateLink( split[0], decode[0], replacement));
        		break;
        	case SCRIPT:
        		String path = (split.length == 2 )? split[1]: split[1] + "/" + split[2]; 
        		path = S_RESOURCES + path.replace("^", ".");
         		builder.append( onHandleScript( super.getClass(), path ));
        		break;
        	case AUTHENTICATION:
        		AuthenticationData.Authentication auth = AuthenticationData.Authentication.valueOf(split[2].toUpperCase());
        		builder.append( onHandleAuthentication(split[1], auth ));
        		break;
        	case LABEL:
        		attr = Attributes.valueOf(split[2].toUpperCase());
        		builder.append( onHandleLabel(split[1], attr ));
        		break;
        	case VALUE:
        		attr = Attributes.valueOf(split[2].toUpperCase());
        		builder.append( onHandleValues(function, split[1], attr ));
        		break;
        	default:
        		attr = Attributes.valueOf(split[2].toUpperCase());
        		builder.append( onHandleFunction(function, split[1], attr ));
        		break;
        	}
        	i = matcher.end();
        }
        builder.append(str.subSequence(i, str.length()));
		return builder.toString();	
	}
*/
	
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
