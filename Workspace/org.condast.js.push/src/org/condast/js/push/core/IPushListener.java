package org.condast.js.push.core;

public interface IPushListener {

	public enum Calls{
		SUBSCRIBE,
		UPDATE;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
		
		public static boolean isValidCall( String str ) {
			for( Calls call: values()) {
				if( call.toString().equals(str))
					return true;
			}
			return false;
		}
	}

	public void notifyPushEvent( PushEvent event );
}
