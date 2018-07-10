package org.openlayer.map.control;

import java.util.Collection;
import org.condast.commons.data.latlng.Field;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.AbstractView;
import org.condast.js.commons.controller.IJavascriptController;

public class ShapesView extends AbstractView<ShapesView.Commands> {

	public static enum Commands{
		CLEAR,
		SET_SHAPE;

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

	private Field field;
	
	public ShapesView( IJavascriptController controller) {
		super( controller );
	}
	
	public Field getField() {
		return field;
	}

	/**
	 * Clear the interactions
	 * @return
	 */
	public String clear() {
		return super.clear( Commands.CLEAR );			
	}
	
	public String setShape( String name, Types type){
		Collection<String> params = super.getParameters( Commands.SET_SHAPE);
		params.add( name );
		params.add( type.toString() );
		return super.perform(Commands.SET_SHAPE, params );
	}
}