package org.condast.js.commons.push;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class PushOptionsBuilder {

	public enum Options{
		TITLE,
		BODY,
		ICON,
		IMAGE,
		BADGE,
		VIBRATE,
		SOUND,
		DIR,
		BEHAVIOURAL;

		@Override
		public String toString() {
			switch( this ) {
			case BEHAVIOURAL:
				return "//";
			default:
				return name().toLowerCase();
			}
		}	
	}
	
	private Map<String, String> options;
	
	public PushOptionsBuilder() {
		options = new HashMap<>();
	}
	
	public void addOption( Options key, String value ) {
		this.options.put(key.toString(), value);
	}

	public void reomveOption( Options key, String value ) {
		this.options.remove(key, value);
	}
	
	public byte[] createPayLoad() {
		Gson gson = new Gson();
		return gson.toJson(options, Map.class).getBytes();
	}

}
