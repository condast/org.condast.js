package org.condast.js.commons.images;

public interface IDefaultMarkers {

	public static final String S_IMAGE_ROOT = "/images/";
	public static final String S_MARKER = "Marker";
	public static final String S_PNG = ".png";
	
	public enum Markers{
		BLUE,
		BROWN,
		DARKGREEN,
		GREEN,
		ORANGE,
		PALEBLUE,
		PINK,
		PURPLE,
		RED,
		YELLOW;
		
		/**
		 * Get the image with the given id.
		 * @param id
		 * @return
		 */
		public String getImage( String root, char id ){
			String name = name().toLowerCase() + "_" + S_MARKER + String.valueOf( id ).toUpperCase() + S_PNG;
			return root + S_IMAGE_ROOT + name;
		}
	}
}
