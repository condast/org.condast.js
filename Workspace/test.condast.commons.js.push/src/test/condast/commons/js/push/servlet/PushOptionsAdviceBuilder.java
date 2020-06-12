package test.condast.commons.js.push.servlet;

import java.util.Iterator;
import java.util.Map;

import org.condast.commons.strings.StringUtils;
import org.condast.js.push.core.advice.Advice;
import org.condast.js.push.core.advice.IAdvice;
import org.condast.js.push.core.builder.PushOptionsBuilder;
import test.condast.commons.js.push.LanguagePack;

public class PushOptionsAdviceBuilder extends PushOptionsBuilder {

	public static final String S_ADVICE_TAG = "advice-tag";
	
	public PushOptionsAdviceBuilder() {
		super();
	}

	public IAdvice createAdvice( long userId, long adviceId, String member, IAdvice.AdviceTypes type, String advice, int repeat ) {
		return new Advice(userId, adviceId, member, type, null, null, advice, repeat);
	}
	
	public byte[] createPayLoad( IAdvice advice, boolean renotify ) {
		LanguagePack language = LanguagePack.getInstance();
		String description = advice.getDescription();
		String body = StringUtils.isEmpty(description)? LanguagePack.Fields.SUCCESS1.name(): description;

		addOption( Options.TITLE, language.getString( advice.getMember()) + " " + LanguagePack.Fields.SAYS.toString() + ":") ;		
		addOption( Options.BODY, language.getString(body));
		addOption( Options.DATA, advice );
		//addOption( Options.ICON, TeamImages.Team.getPath(advice));
		//addOption( Options.BADGE, TeamImages.Team.getPath(Team.PLUSKLAS));
		addOption( Options.TAG, S_ADVICE_TAG);
			
		addOption( Options.VIBRATE, new int[]{500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500});
		Iterator<Map.Entry<IAdvice.Notifications, String>>iterator = advice.getNotifications().entrySet().iterator();
		while( iterator.hasNext() ) {
			Map.Entry<IAdvice.Notifications, String> entry = iterator.next();
			addAction( entry.getKey().name(), language.getString( entry.getKey()), entry.getValue());
		}
		return createPayLoad(renotify, false );
	}
}
