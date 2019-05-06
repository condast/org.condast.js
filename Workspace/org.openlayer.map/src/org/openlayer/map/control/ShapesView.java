package org.openlayer.map.control;

import java.util.Collection;

import org.condast.commons.data.latlng.IField;
import org.condast.commons.data.latlng.Polygon;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.AbstractView;
import org.condast.js.commons.controller.IJavascriptController;

public class ShapesView extends AbstractView<ShapesView.Commands> {

	public static enum Commands{
		CLEAR_SHAPES,
		SET_SHAPE,
		ADD_SHAPE,
		GET_SHAPE,
		REMOVE_SHAPE;

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}
	}

	public static enum Types{
		NONE,
		SQUARE,
		BOX,
		CIRCLE;

		@Override
		public String toString() {
			return StringStyler.prettyString( this.name());
		}
	}

	private IField field;
	
	public ShapesView( IJavascriptController controller) {
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
		return super.clear( Commands.CLEAR_SHAPES );			
	}
	
	public String setShape( String name, Types type){
		Collection<String> params = super.getParameters( Commands.SET_SHAPE);
		params.add( name );
		params.add( type.toString() );
		return super.perform(Commands.SET_SHAPE, params );
	}

	public String addShape( Polygon polygon ){
		Collection<String> params = super.getParameters( Commands.ADD_SHAPE);
		params.add( polygon.toWKT() );
		return super.perform(Commands.ADD_SHAPE, params );
	}

	public String addShape( String wtk ){
		Collection<String> params = super.getParameters( Commands.ADD_SHAPE);
		params.add( wtk );
		return super.perform(Commands.ADD_SHAPE, params );
	}

	public String getShape( String id ){
		Collection<String> params = super.getParameters( Commands.GET_SHAPE);
		params.add( id );
		return super.perform(Commands.GET_SHAPE, params );
	}

	public String removeShape( String id ){
		Collection<String> params = super.getParameters( Commands.REMOVE_SHAPE);
		params.add( id );
		return super.perform(Commands.REMOVE_SHAPE, params );
	}

}