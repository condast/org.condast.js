/*******************************************************************************
 * Copyright (c) 2016 Condast and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Condast                - EetMee
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package  org.condast.js.commons.utils;

import java.text.DecimalFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * @author jverp
 * 
 */
public final class StringUtils {
	
	public static final String S_HASH = "#";
	public static final String S_DOUBLE_SLASH = "//";
	

	public static final String REG_EX = "\\$\\{(.+?)\\}";//${var}

	/**
	 * This code is based on the hashcode implementations in JAVA
	 * Keep an eye out on Levenshtein distances:
	 * @See https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance
	 * @See https://en.wikipedia.org/wiki/Levenshtein_distance 
	 * @return
	 */
	public static int computeStringDistance(CharSequence lhs, CharSequence rhs, boolean leftToRight) {      
		//use the shortest of the two 
		int length = ( lhs.length() < rhs.length())? lhs.length(): rhs.length();
		int hash = 0;
		int weight;
		int index = 0;
		for (int i = 0; i <= length-1; i++){
			index = ( leftToRight )? i: (length - i - 1);
			weight = lhs.charAt(index) - rhs.charAt(index);
			hash += (hash << 5 ) - hash + weight;
		}
		return hash;                           
	} 	

	/**
	 * This calculates the differences as long as it fits in a long (8 bytes max). This will
	 * work for postal codes
	 * @return
	 */
	public static long computeSmallStringDistance(CharSequence lhs, CharSequence rhs, boolean leftToRight ) {      
		//use the shortest of the two 
		int length = ( lhs.length() < rhs.length())? lhs.length(): rhs.length();
		long hash = 0;
		short weight = 0;
		int index = 0;
		for (int i = 0; i < length; i++){
			index = ( leftToRight )? i: (length - i - 1);
			weight = (short)( lhs.charAt(index) - rhs.charAt(index));
			hash <<=8;
			hash += weight;
		}
		return hash;                           
	} 	

	/**
	 * This code is based on the hashcode implementations in JAVA
	 * Keep an eye out on Levenshtein distances:
	 * @See https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance
	 * @See https://en.wikipedia.org/wiki/Levenshtein_distance 
	 * @return
	 */
	public static long computeStringDistance(CharSequence lhs, CharSequence rhs, boolean leftToRight, byte compress) {      
		//use the shortest of the two 
		int length = ( lhs.length() < rhs.length())? lhs.length(): rhs.length();
		long hash = 0;
		short weight = 0;
		int index = 0;
		int maxval = 1 <<( 8 + compress );
		for (int i = 0; i <= length; i++){
			index = ( leftToRight )? i: (length - i - 1);
			weight = (short)( lhs.charAt(index) - rhs.charAt(index));
			if( weight == 0 )
				continue;
			int store = weight;
			while( store < maxval ){
				store<<=1; 
			}
			hash += (store>>compress);
		}
		return hash;                           
	} 	

	public static String checkStringForNull(String text){
		String rtv = "";
		if (text != null)
			rtv=text;
		return rtv;
		
	}
	
    public static boolean hasText(String text) {
       boolean retval = false;
        if (text != null && text.trim().length() > 0) {
            retval = true;
        }
        return retval;
    }
	/**
	 * Replaces non letters of digits with an '_'.
	 * @param s
	 * @return
	 */
	public static String replaceUnprintables(String s) {
		StringBuilder sb = new StringBuilder(s);
		for (int i = 0; i < sb.length(); i++) {
			if (! Character.isLetterOrDigit(sb.charAt(i))) {
				sb.setCharAt(i, '_');

			}
		}
		return sb.toString();
	}
	
	public static boolean isEmpty( String str ){
		return (( str == null ) || ( str.trim().length()  == 0 ));
	}

	/**
	 * Set the first character of the string to uppercase
	 * @param strng
	 * @return
	 */
	public static String firstUpperCaseString( String strng ){
		char chr = strng.charAt(0);
		String str = strng.toString().toLowerCase().substring(1);
		return String.valueOf(chr) + str;		
	}

	/**
	 * Create a pretty string from the given one
	 * @param strng
	 * @return
	 */
	public static String prettyString( String strng ){
		if( !strng.toUpperCase().equals(strng ) && !strng.toLowerCase().equals(strng ))
			return strng;
		String[] split = strng.split("[_]");
		StringBuilder buffer = new StringBuilder();
		for( String str: split )
			buffer.append( firstUpperCaseString( str ));
		return buffer.toString();
	}
	
	public static String removeLastTwoCharacters(String in){
		String rtv = "";
		StringBuilder sb = new StringBuilder(in);
		final int length = sb.length();
		if(length>0){
			sb.delete(length-2, length); 
			rtv = sb.toString();


		}
		return rtv;
	}
	


	public static Set<String> getLastStringsSet(Set<String> set, String remove){
		Set<String> rtv = new TreeSet<String>();
		for(String s:set){
			String snew = s.replace(remove,"");
			snew = snew.trim();
			if(!snew.isEmpty())
			 rtv.add(snew);
		}
		
		return rtv;		
	}
	
	public static String[] splitStringOnWhiteSpaces(String s){
		s = s.trim();
		String[] split = s.split("\\s+");

		return split;
	}
	
	/**
	 * Returns true if the String is numeric
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
		if( isEmpty( str ))
			return false;

		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
	}

	public static boolean isComment(String sHash, String line) {
		return isEmpty( line )?false: line.trim().startsWith(sHash);
	}
	
	/**
	 * Strip a String of text, so conserving the amount (as a String).
	 * For instance "He wants to pay 5.50 Euro" will return "5,50". 
	 * @param s A String containing an amount
	 * @return amount The amount with a comma, as String 
	 */
	public static String getOnlyTheNumeric( String sentence ) {
		String amount = null;

		if( !isEmpty( sentence ) ) {
			NumberFormat nf = new DecimalFormat("#0.00");
			//NumberFormat nf = NumberFormat.getCurrencyInstance();//this one adds f.i. the euro sign. We don't want that
			String[] words = splitStringOnWhiteSpaces( sentence );
			for( String word : words ) {


				if( isNumeric( word ) ) {//isNumeric checks for word == null
					amount = word.replace( ',', '.' );
					amount = nf.format( Double.parseDouble( amount ) );//here parseDouble is required
					break;
				}//else amount will stay null: important to return a value or a null. Null means: no value entered in the gui
			}
		}
		return amount;
	}
	
	/**
	 * Strip a String of text, so conserving the amount (as a String).
	 * For instance "He wants to pay 5.50 Euro" will return "5,50". 
	 * @param s, a String containing an amount
	 * @return amount, the String amount with a comma, with a leading currency symbol 
	 */
	public static String getOnlyTheAmountWithCurrencySymbol( String sentence ) {
		String amount = null;
		if( !isEmpty( sentence ) ) {
			//NumberFormat nf = new DecimalFormat("#0.00");
			NumberFormat nf = NumberFormat.getCurrencyInstance();//this one adds f.i. the euro sign. We want that.
			String[] words = splitStringOnWhiteSpaces( sentence );
			for( String word : words ) {
				if( isNumeric( word ) ) {//isNumeric checks for word == null
					amount = word.replace( ',', '.' );
					amount = nf.format( Double.parseDouble( amount ) );//here parseDouble is required
					break;
				}//otherwise amount will stay null
			}
		}
		return amount;
	}

	public static String readInput( InputStream in ){
		StringBuilder buffer = new StringBuilder();
		Scanner scanner = new Scanner( in );
		try{
			while( scanner.hasNextLine() )
				buffer.append( scanner.nextLine() );
		}
		finally{
			scanner.close();
		}
		return buffer.toString();
	}

	
	public static String getContent( File file ) {
		StringBuilder buffer = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(file );
			while( scanner.hasNextLine())
				buffer.append(scanner.nextLine());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			scanner.close();
		}
		return buffer.toString();
	}
		
	/**
	 * Get the currency for the given locale
	 * @param locale
	 * @param amount
	 * @return
	 */
	public static String getCurrency( Locale locale, double amount ){
	    NumberFormat currencyFormatter = 
	            NumberFormat.getCurrencyInstance( locale);
	    return currencyFormatter.format( amount);
	}
	
	/*
	 * replace the given source with a string that has replaced all the ${<var>} code with
	 * the corresponding value from the hasmap
	 * @param source
	 * @param params
	 * @return
	 */
	public static String replace( String source, Map<String, String> params) {
        Pattern pattern = Pattern.compile( REG_EX);
        Matcher matcher = pattern.matcher( source );
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = params.get(matcher.group(1));
            builder.append(source.substring(i, matcher.start()));
            if (replacement == null) {
                builder.append("");
            } else {
                builder.append(replacement);
                i = matcher.end();
            }
        }
        builder.append(source.substring(i, source.length()));
        return builder.toString();
    }
	
	public static String deleteLastDot( String s ){
		char ch = s.charAt( s.length() - 1 );
		if( '.' == ch ){
			s = s.substring( 0, ( s.length() - 2 ) );
		}
		return s;
	}
}
