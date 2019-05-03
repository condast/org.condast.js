package org.openlayer.map.control;

import java.util.ArrayList;
import java.util.Collection;
import org.condast.commons.data.colours.RGBA;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.AbstractView;
import org.condast.js.commons.controller.IJavascriptController;

public class PixelView extends AbstractView<PixelView.Commands>{

	public static enum Commands{
		GET_PIXEL,
		GET_PIXELS;

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}
	}

	public PixelView( IJavascriptController controller) {
		super( controller  );
	}
	
	/**
	 * Get the pixel at the given latlng position
	 * @param latlng
	 * @return
	 */
	public int[] getPixelColour( LatLng latlng ){
		String[] params = new String[2];
		params[0] = String.valueOf( latlng.getLatitude() );
		params[1] = String.valueOf( latlng.getLongitude() );
		String query = Commands.GET_PIXEL.toString();
		Object[] results = getController().evaluate( query, params);
		if( results == null )
			return null;
		int[] coll = new int[ results.length ];
		for( int i=0; i<results.length; i++ ) {
			double data = (Double) results[i];
			coll[i] = (int)data;
		}
		return coll;		
	}

	/**
	 * Get the pixel at the given latlng position
	 * @param first
	 * @return
	 */
	public Collection<RGBA> getPixelsColour( LatLng first, LatLng last ){
		String[] params = new String[4];
		params[0] = String.valueOf( first.getLongitude() );
		params[1] = String.valueOf( first.getLatitude() );
		params[2] = String.valueOf( last.getLongitude() );
		params[3] = String.valueOf( last.getLatitude() );
		String query = Commands.GET_PIXELS.toString();
		Object[] results = getController().evaluate( query, params);
		if( results == null )
			return null;
		Collection<RGBA> rgbs = new ArrayList<>();
		for( Object result: results ) {
			rgbs.add( new RGBA((Object[])result ));
		}
		return rgbs;		
	}
}