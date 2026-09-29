package org.condast.commons.js.legal.core;

import java.util.Locale;
import org.condast.commons.legal.LegalUtils;
import org.condast.commons.strings.StringStyler;

public class LegalData {

	public static final String S_RESOURCE = "/legal";

	public enum Documents{
		PRIVACY,
		TOS;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}

		public String toPath( Locale locale, LegalUtils.Version version) {
			String doc = LegalUtils.S_LEGAL_TERMS_OF_SERVICE;
			switch( this){
			case PRIVACY:
				doc = LegalUtils.S_LEGAL_PRIVACY; 
				break;
			default:
				break;
			}
			String path = LegalUtils.createLegalPath( locale, version, S_RESOURCE);
			return path + "/" + doc;
		}
		
		public static Documents getDocument( String doc ) {
			String str = StringStyler.styleToEnum(doc);
			return valueOf(str);
		}
	}
}
