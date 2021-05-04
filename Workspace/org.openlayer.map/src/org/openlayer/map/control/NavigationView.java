package org.openlayer.map.control;

import java.util.Collection;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.controller.AbstractView;
import org.condast.js.commons.controller.IJavascriptController;

public class NavigationView extends AbstractView<NavigationView.Commands> {

	public static enum Commands{
		GET_GEO_LOCATION;

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}

		public CommandTypes getCommandType() {
			CommandTypes type = CommandTypes.SEQUENTIAL;
			switch( this ) {
			case GET_GEO_LOCATION:
				type = CommandTypes.EQUAL;
				break;
			default:
				break;
			}
			return type;
		}

		public static boolean isValue( String str ) {
			if( StringUtils.isEmpty(str))
				return false;
			String styles = StringStyler.styleToEnum(str);
			for( Commands command: values()) {
				if( command.name().equals(styles))
					return true;
			}
			return false;
		}
	}

	public NavigationView( IJavascriptController controller) {
		super( null, controller );
	}
	
	@Override
	protected CommandTypes getCommandType(Commands command) {
		return command.getCommandType();
	}


	/**
	 * Get the location of the device if the browser supports it
	 * @param name
	 * @param type
	 * @return
	 */
	public String getLocation(){
		Collection<String> params = super.getParameters( Commands.GET_GEO_LOCATION);
		String result = super.perform(Commands.GET_GEO_LOCATION, params, false );
		return result;
	}
}