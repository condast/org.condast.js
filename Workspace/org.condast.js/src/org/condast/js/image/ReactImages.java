/*******************************************************************************
 * Copyright (c) 2014 Chaupal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *******************************************************************************/
package org.condast.js.image;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.image.AbstractImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class ReactImages extends AbstractImages{

	public static final String BUNDLE_ID = "org.condast.react";
	
	public static final String S_ICON_PATH = "/rdmresources/";

	public enum Images{
		AQUABOT,
		AQITEC,
		CORROSION,
		DUTCH_DRONE_COMPANY,
		ECE_OFFSHORE,
		FIELD_LAB_RDM,
		INDYMO,
		SCREEN_SAVER;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
		public String getResource(){
			String str = null;
			switch( this ){
			case DUTCH_DRONE_COMPANY:
				str = "ddc.png";
				break;
			case ECE_OFFSHORE:
				str = "ece-offshore.png";
				break;
			case FIELD_LAB_RDM:
				str = "rdm.png";
				break;
			default:
				str = this.toString().toLowerCase() + ".png";
				break;
			}
			return str;
		}
	}
	
	private static ReactImages images = new ReactImages();
	
	private ReactImages() {
		super( S_ICON_PATH, BUNDLE_ID );
	}

	/**
	 * Get an instance of this map
	 * @return
	 */
	public static ReactImages getInstance(){
		return images;
	}
	
	@Override
	public void initialise(){
		for( Images image: Images.values() )
			setImage( image.getResource());
	}
	
	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public static Image getImage( Images desc ){
		return getInstance().getImageFromName( desc.getResource() );
	}
	
	/**
	 * Get the screen aver and size it to fit the parent
	 * @param parent
	 * @return
	 */
	public static Image getScreenSaver( Composite parent ){
		ImageData imageData = new ImageData( ReactImages.class.getResourceAsStream( S_ICON_PATH + Images.SCREEN_SAVER.getResource() ));
		ImageData scaledData = imageData.scaledTo( parent.getBounds().width, parent.getBounds().height );
		return new Image(Display.getCurrent(), scaledData );
	}
}