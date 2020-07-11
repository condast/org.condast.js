package org.condast.js.push.core.advice;

import java.util.Map;

import org.condast.commons.strings.StringStyler;

public interface IAdvice {

	enum AdviceTypes{
		FAIL,
		PROGRESS,
		PAUSE,
		SUCCESS,
	}

	enum Mood{
		ANIMATED,
		ANGRY,
		SCARED,
		HAPPY,
		DOUBT,
		NERVOUS,
		SAD;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	enum Notifications{
		UNKNOWN(1),
		DONT_CARE(2),
		PAUSE(3),
		THANKS(4),
		SHUT_UP(5),
		HELP(6);
	
		public int getIndex() {
			return index;
		}

		private int index;
		
		private Notifications( int index ) {
			this.index = index;
		}
		
		public static Notifications getNotification( int index ) {
			for( Notifications nf: values()) {
				if( nf.getIndex() == index )
					return nf;
			}
			return Notifications.UNKNOWN;
		}
	}
	
	public enum Attributes{
		MEMBER,
		REPEAT;
	}

	long getId();
	
	/**
	 * Get the user id for whom the advice is intended
	 * @return
	 */
	long getSubscriptionId();
	
	String getTitle();

	String getTag();

	String getDescription();

	int getRepeat();

	IAdvice.AdviceTypes getType();

	String getUri();
	
	String getIcon();
	
	String getBadge();

	void addNotification(Notifications notification, String uri);
	
	void removeNotification(Notifications notification);

	Map<Notifications, String> getNotifications();
}