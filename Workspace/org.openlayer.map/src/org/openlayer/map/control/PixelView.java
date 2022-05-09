package org.openlayer.map.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.condast.commons.Utils;
import org.condast.commons.data.colours.RGBA;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.plane.IField;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.AbstractView;
import org.condast.js.commons.controller.IJavascriptController;

public class PixelView extends AbstractView<PixelView.Commands>{

	public static final int NO_RESOLUTION = 1;
	public static final int DEFAULT_RESOLUTION = 3;
	
	public static enum Commands{
		GET_LOCATION,
		GET_PIXEL,
		GET_PIXELS,
		GET_AREA_PIXELS,
		GET_AREA_PIXELS_WITH_OFFSET,
		GET_AREA_PIXELS_ROTATION,
		GET_AREA_PIXELS_WITH_ANGLE,
		GET_SITUATIONAL_AWARENESS;

		public CommandTypes getCommandType() {
			CommandTypes type = CommandTypes.SEQUENTIAL;
			switch( this ) {
			default:
				break;
			}
			return type;
		}

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}
	}

	public PixelView( IJavascriptController controller) {
		super( controller  );
	}

	@Override
	protected CommandTypes getCommandType(Commands command) {
		return command.getCommandType();
	}


	/**
	 * Get the pixel at the given latlng position
	 * @param latlng
	 * @return
	 */
	public LatLng getLocation(){
		String query = Commands.GET_LOCATION.toString();
		Object[] results = getController().evaluate( query);
		if( results == null )
			return null;
		double[] coll = new double[ results.length ];
		for( int i=0; i<results.length; i++ ) {
			double data = (Double) results[i];
			coll[i] = (int)data;
		}
		return new LatLng( coll[0], coll[1]);
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
	 * Get the pixels between the two locations
	 * @param first
	 * @return
	 */
	public List<RGBA> getPixelsColours( LatLng first, LatLng last ){
		String[] params = new String[4];
		params[0] = String.valueOf( first.getLongitude() );
		params[1] = String.valueOf( first.getLatitude() );
		params[2] = String.valueOf( last.getLongitude() );
		params[3] = String.valueOf( last.getLatitude() );
		String query = Commands.GET_PIXELS.toString();
		Object[] results = getController().evaluate( query, params);
		if( results == null )
			return null;
		List<RGBA> rgbs = new ArrayList<>();
		for( Object result: results ) {
			rgbs.add( new RGBA((Object[])result ));
		}
		return rgbs;		
	}

	/**
	 * Get the pixels between the two locations
	 * @param first
	 * @return the distance to the first different colour
	 */
	public int hasSingleColour( LatLng first, LatLng last, int value ){
		List<RGBA> colours = getPixelsColours(first, last);
		if( Utils.assertNull(colours))
			return -1;
		RGBA current = null;
		int distance = -1;
		for( RGBA rgba: colours) {
			if( current == null ) {
				current = rgba;
				continue;
			}
			if( current.approximate(rgba, value))
				continue;
			double total = LatLngUtils.distance(first, last);
			int location = (int) (colours.indexOf(rgba)*total/colours.size());
			return location;
		}
		return distance;
	}

	/**
	 * Get the pixels for the given field
	 * @param first
	 * @return
	 */
	public Map<Integer, List<RGBA>> getPixelsColours( IField field ){
		return getPixelsColours( field.getCoordinates(), field.getLength(), field.getWidth());
	}

	/**
	 * Get the pixels for the given field
	 * @param first
	 * @return
	 */
	public Object[] getSituationalAwareness( LatLng first, long length ){
		return getSituationalAwareness(first, length, DEFAULT_RESOLUTION );
	}
	
	/**
	 * Get the pixels for the given field
	 * @param first
	 * @return
	 */
	public Object[] getSituationalAwareness( LatLng first, long length, int resolution ){
		String[] params = new String[4];
		params[0] = String.valueOf( first.getLongitude() );
		params[1] = String.valueOf( first.getLatitude() );
		params[2] = String.valueOf( length );
		params[3] = String.valueOf( resolution );
		String query = Commands.GET_SITUATIONAL_AWARENESS.toString();
		return getController().evaluate( query, params);
	}

	/**
	 * Get the pixels for the given field
	 * @param first
	 * @return
	 */
	public Map<Integer, List<RGBA>> getPixelsColours( LatLng first, long length, long width ){
		String[] params = new String[4];
		params[0] = String.valueOf( first.getLongitude() );
		params[1] = String.valueOf( first.getLatitude() );
		params[2] = String.valueOf( length );
		params[3] = String.valueOf( width );
		String query = Commands.GET_AREA_PIXELS.toString();
		Object[] results = getController().evaluate( query, params);
		if( results == null )
			return null;
		return getRadar( length, results);		
	}

	/**
	 * Get the pixels for the given field
	 * @param first
	 * @return
	 */
	public Map<Integer, List<RGBA>> getPixelsColoursWithOffset( LatLng first, long length, long width ){
		String[] params = new String[4];
		params[0] = String.valueOf( first.getLongitude() );
		params[1] = String.valueOf( first.getLatitude() );
		params[2] = String.valueOf( length );
		params[3] = String.valueOf( width );
		String query = Commands.GET_AREA_PIXELS_WITH_OFFSET.toString();
		Object[] results = getController().evaluate( query, params);
		if( results == null )
			return null;
		return getRadar( length, results);		
	}

	/**
	 * Get the pixels for a rotated view
	 * @param first
	 * @return
	 */
	public Object[] getPixelsColoursWithRotation( LatLng first, long length, long width, int resolution ){
		String[] params = new String[5];
		params[0] = String.valueOf( first.getLongitude() );
		params[1] = String.valueOf( first.getLatitude() );
		params[2] = String.valueOf( length );
		params[3] = String.valueOf( width );
		params[4] = String.valueOf( resolution );
		String query = Commands.GET_AREA_PIXELS_ROTATION.toString();
		return getController().evaluate( query, params);
	}

	/**
	 * Get the pixels for the given field
	 * @param first
	 * @return
	 */
	public Map<Integer, List<RGBA>> getPixelsColours( LatLng first, long length, long width, double angle ){
		String[] params = new String[5];
		params[0] = String.valueOf( first.getLongitude() );
		params[1] = String.valueOf( first.getLatitude() );
		params[2] = String.valueOf( length );
		params[3] = String.valueOf( width );
		params[4] = String.valueOf( angle );
		String query = Commands.GET_AREA_PIXELS_WITH_ANGLE.toString();
		Object[] results = getController().evaluate( query, params);
		return getRadar( length, results);		
	}

	/**
	 * Get the pixels for the given field
	 * @param first
	 * @return
	 */
	public static Map<Integer, List<RGBA>> getRadar( long length, Object[] results ){
		if( Utils.assertNull( results))
			return null;
		Map<Integer, List<RGBA>> radar = new TreeMap<>();
		int y=results.length;
		for( Object result: results ) {
			List<RGBA> rgbs = radar.get(y);
			if( Utils.assertNull( rgbs )) {
				rgbs = new ArrayList<>();
				radar.put(y, rgbs);
			}
			RGBA rgba = ( result == null )?new RGBA(): new RGBA((Object[])result );
			rgbs.add( rgba);
			if( rgbs.size() >= length )
				y--;
		}
		return radar;		
	}

}