package org.openlayer.map.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeatureCollection {

	public enum Schemes{
		EPSG_3857;
		
		@Override
		public String toString() {
			return this.name().replace("_", ":");
		}
	}
	
	private String name;
	private Crs crs;
	private Collection<Feature>features;

	protected FeatureCollection( String type, Feature feature) {
		this( type, Schemes.EPSG_3857, feature );
	}
	
	protected FeatureCollection( String type, Schemes scheme, Feature feature) {
		super();
		this.name = "FeatureCollection";
		crs = new Crs( type, scheme );
		this.features.add( feature );
	}
	
	public String getName() {
		return name;
	}

	public Crs getCrs() {
		return crs;
	}


	private class Crs{

		private String type;
		private Map<String, String> properties;

		protected Crs(String type, Schemes scheme) {
			super();
			this.type = type;
			this.properties = new HashMap<String, String>();
			this.properties.put("name", scheme.toString());
		}

		public String getType() {
			return type;
		}

		public Map<String, String> getCoordinates() {
			return properties;
		}
	}
}
