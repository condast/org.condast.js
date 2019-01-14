package org.condast.postcode.api.names;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.community.ICommunityQuery;
import org.condast.commons.na.model.Community;
import org.condast.commons.na.model.ICommunity;
import org.condast.commons.project.ProjectFolderUtils;
import org.condast.commons.strings.StringUtils;

public class CommunityQuery implements ICommunityQuery {

	public static final String S_PC6HNR = "pc6hnr";
	public static final String S_DBF = "dbf";
	public static final String S_GWB_DB = "0801_gwb.csv";

	public static final String S_RESOURCES = "/resources/";
	public static final String S_YEAR = "2017";
	public static final String S_FILE_MUNICIPALITY = "gemeentenaam";
	public static final String S_FILE_NEIGHBOURHOOD = "wijknaam";
	public static final String S_FILE_LOCALITY = "buurtnaam";
	public static final String S_UNKNOWN = "Unknown";

	public enum Resources{
		POSTCODE(0),
		HOUSE_NUMBER(1),
		LOCALITY(2),
		NEIGHBOURHOOD(3),
		MUNICIPALITY(4);

		private int index;
		
		private Resources( int index ) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append(S_RESOURCES);
			switch( this ) {
			case POSTCODE:
				buffer.append(S_PC6HNR);
				buffer.append(S_YEAR);
				buffer.append(S_GWB_DB);
				return buffer.toString();
			case MUNICIPALITY:
				buffer.append(S_FILE_MUNICIPALITY);
				break;
			case NEIGHBOURHOOD:
				buffer.append(S_FILE_NEIGHBOURHOOD);
				break;
			case LOCALITY:
				buffer.append(S_FILE_LOCALITY);
				break;
			default:
				break;
			}
			buffer.append(S_YEAR);
			buffer.append(".");
			buffer.append( S_DBF);
			return buffer.toString();
		}
	}
	
	private InputStream inp;
	
	public CommunityQuery( File file ) throws FileNotFoundException {
		this( new FileInputStream( file ));
	}
	
	public CommunityQuery( Class<?> clss, String resource ) {
		this( clss.getResourceAsStream( resource ));
	}
	
	public CommunityQuery( InputStream inp ) {
		super();
		this.inp = inp;
	}

	/**
	 * Get the community from the postcode and house number
	 * @param postcode
	 * @param houseNumber
	 * @param location
	 * @return
	 */
	@Override
	public ICommunity locationQuery( String postcode, String houseNumber, LatLng location) {
		ICommunity community = new Community( postcode, location );
		complete(community, houseNumber);
		return community;
	}

	
	@Override
	public void complete(ICommunity community, String houseNumber) {
		String postcode = community.getPostcode();
		if(( StringUtils.isEmpty(postcode)) || ( StringUtils.isEmpty( houseNumber )))
			return;
		Scanner scanner = new Scanner( inp );
		try {
			String line;
			while( scanner.hasNextLine()) {
				line = scanner.nextLine();
				if( StringUtils.isEmpty( line ))
					continue;
				String[] split = line.split("[;]");
				if( !split[0].equals( postcode ))
					continue;
				if( !houseNumber.contains( split[ Resources.HOUSE_NUMBER.getIndex()]))
					continue;
				int mnp = Integer.parseInt(split[ Resources.MUNICIPALITY.getIndex()]);
				community.setMunicipality( getDetails(Resources.MUNICIPALITY, mnp));
				int ngh = Integer.parseInt(split[ Resources.NEIGHBOURHOOD.getIndex()]);
				community.setNeighbourhood( getDetails( Resources.NEIGHBOURHOOD, ngh ));
				int loc = Integer.parseInt(split[ Resources.LOCALITY.getIndex()]);
				community.setLocality( getDetails(Resources.LOCALITY, loc ));
			}
		}
		finally {
			scanner.close();
		}
	}

	protected static String getDetails( Resources resource, int code) {
		if( code <=0 )
			return S_UNKNOWN;
		Scanner scanner = null;
		try {
			String file = resource.toString();
			InputStream inp = CommunityQuery.class.getResourceAsStream( file );//resource.toString() );
			scanner = new Scanner( inp);
			byte[] line = scanner.nextLine().getBytes();
			DBFFormat format = new DBFFormat(line );
			line = scanner.nextLine().getBytes();
			String name = format.get(line, code );
			if( !StringUtils.isEmpty( name))
				return name;
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		finally {
			if( scanner != null )
				scanner.close();
		}
		return S_UNKNOWN;
	}
	
	public static int getNumberFromAddress( String address ) {
		String str = address.replaceAll("[\\D]", "");
		return Integer.parseInt( str );
	}
	
	public static ICommunityQuery getDefaultQuery() throws FileNotFoundException {
		String config = ProjectFolderUtils.getDefaultConfigDir();
		File file = new File( config, Resources.POSTCODE.toString() );
		return new CommunityQuery( file );		
	}
	
	@SuppressWarnings("unused")
	private static class DBFFormat{
		private byte vendor;
		private Date update;
		private int recordSize;
		private int header, size;
		
		private Map<Integer, String> records;
		
		public DBFFormat( byte[] line ) {
			this.vendor = line[0];
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, line[1] );
			calendar.set(Calendar.MONTH, line[2] );
			calendar.set(Calendar.DAY_OF_MONTH, line[3] );
			update = calendar.getTime();
			recordSize = line[4] + line[5]*256 + line[6]*(256^2) + line[7]*(256^3);
			header = line[10] + line[11]*256;
			size = line[8] + line[9]*256;
			records = new HashMap<>();
		}

		public void parse( byte[] line ) {
			int index = 0;
			byte[] record;
			int code;
			String name;
			for( int i=0; i<recordSize; i++ ) {
				record = Arrays.copyOfRange(line, index, index+header);
				byte[] numbers = Arrays.copyOfRange(record, 0, 9);
				code = Integer.parseInt( CommunityQuery.toString(numbers));
				name = CommunityQuery.toString( Arrays.copyOfRange(record, 9, record.length-1));
				records.put(code, name);
				index += header;
			}
		}
		
		public String get( byte[] line, int find ) {
			if( records.containsKey(find))
				return records.get(find);
			int index = 0;
			byte[] record;
			int code;
			String name;
			for( int i=0; i<recordSize; i++ ) {
				record = Arrays.copyOfRange(line, index, index+header);
				byte[] numbers = Arrays.copyOfRange(record, 0, 9);
				code = Integer.parseInt( CommunityQuery.toString(numbers));
				name = CommunityQuery.toString( Arrays.copyOfRange(record, 9, record.length-1));
				index += header;
				if( code == find )
					return name;
				records.put(code, name);
			}
			return null;
		}

	}
	
	public static final String toString( byte[] arr) {
		StringBuffer buffer = new StringBuffer();
		for( byte bt: arr) {
			if( bt != 32 )
				buffer.append((char)bt);
		}
		return buffer.toString();	
	}
}
