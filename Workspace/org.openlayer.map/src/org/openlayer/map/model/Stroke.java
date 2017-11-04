package org.openlayer.map.model;

public class Stroke {

	private String Color;
	private double width;
	private int lineDash;
	
	protected Stroke(String color, double width) {
		this( color, width, 0);
	}

	protected Stroke(String color, double width, int lineDash) {
		super();
		Color = color;
		this.width = width;
		this.lineDash = lineDash;
	}
}
