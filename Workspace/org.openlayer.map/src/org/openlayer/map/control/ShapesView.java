package org.openlayer.map.control;

import java.util.Collection;

import org.condast.commons.data.plane.FieldData.Shapes;
import org.condast.commons.data.plane.IField;
import org.condast.commons.data.plane.IPolygon;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.AbstractView;
import org.condast.js.commons.controller.IJavascriptController;

public class ShapesView extends AbstractView<ShapesView.Commands> {

	public static enum Commands{
		CLEAR_SHAPES,
		SET_SHAPE,
		ADD_SHAPE,
		ADDEND_SHAPE,
		GET_SHAPE,
		REMOVE_SHAPE;

		public CommandTypes getCommandType() {
			CommandTypes type = CommandTypes.SEQUENTIAL;
			switch( this ) {
			case CLEAR_SHAPES:
				type = CommandTypes.EQUAL;
				break;
			case SET_SHAPE:
			case REMOVE_SHAPE:
				type = CommandTypes.EQUAL_ATTR;
				break;
			default:
				break;
			}
			return type;
		}

		public boolean isArray() {
			boolean result = false;
			switch( this ) {
			case ADD_SHAPE:
			case ADDEND_SHAPE:
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(this.name());
		}
	}

	public static enum Types{
		POINT,
		LINE_STRING,
		LINEAR_RING,
		MULTI_POINT,
		MULTI_LINE_STRING,
		MULTI_POLYGON,
		GEOMETRY_COLLECTION,
		CIRCLE;
		
		@Override
		public String toString() {
			return StringStyler.prettyString( this.name());
		}

		public static Types fromShape(Shapes shape) {
			Types type = Types.POINT;
			switch( shape ) {
			case LINE:
				type = LINE_STRING;
				break;
			case BOX:
			case SQUARE:
				type = Types.LINEAR_RING;
				break;
			case CIRCLE:
				type = Types.CIRCLE;
				break;
			default:
				break;
			}
			return type;
		}
	}

	private IField field;
	
	public ShapesView( IJavascriptController controller) {
		super( controller );
	}

	@Override
	protected CommandTypes getCommandType(Commands command) {
		return command.getCommandType();
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
	
	/**
	 * Create a new shape on the map of the given type
	 * @param name
	 * @param type
	 * @return
	 */
	public String setShape( String name, Types type){
		Collection<String> params = super.getParameters( Commands.SET_SHAPE);
		params.add( name );
		params.add( type.toString() );
		return super.perform(Commands.SET_SHAPE, params.toArray( new String[params.size()]), false);
	}

	/**
	 * Create a new shape on the map of the given type
	 * @param name
	 * @param type
	 * @return
	 */
	public String setShape( String name, Shapes shape){
		return this.setShape( name, Types.fromShape( shape ));
	}

	public String addShape( IPolygon polygon ){
		Collection<String> params = super.getParameters( Commands.ADD_SHAPE);
		params.add( polygon.toWKT() );
		return super.perform(Commands.ADD_SHAPE, params.toArray( new String[params.size()]), Commands.ADD_SHAPE.isArray());
	}

	public String addShape( String wtk ){
		Collection<String> params = super.getParameters( Commands.ADD_SHAPE);
		params.add( wtk );
		return super.perform(Commands.ADD_SHAPE, params.toArray( new String[params.size()]), Commands.ADD_SHAPE.isArray() );
	}

	public String addendShape( String wtk ){
		Collection<String> params = super.getParameters( Commands.ADDEND_SHAPE);
		params.add( wtk );
		return super.perform(Commands.ADDEND_SHAPE, params.toArray( new String[params.size()]), Commands.ADDEND_SHAPE.isArray() );
	}

	public String getShape( String id ){
		Collection<String> params = super.getParameters( Commands.GET_SHAPE);
		params.add( id );
		return super.perform(Commands.GET_SHAPE, params.toArray( new String[params.size()]), Commands.GET_SHAPE.isArray() );
	}

	public String removeShape( String id ){
		Collection<String> params = super.getParameters( Commands.REMOVE_SHAPE);
		params.add( id );
		return super.perform(Commands.REMOVE_SHAPE, params.toArray( new String[params.size()]), Commands.REMOVE_SHAPE.isArray());
	}

}