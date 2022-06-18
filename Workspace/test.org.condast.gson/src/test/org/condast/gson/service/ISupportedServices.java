package test.org.condast.gson.service;

import org.condast.commons.Utils;
import org.condast.commons.strings.StringStyler;

public interface ISupportedServices {

	public enum Composites{
		MATCHING,
		HOST_PROFILE,
		HOST_AVAILABILITY,
		HOST_BUILDING,
		GUEST_AVAILABILITY,
		GUEST_PROFILE,
		APPLICATION_PERSON,
		NA_SEARCH,
		PROFILE_SEARCH,
		OTHERS,
		MATCH_SEARCH;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
		public static boolean isValid( String str ){
			if( Utils.assertNull( str ))
				return false;	
			for( Composites comp: values() ){
				if( comp.name().equals( str ))
					return true;
			}
			return false;
		}
	}

	public enum Wizards{
		PROFILE_SEARCH,
		MATCHING,
		MATCH_SEARCH;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
		public static boolean isValid( String str ){
			if( Utils.assertNull( str ))
				return false;	
			for( Wizards comp: values() ){
				if( comp.name().equals( str ))
					return true;
			}
			return false;
		}
	}
	
	public enum Controllers{
		VOLUNTEER,
		APPLICATION_PERSON,
		PROFILE,
		MATCHING,
		ADMIN;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
		public static boolean isValid( String str ){
			if( Utils.assertNull( str ))
				return false;	
			for( Controllers comp: values() ){
				if( comp.name().equals( str ))
					return true;
			}
			return false;
		}
	}

}