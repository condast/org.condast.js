package org.openlayer.map.control;

import java.util.Collection;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.Waypoint;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.controller.AbstractView;
import org.condast.js.commons.controller.IJavascriptController;
import org.condast.js.commons.images.IDefaultMarkers;
import org.openlayer.map.data.IconData;

public class IconsView extends AbstractView<IconsView.Commands>{

	public static final String S_OPENLAYERS_ROOT = "/openlayer";
	
	public enum Commands{
		CREATE_STYLE,
		CLEAR_ICONS,
		ADD_ICON,
		ADD_ICONS,
		REPLACE_ICON,
		REMOVE_ICON;
		
		public CommandTypes getCommandType() {
			CommandTypes type = CommandTypes.SEQUENTIAL;
			switch( this ) {
			case CLEAR_ICONS:
				type = CommandTypes.EQUAL;
				break;
			case REPLACE_ICON:
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
			case ADD_ICONS:
				result = true;
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}
	}
	
	public IconsView( IJavascriptController controller) {
		super( controller );
	}

	
	@Override
	protected CommandTypes getCommandType(Commands command) {
		return command.getCommandType();
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String clearIcons(){
		return super.perform(Commands.CLEAR_ICONS, false);	
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String addMarker( String id, String name, LatLng latlng, IDefaultMarkers.Markers marker,  char type, double opacity ){
		return this.addIcon( id, name, latlng, marker.getImage(S_OPENLAYERS_ROOT, type), opacity);
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String addMarker( String id, String name, LatLng latlng, IDefaultMarkers.Markers marker,  char type ){
		return this.addMarker( id, name, latlng, marker, type, 1.0);
	}

	/**
	 * Add a marker
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String addMarker( LatLng latlng, IDefaultMarkers.Markers marker, char type ){
		String name = StringUtils.isEmpty(latlng.getDescription())? latlng.getId(): latlng.getDescription();
		return addMarker( latlng.getId(), name, latlng, marker, type, 1.0);
	}

	/**
	 * Remove a marker
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String removeMarker( LatLng latlng ){
		return removeIcon( latlng.getId());
	}

	/**
	 * Replace an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String replaceMarker( LatLng latlng, IDefaultMarkers.Markers marker, char type ){
		this.removeIcon(latlng.getId());
		return this.addMarker(latlng, marker, type);
	}

	/**
	 * Replace an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String replaceMarker( String id, IDefaultMarkers.Markers marker, char type ){
		return this.replaceIcon( id, marker.getImage(S_OPENLAYERS_ROOT, type), 1.0);
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param path: path to the image
	 * @param opacity
	 * @return
	 */
	public String addIcon( String id, String name, LatLng latlng, String path, double opacity ){
		String[] params = new String[5];
		params[0] = id;
		params[1] = name;
		params[2] = String.valueOf( latlng.getLatitude() );
		params[3] = String.valueOf( latlng.getLongitude() );
		params[4] = path;
		return perform( Commands.ADD_ICON, params, Commands.ADD_ICON.isArray(), false );
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param path: path to the image
	 * @param opacity
	 * @return
	 */
	public String addIcons( Collection<IconData> icons ){
		String[] params = new String[icons.size()];
		int index = 0;
		for( IconData icon: icons ) {
			params[index++] = icon.toString();
		}
		return perform( Commands.ADD_ICONS, params, Commands.ADD_ICONS.isArray(), false );
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String replaceIcon( String id, String path, double opacity ){
		String[] params = new String[3];
		params[0] = id;
		params[1] = path;
		params[2] = String.valueOf( opacity );
		return perform( Commands.REPLACE_ICON, params, Commands.REPLACE_ICON.isArray(), false );
	}

	public String addIcon( String id, String name, LatLng latlng, String path ){
		return this.addIcon(id, name, latlng, path, 1.0);
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String removeIcon( String id ){
		String[] params = new String[1];
		params[0] = id;
		return perform( Commands.REMOVE_ICON, params, Commands.REMOVE_ICON.isArray(), false);
	}
	
	/**
	 * Add a waypoint
	 * @param waypoint
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String addWaypoint( Waypoint waypoint, IDefaultMarkers.Markers marker ){
		return addMarker( String.valueOf( waypoint.hashCode()), String.valueOf( waypoint.getIndex()), waypoint.getLocation(), marker, waypoint.getMarker(), 1.0);
	}

	/**
	 * Add a waypoint
	 * @param waypoint
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String addWaypoint( Waypoint waypoint, IDefaultMarkers.Markers marker, char type ){
		return addMarker( String.valueOf( waypoint.hashCode()), String.valueOf( waypoint.getIndex()), waypoint.getLocation(), marker, type, 1.0);
	}

	/**
	 * Replace an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String replaceWaypoint( Waypoint waypoint, IDefaultMarkers.Markers marker ){
		return replaceMarker( String.valueOf( waypoint.hashCode()), marker, waypoint.getMarker());
	}


	/**
	 * Replace an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String removeWaypoint( Waypoint waypoint ){
		return removeIcon( String.valueOf( waypoint.hashCode()));
	}

}
