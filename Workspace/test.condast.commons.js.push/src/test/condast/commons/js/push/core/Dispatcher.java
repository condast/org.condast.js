package test.condast.commons.js.push.core;

import java.util.logging.Logger;

import org.condast.commons.messaging.push.ISubscription;
import org.condast.js.push.core.IPushListener;
import org.condast.js.push.core.PushEvent;
import org.condast.js.push.core.advice.IAdvice;

import nl.martijndwars.webpush.core.PushManager;
import test.condast.commons.js.push.servlet.PushOptionsAdviceBuilder;

public class Dispatcher implements IPushListener{

	public static final String S_ERR_NO_USER_REGISTERED = "This user was not registered for push messages: ";
	
	public static String TITLE = "Commons Test Service";
	public static final String S_PUBLIC_KEY =   "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE7i81_eE9YMFhMrAU_X5SjPMJ_bZNc91L5yRNllMfOO3VLI7YnQC4RIYJq9Bcv-kgfqG4f_3kFTSwTd8Aegyu-w";
	//public static final String S_PUBLIC_KEY = "BPf36QAqZNNvvnl9kkpTDerXUOt6Nm6P4x9GEvmFVFKgVyCVWy24KUTs6wLQtbV2Ug81utbNnx86_vZzXDyrl88=";//Works
	public static final String S_PRIVATE_KEY = "MHcCAQEEIBNsJSVsWRZbrKgHPEAn2d-TUgabWWjne3imZiv5vw6RoAoGCCqGSM49AwEHoUQDQgAE7i81_eE9YMFhMrAU_X5SjPMJ_bZNc91L5yRNllMfOO3VLI7YnQC4RIYJq9Bcv-kgfqG4f_3kFTSwTd8Aegyu-w";

	private static Dispatcher dispatcher = new Dispatcher();
	
	private PushManager pushManager;

	private static Logger logger = Logger.getLogger(Dispatcher.class.getName());

	private Dispatcher() {
		pushManager = new PushManager();
	}
	
	public static  Dispatcher getInstance() {
		return dispatcher;
	}

	public PushManager getPushManager() {
		return pushManager;
	}

	public void setPushManager(PushManager pushManager) {
		this.pushManager = pushManager;
	}
	
	/**
	 * Push the given advice to the user
	 * @param userId
	 * @param advice
	 * @return
	 */
	public boolean sendPushMessage( long userId, IAdvice advice ) {
		if(( advice ==  null ) ||( userId < 0 ))
			return false;
		if( !pushManager.hasSubscription(userId)) {
			logger.warning(S_ERR_NO_USER_REGISTERED + userId );
			return false;
		}
		ISubscription subscription = pushManager.getSubscription( userId );
		PushOptionsAdviceBuilder builder = new PushOptionsAdviceBuilder();
		try {
			PushManager.sendPushMessage( S_PUBLIC_KEY, S_PRIVATE_KEY, subscription, builder.createPayLoad( advice, true ));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}				
		return true;
	}

	@Override
	public void notifyPushEvent(PushEvent event) {
		switch( event.getCall()) {
		case SUBSCRIBE:
			ISubscription subscription = pushManager.subscribe( event.getUserId(), event.getToken(), event.getData());
			break;
		default:
			break;
		}
	}

}
