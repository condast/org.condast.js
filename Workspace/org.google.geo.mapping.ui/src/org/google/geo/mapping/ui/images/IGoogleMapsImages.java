package org.google.geo.mapping.ui.images;

public interface IGoogleMapsImages {

	public static final String S_RELATIVE_IMAGE_ROOT = "/geo/images/";
	public static final String S_MARKER = "Marker";
	public static final String S_PNG = ".png";
	
	public enum MarkerImages{
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
		public String getImage(){
			return getImage( 'A' );
		}
		
		/**
		 * Get the image with the given id.
		 * @param id
		 * @return
		 */
		public String getImage( char id ){
			String name = name().toLowerCase() + "_" + S_MARKER + String.valueOf( id ).toUpperCase() + S_PNG;
			return S_RELATIVE_IMAGE_ROOT + name;
		}
	}
}
