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
package org.condast.js.commons.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;
import java.util.logging.Logger;

import org.condast.commons.messaging.core.util.FileIOUtil;

public class HttpURLConnectionUtil {

	private static Logger logger = Logger.getLogger(HttpURLConnectionUtil.class.getName() );
	
	 public static String scanResponseHeaders(HttpURLConnection httpURLConnection) throws IOException{
		logger.info("**Scanning ResponseHeader**");
		 StringBuffer sb = new StringBuffer();
		 int count = 0;
		 String key, value;
		 do{
			 key = httpURLConnection.getHeaderFieldKey(count);
			 value = httpURLConnection.getHeaderField(count);
		     count++;

		     if (value != null)
		     {
		    	 key = (key!=null)? key:"No key";
		    	 String s = String.format("%-30s = %-45s\n", key,value);
		    	 sb.append(s + "\n");
			 }
		 }while (value != null);

		 return sb.toString();
	 }

	 public static String scanRequestHeader(HttpURLConnection httpURLConnection){
		 StringBuffer sb = new StringBuffer();
		 logger.info("**Scanning RequestHeader**");
		 Map<String, List<String>> map = httpURLConnection.getRequestProperties();
		 Set<Entry<String,List<String>>> sets = map.entrySet();
		 Iterator<Entry<String,List<String>>> iterator = sets.iterator();
		 if(iterator.hasNext()){
			 Entry<String,List<String>> e = iterator.next();
		    	System.out.println(e.getKey());
		    	for(String s:e.getValue()){
		    		sb.append(e + "\n");
		    		System.out.println(s);
		    	}
		 }
		 System.out.println("**\n");

		 return sb.toString();
	 }

	 public static void saveHttpURLConnection(HttpURLConnection httpURLConnection,String outputFile) throws IOException{
		 InputStream is = httpURLConnection.getInputStream();
		    OutputStream os = new FileOutputStream(outputFile);
		    String type = httpURLConnection.getHeaderField("Content-Type").toLowerCase().trim();
		    if (type.startsWith("text"))
		    	FileIOUtil.saveTextURLtoFile(is, os);

		    else
		      FileIOUtil.saveBinaryToFile(is, os);
		    is.close();
		    os.close();
		    httpURLConnection.disconnect();
	 }
}
