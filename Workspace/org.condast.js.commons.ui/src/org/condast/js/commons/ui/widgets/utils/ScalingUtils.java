package org.condast.js.commons.ui.widgets.utils;

import java.util.Map;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.plane.IField;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class ScalingUtils {

	private Canvas control;
	private IField field;

	public ScalingUtils( Canvas control, IField field) {
		this.control = control;
		this.field = field;
	}

	public Point scaleToCanvas( LatLng location ){
		Rectangle clientArea = control.getClientArea();
		Map.Entry<Double, Double> vector = field.getPoint(location);
		int x= (int)(clientArea.width * vector.getKey()/field.getLength());
		int y = (int)(clientArea.height * vector.getValue()/field.getWidth());
		return new Point(x, y );
	}

	public int scaleYToDisplay( int width ){
		Rectangle clientArea = control.getClientArea();
		float scale = ((float)field.getWidth())/clientArea.height;
		return (int)( width/scale );
	}

	public int scaleXToDisplay( int length ){
		Rectangle clientArea = control.getClientArea();
		float scale = ((float)field.getLength())/clientArea.width;
		return (int)( length/scale );
	}

}
