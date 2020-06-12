package org.condast.js.push.core;

import java.util.Iterator;
import java.util.Map;

import org.condast.js.push.core.advice.IAdvice;
import org.condast.js.push.core.builder.PushOptionsBuilder;

public class PushOptionsAdviceBuilder extends PushOptionsBuilder {

	public static final String S_ADVICE_TAG = "advice-tag";
	
	public PushOptionsAdviceBuilder() {
		super();
	}

	public byte[] createPayLoad( IAdvice advice, boolean renotify ) {
		String description = advice.getDescription();
		String body = description;

		addOption( Options.TITLE, advice.getMember()) ;		
		addOption( Options.BODY, body);
		addOption( Options.DATA, advice );
		addOption( Options.ICON, advice.getIcon());
		addOption( Options.BADGE, advice.getBadge());
		addOption( Options.TAG, S_ADVICE_TAG);
			
		addOption( Options.VIBRATE, new int[]{500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500});
		Iterator<Map.Entry<IAdvice.Notifications, String>>iterator = advice.getNotifications().entrySet().iterator();
		while( iterator.hasNext() ) {
			Map.Entry<IAdvice.Notifications, String> entry = iterator.next();
			addAction( entry.getKey().name(), entry.getKey().name(), entry.getValue());
		}
		return createPayLoad(renotify, false );
	}
}
