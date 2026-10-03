package org.condast.js.commons.ui.widgets.field;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.image.AbstractImages;
import org.condast.js.commons.ui.Activator;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @see: https://www.iconfinder.com/savlon
 * @author Condast
 *
 */
public class FieldImages extends AbstractImages{

	public static final String S_DOUBLE_ARROW = "double-arrow-";
	private static final String S_CONNECTED_IMAGE = "ledorange-32.png";
	private static final String S_DISCONNECTED_IMAGE = "ledlightblue-32.png";

	public static final String S_ZOOM_IN = "zoom-in";
	public static final String S_ZOOM_OUT = "zoom-out";

	public enum Images{
		CHECK,
		CONNECTED,
		DISCONNECTED,
		UP,
		DOWN,
		LEFT,
		RIGHT,
		MENU,
		SETTINGS,
		ZOOM_IN,
		ZOOM_OUT;

		public static String getResource( Images image ){
			return getResource( image, ImageSize.NORMAL );
		}

		public static String getResource( Images image, ImageSize isize ){
			StringBuilder builder = new StringBuilder();
			builder.append(ImageSize.getFolder(isize));
			switch( image ){
			case CONNECTED:
				builder.append( S_CONNECTED_IMAGE );
				break;
			case DISCONNECTED:
				builder.append( S_DISCONNECTED_IMAGE );
				break;
			case UP:
				builder.append( S_DOUBLE_ARROW );
				builder.append( StringStyler.xmlStyleString(image.name()));
				builder.append("-" );
				builder.append( isize.getSize() );
				builder.append(".png" );
				break;
			case LEFT:
				builder.append( S_DOUBLE_ARROW );
				builder.append( StringStyler.xmlStyleString(image.name()));
				builder.append("-" );
				builder.append( isize.getSize() );
				builder.append(".png" );
				break;
			case DOWN:
				builder.append( S_DOUBLE_ARROW );
				builder.append( StringStyler.xmlStyleString(image.name()));
				builder.append("-" );
				builder.append( isize.getSize() );
				builder.append(".png" );
				break;
			case RIGHT:
				builder.append( S_DOUBLE_ARROW );
				builder.append( StringStyler.xmlStyleString(image.name()));
				builder.append("-" );
				builder.append( isize.getSize() );
				builder.append(".png" );
				break;
			case MENU:
				builder.append( StringStyler.xmlStyleString(image.name()));
				builder.append( "-");
				builder.append("-" );
				builder.append( isize.getSize() );
				builder.append(".png" );
				break;
			default:
				builder.append( StringStyler.xmlStyleString(image.name()));
				builder.append("-" );
				builder.append( isize.getSize() );
				builder.append(".png" );
				break;
			}
			return builder.toString();
		}
	}

	private static Logger logger = Logger.getLogger( FieldImages.class.getName() );

	public FieldImages() {
		super( S_RESOURCES, Activator.BUNDLE_ID);
	}

	@Override
	public void initialise(){
		for( Images image: Images.values())
			setImage( image.toString() );
	}

	public Image getImage( Images image ){
		return super.getImageFromName( Images.getResource( image ));
	}

	protected void setImage( Images image ){
		super.setImage( Images.getResource(image));
	}

	/**
	 * Register the resource with the given name
	 * @param name
	 */
	public static void registerImage( Images image ){
		registerImage( image.name().toLowerCase(), Images.getResource(image));
	}

	/**
	 * Register the resource with the given name
	 * @param name
	 */
	public static void registerImage( String name, String file ){
		ResourceManager resourceManager = RWT.getResourceManager();
		if( !resourceManager.isRegistered( name ) ) {
			InputStream inputStream = FieldImages.class.getClassLoader().getResourceAsStream( file );
			try {
				resourceManager.register( name, inputStream );
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.log( Level.SEVERE, name + ": " + file );
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Get the image with the given name
	 * @param name
	 * @return
	 */
	public static String getImageString( Images image ){
		return Images.getResource(image);
	}

	/**
	 * Set the image for the given control
	 * @param widget
	 * @param name
	 */
	public static void setImage( Control widget, Images image ){
		widget.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		//registerImage( image );
		if( widget instanceof Label ){
			  Label label = (Label) widget;
			  String src = getImageString( image );
			  label.setText( "Hello<img width='24' height='24' src='" + src + "'/> there " );
			}
		if( widget instanceof Button ){
		  Button button = (Button) widget;
		  String src = getImageString( image );
		  button.setText( "<img width='24' height='24' src='" + src + "'/>" );
		}
	}
}
