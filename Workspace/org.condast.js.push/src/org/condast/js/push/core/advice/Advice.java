package org.condast.js.push.core.advice;

import java.util.LinkedHashMap;
import java.util.Map;

public class Advice implements IAdvice {

	private long adviceId;
	private long subscriptionId;
	private String title;
	private String identifier;
	private String advice;
	private int repeat;
	private IAdvice.AdviceTypes type;
	private String uri;
	private String icon;
	private String badge;
	private Map<Notifications, String> notifications;

	
	public Advice( long subscriptionId, long adviceId, String identifier, String Title, IAdvice.AdviceTypes type, String advice, String icon, String badge, int repeat) {
		super();
		this.subscriptionId = subscriptionId;
		this.title = Title;
		this.identifier = identifier;
		this.type = type;
		this.adviceId = adviceId;
		this.advice = advice;
		this.icon = icon;
		this.badge = badge;
		this.repeat = repeat;
		this.notifications = new LinkedHashMap<>();
	}

	@Override
	public long getId() {
		return this.adviceId;
	}
	
	@Override
	public long getSubscriptionId() {
		return this.subscriptionId;
	}

	@Override
	public String getTag() {
		return identifier;
	}

	/* (non-Javadoc)
	 * @see org.collin.moodle.core.IAdvice#getMember()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/* (non-Javadoc)
	 * @see org.collin.moodle.core.IAdvice#getAdvice()
	 */
	@Override
	public String getDescription() {
		return this.advice;
	}
	
	@Override
	public String getIcon() {
		return icon;
	}

	@Override
	public String getBadge() {
		return badge;
	}

	@Override
	public void addNotification( IAdvice.Notifications notification, String uri ) {
		this.notifications.put(notification, uri );
	}

	@Override
	public void removeNotification( IAdvice.Notifications notification ) {
		this.notifications.remove(notification);
	}

	public Map<Notifications, String> getNotifications() {
		return notifications;
	}

	/* (non-Javadoc)
	 * @see org.collin.moodle.core.IAdvice#getRepeat()
	 */
	@Override
	public int getRepeat() {
		return repeat;
	}

	/* (non-Javadoc)
	 * @see org.collin.moodle.core.IAdvice#getType()
	 */
	@Override
	public IAdvice.AdviceTypes getType() {
		return type;
	}

	@Override
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
