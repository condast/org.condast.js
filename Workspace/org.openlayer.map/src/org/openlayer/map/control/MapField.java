package org.openlayer.map.control;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.IJavascriptController;

public class MapField {

	public enum Commands{
		CLEAR,
		CLEAR_SHAPES,
		SET_STROKE,
		SET_STYLE,
		SET_LINE_STYLE,
		SET_FIELD,
		DRAW_LINE;

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}
	}
	
	private Field field;
	
	private IJavascriptController controller;

	public MapField( IJavascriptController controller) {
		this.controller = controller;
	}
	
	public Field getField() {
		return field;
	}

	/**
	 * Clear the interactions
	 * @return
	 */
	public String clear() {
		String query = Commands.CLEAR.toString();
		controller.setQuery( query );
		return query;				
	}
	
	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String clearShapes(){
		String query = Commands.CLEAR_SHAPES.toString();
		controller.setQuery( query );
		return query;		
	}

	public String setStroke( String colour, int width ){
		String[] params = new String[2];
		params[0] = String.valueOf( colour );
		params[1] = String.valueOf( width );
		String query = Commands.SET_STROKE.toString();
		controller.setQuery( query, params );
		return query;		
	}

	public String setStyle( int points, int length, int width, double angle ){
		String[] params = new String[4];
		params[0] = String.valueOf( points );
		params[1] = String.valueOf( length );
		params[2] = String.valueOf( width );
		params[3] = String.valueOf( angle );
		String query = Commands.SET_STYLE.toString();
		controller.setQuery( query, params );
		return query;		
	}

	public String setLineStyle( String colour, int width ){
		String[] params = new String[2];
		params[0] = colour;
		params[1] = String.valueOf( width );
		String query = Commands.SET_LINE_STYLE.toString();
		controller.setQuery( query, params );
		return query;		
	}

	public String setField( Field field ){
		this.field = field;
		if( field == null )
			return null;
		setStyle(4, (int)field.getLength(), (int)field.getWidth(), field.getAngle());
		String[] params = new String[4];
		params[0] = String.valueOf( this.field.getCentre().getLatitude() );
		params[1] = String.valueOf( this.field.getCentre().getLongitude());
		params[2] = String.valueOf( this.field.getLength() );
		params[3] = String.valueOf( this.field.getWidth());
		String query = Commands.SET_FIELD.toString();
		controller.setQuery( query, params );
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
		controller.setQuery( query, params );
		return query;
	}

	public void synchronize( int clients ){
		controller.synchronize(clients);
	}

	public void synchronize(){
		controller.synchronize();
	}
}