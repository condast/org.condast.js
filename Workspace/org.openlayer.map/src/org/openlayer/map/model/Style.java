package org.openlayer.map.model;

public class Style {

	private Stroke stroke;
	private double width;
	
	protected Style(Stroke stroke) {
		super();
		this.stroke = stroke;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public double getWidth() {
		return width;
	}
}
