package org.openlayer.map.control;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.IJavascriptController;
import org.condast.js.commons.images.IDefaultMarkers;

public class IconsView {

	public static final String S_OPENLAYERS_ROOT = "/openlayer";
	
	public enum Commands{
		CREATE_STYLE,
		CLEAR_ICONS,
		ADD_ICON,
		REPLACE_ICON,
		REMOVE_ICON;

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}
	}
	
	private IJavascriptController controller;

	public IconsView( IJavascriptController controller) {
		this.controller = controller;
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String clearIcons(){
		String query = Commands.CLEAR_ICONS.toString();
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
	public String addMarker( String name, LatLng latlng, IDefaultMarkers.Markers marker,  char type, double opacity ){
		return this.addIcon(name, latlng, marker.getImage(S_OPENLAYERS_ROOT, type), opacity);
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String addMarker( String name, LatLng latlng, IDefaultMarkers.Markers marker,  char type ){
		return this.addMarker( name, latlng, marker, type, 1.0);
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String addMarker( LatLng latlng, IDefaultMarkers.Markers marker, char type ){
		return this.addMarker( latlng.getId(), latlng, marker, type, 1.0);
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String replaceMarker( LatLng latlng, IDefaultMarkers.Markers marker, char type ){
		return this.replaceIcon(latlng.getId(), marker.getImage(S_OPENLAYERS_ROOT, type), 1.0);
	}

	/**
	 * Add an icon
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
	 * @param opacity
	 * @return
	 */
	public String addIcon( String name, LatLng latlng, String path, double opacity ){
		String[] params = new String[5];
		params[0] = name;
		params[1] = String.valueOf( latlng.getLatitude() );
		params[2] = String.valueOf( latlng.getLongitude() );
		params[3] = path;
		params[4] = String.valueOf( opacity );
		String query = Commands.ADD_ICON.toString();
		controller.setQuery( query, params );
		return query;		
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String replaceIcon( String name, String path, double opacity ){
		String[] params = new String[3];
		params[0] = name;
		params[1] = path;
		params[2] = String.valueOf( opacity );
		String query = Commands.REPLACE_ICON.toString();
		controller.setQuery( query, params );
		return query;		
	}

	public String addIcon( String name, LatLng latlng, String path ){
		return this.addIcon(name, latlng, path, 1.0);
	}

	/**
	 * Add an icon
	 * @param name
	 * @param latlng
	 * @param opacity
	 * @return
	 */
	public String removeIcon( String name ){
		String[] params = new String[1];
		params[0] = name;
		String query = Commands.REMOVE_ICON.toString();
		controller.setQuery( query, params );
		return query;		
	}

	public void synchronise() {
		controller.synchronize();
	}
}
