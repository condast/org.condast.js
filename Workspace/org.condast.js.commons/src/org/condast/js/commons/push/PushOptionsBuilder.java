package org.condast.js.commons.push;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.condast.commons.strings.StringUtils;

public class PushOptionsBuilder {

	public enum Options{
		TITLE,
		BODY,
		DATA,
		ICON,
		IMAGE,
		BADGE,
		VIBRATE,
		TAG,
		RENOTIFY,
		REQUIRE_INTERACTION,
		SILENT,
		SOUND,
		DIR,
		BEHAVIOURAL,
		ACTIONS;

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
	
	private Map<String, Object> options;
	
	private Collection<Action> actions;
	
	public PushOptionsBuilder() {
		options = new HashMap<>();
		actions = new ArrayList<>();
	}
	
	public void addOption( Options key, String value ) {
		this.options.put(key.toString(), value);
	}

	protected void addOption( Options key, Object value ) {
		this.options.put(key.toString(), value);
	}

	protected void addOption( Options key, boolean check ) {
		this.options.put(key.toString(), check );
	}

	public void removeOption( Options key ) {
		this.options.remove( key.name() );
	}

	public void addAction( String action, String title, String icon ) {
		this.actions.add( new Action( action, title, icon));
	}

	public boolean removeAction( String action ) {
		if( StringUtils.isEmpty(action))
			return false;
		Collection<Action> temp = new ArrayList<>();
		for( Action act: actions ) {
			if( action.equals(act.getAction()))
				temp.add(act);
		}
		return this.actions.removeAll(temp);
	}

	public byte[] createPayLoad() {
		return toString().getBytes();
	}
	
	public byte[] createPayLoad( boolean renotify, boolean requireInteraction ) {
		if( renotify )
			addOption(Options.RENOTIFY, renotify);
		if( requireInteraction )
			addOption(Options.REQUIRE_INTERACTION, requireInteraction);
		return createPayLoad();
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		Action[] arr = this.actions.toArray(new Action[ this.actions.size()]);
		this.options.put(Options.ACTIONS.toString(), arr);
		return gson.toJson(options);
	}

	private class Action implements IPushAction{
		private String action;
		private String title;
		private String icon;
		
		public Action(String action, String title, String icon) {
			super();
			this.action = action;
			this.title = title;
			this.icon = icon;
		}
		
		/* (non-Javadoc)
		 * @see org.condast.js.commons.push.IPushAction#getAction()
		 */
		@Override
		public String getAction() {
			return action;
		}
		/* (non-Javadoc)
		 * @see org.condast.js.commons.push.IPushAction#getTitle()
		 */
		@Override
		public String getTitle() {
			return title;
		}
		
		/* (non-Javadoc)
		 * @see org.condast.js.commons.push.IPushAction#getIcon()
		 */
		@Override
		public String getIcon() {
			return icon;
		}
	}
}
