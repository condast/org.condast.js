package org.youtube.viewer.session;


public interface ISessionListener<T extends Object> {

	enum Parameters{
		TOKEN,
		TYPE,
		DATA;
	
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public static enum Types{
		INIT;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}
	
	public static enum Commands{
		MARKER,
		DONE;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public void notifySessionChanged( SessionEvent<T> event );
}
