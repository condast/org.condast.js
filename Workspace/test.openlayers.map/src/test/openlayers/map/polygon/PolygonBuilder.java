package test.openlayers.map.polygon;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.Polygon;
import org.condast.commons.strings.StringStyler;

public class PolygonBuilder {

	private static final String S_REF_POLYGON = 
			"POLYGON((6.079712163399909 53.15660657747998,6.078153799962258 53.15620610078594,6.078821670006964 53.15527164063832, 6.080380033444617 53.155672126049296,6.079712163399909 53.15660657747998))";
	private static final String S_REF_POLYGON2 = 
			"POLYGON((6.079688149247124 53.15675512181994,6.07794471338744 53.156449538589044,6.078454333100273 53.155404105823465,6.080197768959957 53.15570969649576,6.079688149247124 53.15675512181994))";
	private static final String S_REF_POLYGON3 = 	
			"POLYGON((6.079712163399909 53.15660657747998,6.078153799962258 53.15620610078594,6.078821670006964 53.15527164063832,6.080380033444617 53.155672126049296,6.079712163399909 53.15660657747998))";

	public enum TestPolygons{
		LEIJEN1,
		LEIJEN2,
		LEIJEN3;

		@Override
		public String toString() {
			return StringStyler.prettyString(super.toString());
		}
		
		public static String[] getNames() {
			String[] arr = new String[ values().length ];
			for( int i=0; i< values().length; i++ )
				arr[i] = values()[i].toString();
			return arr;
		}
		
		public static String toWTK( TestPolygons polygon ) {
			switch( polygon) {
			case LEIJEN1:
				return S_REF_POLYGON;
			case LEIJEN2:
				return S_REF_POLYGON2;
			case LEIJEN3:
				return S_REF_POLYGON3;
		default:
				break;
			}
			return null;
		}
	}
	
	public PolygonBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public static Polygon createPolygon( TestPolygons pgs) {
		Polygon polygon = Polygon.fromWKT(pgs.toString(), TestPolygons.toWTK(pgs));
		polygon.setFieldData();
		return polygon;
	}

	public static Polygon[] getPolygons( ) {
		Polygon[] polygons = new Polygon[ TestPolygons.values().length];
		for( int i=0; i< TestPolygons.values().length; i++ ){
			TestPolygons pgs = TestPolygons.values()[i];
			polygons[i] =  Polygon.fromWKT(pgs.toString(), TestPolygons.toWTK(pgs));
			polygons[i].setFieldData();
		}
		return polygons;
	}

	public static LatLng[] getLocations( ) {
		Polygon[] polygons = getPolygons();
		LatLng[] locations = new LatLng[ TestPolygons.values().length];
		for( int i=0; i< polygons.length; i++ ){
			Polygon polygon = polygons[i];
			locations[i] =  polygon.getCoordinates();
		}
		return locations;
	}

}
