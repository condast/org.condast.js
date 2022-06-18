package org.openlayer.map.model;

public class Feature {

	private Geometry geometry;

	protected Feature( Geometry geometry) {
		super();
		this.geometry = geometry;
	}

	public Geometry getGeometry() {
		return geometry;
	}
}
