package org.condast.js.push.core;

public interface IPushListener {

	public enum Calls{
		SEND,
		SUBSCRIBE,
		UPDATE;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
		
		public static boolean isValidCall( String str ) {
			String check = str.replace("/", "");
			for( Calls call: values()) {
				if( call.toString().equals(check))
					return true;
			}
			return false;
		}
	}

	public void notifyPushEvent( PushEvent event );
}
