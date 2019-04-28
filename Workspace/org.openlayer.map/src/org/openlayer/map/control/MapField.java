package org.openlayer.map.control;

import java.util.Collection;

import org.condast.commons.data.latlng.IField;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.AbstractView;
import org.condast.js.commons.controller.IJavascriptController;

public class MapField extends AbstractView<MapField.Commands>{
	
	//Current ideal setting (-175,-20)
	private static final int PIXEL_OFFSET_X = -125;//(more negative to the right, 175 was almost perfect
	private static final int PIXEL_OFFSET_Y = -25;//(towards 0 is higher, -25 was perfect)
	
	public static enum Commands{
		CLEAR,
		CLEAR_FIELD,
		GET_PIXEL,
		SET_STROKE,
		SET_STYLE,
		SET_LINE_STYLE,
		SET_FIELD,
		DRAW_LINE,
		DRAW_SHAPE;

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}
	}
	
	private IField field;
	
	public MapField( IJavascriptController controller) {
		super( controller );
	}
	
	public IField getField() {
		return field;
	}

	/**
	 * Clear the interactions
	 * @return
	 */
	public String clear() {
		return super.clear( Commands.CLEAR );
	}

	/**
	 * Get the pixel at the given latlng position
	 * @param latlng
	 * @return
	 */
	public int[] getPixelColour( LatLng latlng ){
		String[] params = new String[4];
		params[0] = String.valueOf( latlng.getLatitude() );
		params[1] = String.valueOf( latlng.getLongitude() );
		params[2] = String.valueOf( PIXEL_OFFSET_X );
		params[3] = String.valueOf( PIXEL_OFFSET_Y );
		String query = Commands.GET_PIXEL.toString();
		Object[] results = getController().evaluate( query, params);
		int[] coll = new int[ results.length ];
		for( int i=0; i<results.length; i++ ) {
			double data = (Double) results[i];
			coll[i] = (int)data;
		}
		return coll;		
	}

	/**
	 * Clear the shapes
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String clearField(){
		String query = Commands.CLEAR_FIELD.toString();
		getController().setQuery( query );
		return query;		
	}

	public String createColour( int[] rgba ){
		StringBuilder builder = new StringBuilder();
		builder.append("rgb");
		if( rgba.length == 4)
			builder.append("a");
		builder.append("(");
		for( int i=0; i<rgba.length; i++) {
			builder.append( rgba[i] );
			if( i< rgba.length-1 )
				builder.append( ",");
		}
		builder.append( ")" );
		return builder.toString();
	}

	public String setStroke( String colour, int width ){
		String[] params = new String[2];
		params[0] = String.valueOf( colour );
		params[1] = String.valueOf( width );
		String query = Commands.SET_STROKE.toString();
		getController().setQuery( query, params );
		return query;		
	}

	public String setStyle( int points, int length, int width, double angle, double scale ){
		String[] params = new String[5];
		params[0] = String.valueOf( points );
		params[1] = String.valueOf( length );
		params[2] = String.valueOf( width );
		params[3] = String.valueOf( angle );
		params[4] = String.valueOf( scale );
		String query = Commands.SET_STYLE.toString();
		getController().setQuery( query, params );
		return query;		
	}

	public String setLineStyle( String colour, int width ){
		String[] params = new String[2];
		params[0] = colour;
		params[1] = String.valueOf( width );
		String query = Commands.SET_LINE_STYLE.toString();
		getController().setQuery( query, params );
		return query;		
	}

	public String setField( IField field, double scale ){
		this.field = field;
		if( field == null )
			return null;
		setStyle(4, (int)field.getLength(), (int)field.getWidth(), Math.toRadians( field.getAngle() ), scale );
		Collection<String> params = super.getParameters( Commands.SET_FIELD);
		params.add( String.valueOf( this.field.getCentre().getLatitude() ));
		params.add( String.valueOf( this.field.getCentre().getLongitude() ));
		params.add( String.valueOf( this.field.getLength() ));
		params.add( String.valueOf( this.field.getWidth()));
		String query = super.perform( Commands.SET_FIELD, params );
		return query;
	}

	public String drawLine( String name, LatLng begin, LatLng end ){
		String[] params = new String[5];
		params[0] = name;
		params[1] = String.valueOf( begin.getLatitude() );
		params[2] = String.valueOf( begin.getLongitude());
		params[3] = String.valueOf( end.getLatitude());
		params[4] = String.valueOf( end.getLongitude());
		String query = Commands.DRAW_LINE.toString();
		getController().setQuery( query, params );
		return query;
	}
}