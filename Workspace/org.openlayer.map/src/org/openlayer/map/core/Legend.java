package org.openlayer.map.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
		PATH,
		QUAY,
		ROAD,
		SAND,
		SHALLOWS,
		WETLAND,
		BUILDING,
		CONCRETE;
					
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
	}

	private List<Surroundings> area;
	private int scanArea;
	private Surroundings current;

	public Legend() {
		this( DEFAULT_SCAN_AREA);
	}
	
	public Legend( int scanArea ) {
		area = new ArrayList<>();
		this.scanArea = scanArea;
	}

	public Surroundings scanArea( int[] rgba ) {
		Surroundings surr = getLegend(rgba );
		if( area.size() >= this.scanArea)
			area.remove(0);
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
		return ( surr == null )? Surroundings.UNKNOWN: surr;
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

	public static Map<RGBA,Legend.Surroundings> getLegend() {
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

	public static Surroundings getLegend( int[] rgba ) {
		Map<RGBA, Surroundings> legend = Legend.getLegend();
		Surroundings surr = legend.get( new RGBA( rgba ));
		return ( surr == null )? Surroundings.UNKNOWN: surr;
	}

}
