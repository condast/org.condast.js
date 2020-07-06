package org.condast.js.push.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.messaging.push.ISubscription;
import org.condast.js.push.core.advice.Advice;
import org.condast.js.push.core.advice.IAdvice;

import com.google.gson.Gson;

import nl.martijndwars.webpush.core.PushManager;

public class AbstractPush implements IPushListener{
	
	public static final String S_ERR_NO_USER_REGISTERED = "This user was not registered for push messages: ";
	
	private String identifier;
	private PushManager pushManager;
	
	private String publicKey;
	private String privateKey; 
	
	private static Logger logger = Logger.getLogger(AbstractPush.class.getName());
	
	private Collection<IPushListener> listeners;

	protected AbstractPush( String identifier ) {
		super();
		this.identifier = identifier;
		pushManager = new PushManager();
		this.listeners = new ArrayList<>();
	}

	protected ISubscription subscribe( long id, String token, String subscription ) {
		ISubscription sub = pushManager.subscribe(id, token, subscription);
		return sub;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void addPushListener( IPushListener listener ) {
		this.listeners.add(listener);
	}

	public void removePushListener( IPushListener listener ) {
		this.listeners.remove(listener);
	}
	
	@Override
	public void notifyPushEvent( PushEvent event) {
		switch( event.getCall()) {
		case SUBSCRIBE:
			subscribe( event.getId(), event.getToken(), event.getData());
			break;
		case SEND:
			Gson gson = new Gson();
			IAdvice advice = gson.fromJson(event.getData(), Advice.class);
			sendPushMessage( event.getId(), advice);
			break;
		default:
			break;
		}
		for( IPushListener listener: this.listeners)
			listener.notifyPushEvent(event);
	}

	protected void initialise( String path ) throws IOException {
		URL url = new URL( path );
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
	}

	/**
	 * Push the given advice to the user
	 * @param usubscriptionId
	 * @param advice
	 * @return
	 */
	public boolean sendPushMessage( long subscriptionId, IAdvice advice ) {
		if(( advice ==  null ) ||( subscriptionId < 0 ))
			return false;
		PushManager pm = pushManager;
		if( !pm.hasSubscription(subscriptionId)) {
			logger.warning(S_ERR_NO_USER_REGISTERED + subscriptionId );
			return false;
		}
		ISubscription subscription = pm.getSubscription( subscriptionId );
		PushOptionsAdviceBuilder builder = new PushOptionsAdviceBuilder();
		try {
			PushManager.sendPushMessage( publicKey, privateKey, subscription, builder.createPayLoad( advice, true ));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}				
		return true;
	}
}
