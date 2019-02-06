package test.condast.community.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.condast.commons.na.community.CommunityResource;
import org.condast.commons.na.community.CommunityResource.Resources;
import org.condast.commons.project.ProjectFolderUtils;
import org.condast.commons.strings.StringUtils;

import com.google.gson.Gson;

public class CommunityParser {

	public static final String S_PC6HNR = "pc6hnr";
	public static final String S_DBF = "dbf";
	public static final String S_GWB_DB = "0801_gwb.csv";

	public static final String S_RESOURCES = "/resources/";
	public static final String S_YEAR = "2017";
	public static final String S_FILE_MUNICIPALITY = "gemeentenaam";
	public static final String S_FILE_NEIGHBOURHOOD = "wijknaam";
	public static final String S_FILE_LOCALITY = "buurtnaam";
	public static final String S_UNKNOWN = "Unknown";

	public enum Attributes{
		ID,
		RESOURCE,
		NAME,
		CHILDREN;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}
	
	
	private InputStream inp;
	
	private Map<Integer, ComData<Integer, ComData<?,?>>> cache;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public CommunityParser( File file ) throws FileNotFoundException {
		this( new FileInputStream( file ));
	}
	
	public CommunityParser( Class<?> clss, String resource ) {
		this( clss.getResourceAsStream( resource ));
	}
	
	public CommunityParser( InputStream inp ) {
		super();
		this.inp = inp;
		cache = new TreeMap<>();
	}

	@SuppressWarnings("unchecked")
	public ComData<?,?>[] parse() {
		cache.clear();
		Scanner scanner = new Scanner( inp );
		try {
			String line;
			if( scanner.hasNextLine())
				scanner.nextLine();
			while( scanner.hasNextLine()) {
				line = scanner.nextLine();
				logger.info("Parsing: " + line );
				if( StringUtils.isEmpty( line ))
					continue;
				String[] split = line.split("[;]");//postcode; housenumber; locality-id; neighbourhood-id; municipality-id
				Integer mcid = Integer.parseInt(split[CommunityResource.Resources.MUNICIPALITY.getIndex()]);
				ComData<?,?> md = cache.get(mcid);
				if( md == null ) {
					String name = getDetails(CommunityResource.Resources.MUNICIPALITY, mcid);
					md = new ComData<>(mcid, name, CommunityResource.Resources.MUNICIPALITY  );
					cache.put(mcid, (ComData<Integer, ComData<?, ?>>) md );
				}
				complete( md, CommunityResource.Resources.MUNICIPALITY, split );
			}
		}
		finally {
			scanner.close();
		}
		return cache.values().toArray( new ComData<?,?>[ cache.size()]);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void complete( ComData current, CommunityResource.Resources resource, String[] split ) {
		if( current.getResource().equals(resource)) {
			int id = -1;
			String name = null;
			switch( resource ) {
			case MUNICIPALITY:
			case NEIGHBOURHOOD:
				id = Integer.parseInt( split[ resource.getIndex()] );
				ComData<?,?> child = (ComData<?,?>) current.getChildren().get(id);
				CommunityResource.Resources cr = CommunityResource.Resources.values()[resource.getIndex()-1];//work from municipality downwards
				if( child == null ) {
					name = getDetails(resource, id);
					child = new ComData<>( id, name, cr);
					current.getChildren().put(id, child);
				}
				complete( child, cr, split );
				break;
			case LOCALITY:
				id = Integer.parseInt( split[ resource.getIndex()] );
				Locality loc = (Locality) current.getChildren().get(id);
				String postcode = split[CommunityResource.Resources.POSTCODE.getIndex()];
				int housenumber = Integer.parseInt(split[CommunityResource.Resources.HOUSE_NUMBER.getIndex()]);
				if( loc == null ) {
					name = getDetails(resource, id);
					loc = new Locality(id, name, postcode, housenumber);
					current.getChildren().put(id, loc);
				}else {
					loc.addPostcode(postcode, housenumber);
				}
				break;
			default:
				break;
			}
			return;
		}
		CommunityResource.Resources cr = CommunityResource.Resources.values()[resource.getIndex()-1];//work from municipality downwards
		if( cr.getIndex() < CommunityResource.Resources.LOCALITY.getIndex())
			return;
		for( Object child: current.getChildren().values()) {
			complete( (ComData<?, ?>) child, cr, split );
		}
	}

	protected static String getDetails( CommunityResource.Resources resource, int code) {
		if( code <=0 )
			return S_UNKNOWN;
		String config = ProjectFolderUtils.getDefaultConfigDir();
		File file = new File( config, resource.toString());
		if(!file.exists())
			return null;
		Scanner scanner = null;
		try {
			scanner = new Scanner( file );
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
	
	public static String[] getPostcodes( File file, String location ) {
		if( StringUtils.isEmpty(location))
			return new String[0];
		String str=  location.toLowerCase().trim();
		String results = StringUtils.getContent(file);
		Gson gson = new Gson();
		ComData<?,?>[] comdata = gson.fromJson(results, ComData[].class);
		Collection<String> codes = new TreeSet<>();
		for( ComData<?,?> td: comdata ) {
			ComData<?,?> cd = td.getComData( str );
			if( cd != null ) 
				codes.addAll(cd.getPostcodes( ));
		}	
		return codes.toArray( new String[ codes.size() ]);
	}

	public static int getNumberFromAddress( String address ) {
		String str = address.replaceAll("[\\D]", "");
		return Integer.parseInt( str );
	}
	
	public static CommunityParser getDefaultParser() throws FileNotFoundException {
		String config = ProjectFolderUtils.getDefaultConfigDir();
		File file = new File( config, CommunityResource.Resources.POSTCODE.toString() );
		return new CommunityParser( file );		
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
				code = Integer.parseInt( CommunityParser.toString(numbers));
				name = CommunityParser.toString( Arrays.copyOfRange(record, 9, record.length-1));
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
				code = Integer.parseInt( CommunityParser.toString(numbers));
				name = CommunityParser.toString( Arrays.copyOfRange(record, 9, record.length-1));
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

	public class ComData<T,U extends Object>{
		@SuppressWarnings("unused")
		private int id;
		private String name;
		private CommunityResource.Resources resource;
		private Map<T, U> children;
		
		private ComData() {
			super();
			children = new TreeMap<>();
		}

		protected ComData(int id, String name, CommunityResource.Resources resource ) {
			this( id, name, resource, new TreeMap<>());
		}

		protected ComData( Map<Integer, String> entry ) {
			this( Integer.parseInt( entry.get(Attributes.ID.ordinal())),
				entry.get( Attributes.NAME.ordinal()),
				CommunityResource.Resources.valueOf( entry.get( Attributes.RESOURCE.ordinal())),
				new TreeMap<>());
		}

		protected ComData(int id, String name, CommunityResource.Resources resource, Map<T, U> children) {
			this();
			this.id = id;
			this.name = name;
			this.resource = resource;
			this.children = children;
		}

		protected Map<T, U> getChildren() {
			return children;
		}

		public CommunityResource.Resources getResource() { 
			return (resource== null )? CommunityResource.Resources.LOCALITY: resource;
		}
		
		public ComData<?,?> getComData( String name  ) {
			if( StringUtils.isEmpty(name))
				return null;
			return getComData(this, name.toLowerCase().trim());
		}

		protected ComData<?,?> getComData( ComData<?,?> data, String name  ) {
			if( data.name.toLowerCase().trim().equals(name))
				return data;
			if(( data instanceof Locality ))
				return null;
			while( data.getChildren().entrySet().iterator().hasNext() ) {
				Map<Integer, String> child = (Map<Integer, String>) data.getChildren().entrySet().iterator().next();
				ComData<?,?> result = getComData( new ComData<Integer, String>( child ), name);
				if( result != null )
					return result;		
			}
			return null;
		}

		public Collection<String> getPostcodes() {
			Collection<String> results = new TreeSet<>();
			getPostcodes( this, results ); 
			return results;
		}

		protected void getPostcodes( ComData<?,?> cd, Collection<String> results ) {
			if( CommunityResource.Resources.LOCALITY.equals( cd.getResource()) ) {
				Locality loc = (Locality) cd;
				results.addAll( loc.getChildren().keySet());
				return;
			}
			for( Object child: cd.getChildren().values() )
				getPostcodes( (ComData<?, ?>) child, results );
		}
	}

	private class Locality extends ComData<String, Collection<Integer>>{
		
		@SuppressWarnings("unused")
		public Locality() {
			super();
		}

		public Locality(int id, String name, String postcode, int houseNumber) {
			super( id, name, CommunityResource.Resources.LOCALITY );
			addPostcode( postcode, houseNumber);
		}
		
		public boolean addPostcode( String postcode, int number ) {
			Collection<Integer> numbers=  getChildren().get(postcode);
			if( numbers == null ) {
				numbers = new TreeSet<>();
				getChildren().put(postcode, numbers);
			}
			return numbers.add(number);
		}

		public Collection<String> getPostcodes() {
			return getChildren().keySet();
		}
	}
}
