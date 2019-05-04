package org.openlayer.map.control;

import java.util.Collection;
import java.util.Map;

import org.condast.commons.data.colours.RGBA;
import org.condast.commons.data.latlng.IField;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.surroundings.AbstractBypass;
import org.condast.js.commons.controller.IJavascriptController;
import org.openlayer.map.core.Legend;

public class Bypass extends AbstractBypass {

	private PixelView pixelView;
	
	public Bypass(IField field, IJavascriptController controller) {
		super(field);
		pixelView = new PixelView( controller );
	}

	@Override
	protected boolean attempt(LatLng first, LatLng last) {
		Collection<RGBA> rgbs = pixelView.getPixelsColour(first, last);
		for( RGBA rgba: rgbs ) {
			if(!Legend.Surroundings.WATER.equals( Legend.getLegend(rgba.toArray()))) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected long onScan(Node node, boolean clockwise) {
		int yoff = 0; 
		int sign = clockwise?1:-1;
		LatLng latlng  = null;
		long result1 = Long.MAX_VALUE;
		long result2 = Long.MAX_VALUE;
		do{
			Map.Entry<Double, Double> vector = LatLngUtils.getVector(getFirst(), node.latlng);
			double y = vector.getValue() + sign*yoff++;
			double max = (super.getMaxLeg() > vector.getKey())? super.getMaxLeg(): vector.getKey();
			latlng  = LatLngUtils.transform( node.latlng, vector.getKey(), y);
			LatLng check = LatLngUtils.transform( node.latlng, max, y);

			if( attempt( node.latlng, check)) {
				if( attempt( node.parent.latlng, check))
					node.parent.addChild(latlng, clockwise);
				else
					node.addChild( latlng, clockwise );
				if( attempt( latlng, getLast() ))
					return (long) LatLngUtils.getDistance(latlng, getLast());
				else {
					Node node1 = new Node( latlng );
					result1 = onScan( node1, clockwise );
					Node node2 = new Node( latlng );
					result2 = onScan( node2, !clockwise );
					return ( result1 < result2)? result1: result2;
				}
			}
		}
		while( getField().isInField(latlng, 0));
		return ( result1 < result2)? result1: result2;
	}

}
