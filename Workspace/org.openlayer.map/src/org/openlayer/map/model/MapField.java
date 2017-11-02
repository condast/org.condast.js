package org.openlayer.map.model;

import org.condast.commons.latlng.Field;
import org.condast.commons.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.IJavascriptController;

public class MapField {

	public enum Commands{
		SET_STROKE,
		SET_STYLE,
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
		
	public String setField( Field field ){
		this.field = field;
		String[] params = new String[4];
		params[0] = String.valueOf( this.field.getCoordinates().getLatitude() );
		params[1] = String.valueOf( this.field.getCoordinates().getLongitude());
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

	public void synchronize(){
		controller.executeQuery();
	}
}