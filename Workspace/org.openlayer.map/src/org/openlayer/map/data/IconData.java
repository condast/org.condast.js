package org.openlayer.map.data;

import org.condast.commons.data.latlng.LatLng;
import org.condast.js.commons.images.IDefaultMarkers.Markers;
import org.openlayer.map.control.IconsView;

public class IconData {

	public static final String S_MEMBER_START = "";
	public static final String S_MEMBER_END = "\",";

	private String id;
	private String name;
	private LatLng latlng;
	private String path;
	private double opacity;

	public IconData(String id, String name, LatLng latlng, Markers marker, char type, double opacity) {
		this( id, name, latlng, marker.getImage(IconsView.S_OPENLAYERS_ROOT, type), opacity );
	}
	
	public IconData(String id, String name, LatLng latlng, String path, double opacity) {
		super();
		this.id = id;
		this.name = name;
		this.latlng = latlng;
		this.path = path;
		this.opacity = opacity;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LatLng getLatlng() {
		return latlng;
	}

	public String getPath() {
		return path;
	}

	public double getOpacity() {
		return opacity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"id\": \"" + id + S_MEMBER_END);
		builder.append("\"name\": \"" + name + S_MEMBER_END);
		builder.append("\"latitude\": \"" + latlng.getLatitude() + S_MEMBER_END);
		builder.append("\"longitude\": \"" + latlng.getLongitude() + S_MEMBER_END);
		builder.append("\"path\": \"" + path + "\"");
		builder.append("}");
		return builder.toString();
	}
}
