package org.openlayer.map.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.condast.commons.Utils;
import org.condast.commons.data.colours.RGBA;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;

public class Legend {

	private static final String S_LEGEND_LOC = "/resources/legend.properties";

	private static final int DEFAULT_SCAN_AREA = 10;

	public enum Surroundings{
		UNKNOWN,
		WATER,
		GREENS,
		FIELD,
		FOREST,
		JETTY,
		PARK,
		PATH,
		QUAY,
		ROAD,
		SAND,
		SHALLOWS,
		WETLAND,
		BUILDING,
		HOUSE,
		CONCRETE;
		
		private int[] rgba;
				
		public int[] getRgba() {
			return rgba;
		}

		public void setRgba(int[] rgba) {
			this.rgba = rgba;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
	}

	private List<Surroundings> area;
	private int scanArea;
	private Surroundings current;
	
	private static Map<RGBA, Surroundings> map = Legend.getSurroundings();

	public Legend() {
		this( DEFAULT_SCAN_AREA);
	}
	
	public Legend( int scanArea ) {
		area = new ArrayList<>();
		this.scanArea = scanArea;
	}

	public static Map<RGBA, Surroundings> getMap(){
		return map;
	}
	
	public List<Surroundings> getArea() {
		return area;
	}

	public Surroundings getCurrent() {
		return current;
	}

	public Surroundings scanArea( RGBA rgba ) {
		if( area.size() >= this.scanArea)
			area.remove(0);
		
		Surroundings surr = getLegend(rgba );
		switch( surr ) {
		case UNKNOWN:
			if( !Surroundings.WATER.equals(current))
				surr = current;
			else {
				if( this.area.contains(Surroundings.SHALLOWS))
					surr = Surroundings.SHALLOWS;
				surr = updateArea(surr);
			}
			area.add(surr);
			break;
		case WATER:
			area.add(surr);
			trim( surr );
			if( area.isEmpty())
				break;
			surr = updateArea(surr);
			if( Surroundings.UNKNOWN.equals(surr))
				surr = Surroundings.SHALLOWS;
			break;
		default:
			area.add(surr);
			break;
		}
		
		current = surr;
		current =  ( surr == null )? Surroundings.UNKNOWN: surr;
		current.setRgba(rgba.toArray());
		return current;
	}

	/**
	 * Trim the area by removing all the FIRST entries
	 * that correspond with the given surrounding
	 * @param surr
	 */
	protected void trim( Surroundings surr ) {
		Iterator<Surroundings> iterator = new ArrayList<>( area ).iterator();
		while( iterator.hasNext() ) {
			Surroundings select = iterator.next();
			if( surr.equals(select))
				area.remove(0);
			else
				return;
		}
	}

	protected int countSurroundings( Surroundings surr ) {
		int amount = 0;
		for( Surroundings surrounding: area ) {
			if( surr.equals(surrounding))
				amount++;
		}
		return amount;
	}

	protected Surroundings updateArea( Surroundings surr ) {
		if( Surroundings.WATER.equals(surr)) {
			if( this.area.contains( Surroundings.SHALLOWS))
				return Surroundings.SHALLOWS;
		}
		
		int diff = 0;
		for( Surroundings surrounding: area ) {
			if( !Surroundings.WATER.equals(surrounding))
				diff++;
		}
		double result = (double)diff/area.size();
		return ( result <= 0.3)?surr: Surroundings.SHALLOWS;
	}
	
	public static boolean isComment( String line ) {
		String str = line.trim();
		return str.startsWith("//") || 
				str.startsWith("#") ||
				str.startsWith("/*") ||
				str.startsWith("*");
	}

	private static Map<RGBA,Legend.Surroundings> getSurroundings() {
		Map<RGBA, Legend.Surroundings> results = new HashMap<>();
		Scanner scanner = new Scanner( Legend.class.getResourceAsStream(S_LEGEND_LOC));
		try {
			Legend.Surroundings surr = Surroundings.UNKNOWN;
			while( scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if( StringUtils.isEmpty(line) || isComment(line))
					continue;
				String[] split = line.split("[:]");
				if( split.length == 2) {
					surr = Legend.Surroundings.valueOf(split[0]);
					if( StringUtils.isEmpty(split[1]))
						continue;
					split = split[1].trim().split("[,]");
				}else {
					if( StringUtils.isEmpty(split[0]))
						continue;
					split = split[0].trim().split("[,]");					
				}
				results.put(new RGBA( split ), surr);
			}
		}
		finally {
			scanner.close();
		}
		return results;
	}

	public static Map<Integer, List<RGBA>> getImage( Collection<Legend> legends ) {
		Map<Integer, List<RGBA>> results = new HashMap<>();
		int index = 0;
		for( Legend legend: legends) {
			results.put( index++, getColours( legend ));
			return results;
		}
		return results;
	}

	public static List<RGBA> getColours( Legend legend ){
		List<RGBA> results = new ArrayList<>();
		legend.area.forEach( s->{ results.add( new RGBA( s.rgba ));});
		return results;
	}
	
	public Surroundings getLegend( RGBA rgba ) {
		Surroundings surr = ( rgba == null )? Surroundings.UNKNOWN: map.get( rgba );
		return ( surr == null )? Surroundings.UNKNOWN: surr;
	}

	public static Map<Integer, List<Legend>> getLegends( Map<Integer,List<RGBA>> radarData ){
		return getLegends(1, radarData);
	}
	
	public static Map<Integer, List<Legend>> getLegends( int radius, Map<Integer,List<RGBA>> radarData ){
		Map<Integer, List<Legend>> legends = new TreeMap<>();
		if( Utils.assertNull(radarData ))
			return legends;
		radarData.entrySet().forEach( e->{ 
			int centre = e.getKey();
			List<RGBA> colours = e.getValue();

			for( int o=0; o<colours.size(); o++ ){
				try {
 					Collection<RGBA> area = new ArrayList<>();
					area.add(colours.get(o));
					
					List<Legend> row = legends.get(centre);
					if( row == null ) {
						row = new ArrayList<Legend>();
						legends.put(centre, row);
					}
									
					for( int i=0; i<2*radius; i++ ) {
						int y = centre-radius +i ;
						if( !radarData.containsKey(y ))
							continue;

						List<RGBA> rrow = radarData.get(y);
						int length = rrow.size();
						for( int j=0; j<=radius; j++ ) {
							int x = o-radius +j ;
							if(( x<0 ) || ( x >= length ))
								continue;
							area.add(rrow.get(x));
						}
					}

					int size = ( area.size()==0?1:area.size());
					Legend legend = new Legend( size);
					row.add(legend);
					if( Utils.assertNull(area))
						return;

					for( RGBA rgba: area )
						legend.scanArea( rgba);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			};
		});
		return legends;
	}
}
