package org.openlayer.map.control;

import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.IJavascriptController;

import com.google.gson.Gson;

public class GeoJson {

	public static final String S_SET_STYLE_ID = "SetStyleId";
	public static final String S_SET_STYLE = "setStyle";

	public enum Commands{
		SET_STROKE,
		SET_STYLE,
		SET_FIELD,
		DRAW_LINE;

		@Override
		public String toString() {
			return StringStyler.toMethodString(this.name());
		}
	}
	
	private IJavascriptController controller;

	public GeoJson( IJavascriptController controller) {
		this.controller = controller;
	}
	

	public String setCommand( String query, String style, Object obj ){
		Gson gson = new Gson();
		String[] params = new String[3];
		params[0] = query;
		params[1] = gson.toJson( style );
		params[2] = gson.toJson(obj);
		controller.setQuery( query, params );
		return query;
	}

	public void synchronize(){
		controller.executeQuery();
	}
	
	/**
	 * a default browser function that ca be added to javascript code for call back
	 * @author Kees
	 *
	 */
/*
	private class StyleCallBack extends BrowserFunction{
		
		private String id;
		
		private StyleCallBack(Browser browser ) {
			super(browser, S_SET_STYLE);
			this.id = S_SET_STYLE_ID;
		}

		@Override
		public Object function(Object[] arguments) {
			
			return super.function(arguments);
		}	
	}
*/
}