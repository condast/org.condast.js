package nl.martijndwars.webpush.core;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.condast.commons.messaging.push.ISubscription;
import org.jose4j.lang.JoseException;

import com.google.gson.Gson;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

//Sets the path to alias + path
public class PushManager{

	public static final String S_ERR_UNKNOWN_REQUEST = "An invalid request was rertrieved: ";
	public static final String S_ERR_INVALID_VESSEL = "A request was received from an unknown vessel:";
	
	public static final long DEFAULT_PERIOD = 3600000;//every hour
	
	/** The Time to live of GCM notifications */
	private static final int TTL = 255;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Map<Long,SubscriptionData> subscriptions;
	
	private Timer timer;
	private TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			refresh();
		}		
	};
	
	public PushManager() {
		subscriptions = new HashMap<>();
		timer = new Timer( true );
		timer.schedule(timerTask, DEFAULT_PERIOD, DEFAULT_PERIOD);
	}

	public ISubscription subscribe( long id, String token, String subscription ) {
		logger.info( subscription );
		Gson gson = new Gson();
		Subscription sub = gson.fromJson(subscription, Subscription.class );
		if( !Subscription.isValidSubscription(sub)) 
			return null;
		synchronized( subscriptions ){
			subscriptions.put(id, new SubscriptionData( id, sub ));
		}
		return sub;
	}
	
	public void unsubscribe( long subscriptionId ) {
		this.subscriptions.remove( subscriptionId );
	}

	public boolean hasSubscription( long subscriptionId ) {
		return this.subscriptions.containsKey(subscriptionId );
	}
	
	public ISubscription getSubscription( long userId ) {
		return this.subscriptions.get( userId ).getSubscription();
	}

	private void refresh() {
		synchronized( subscriptions ){
			Collection<SubscriptionData> temp = new ArrayList<>( this.subscriptions.values() );
			for( SubscriptionData data: temp) {
				if( data.isPassed())
					this.subscriptions.remove(data.getUserId());
			}
		}
	}
	
	public static String sendPushMessage(String publicKey, String privateKey, ISubscription sub, byte[] payload) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {

		  // Figure out if we should use GCM for this notification somehow
		  boolean useGcm = shouldUseGcm(sub);
		  Notification notification;
		  PushService pushService;
		  
		  if (!useGcm) {
		    // Create a notification with the endpoint, userPublicKey from the subscription and a custom payload
		    notification = new Notification(
		      sub.getEndpoint(),
		      sub.getUserPublicKey(),
		      sub.getAuthAsBytes(),
		      payload
		    );

		    // Instantiate the push service, no need to use an API key for Push API
		    pushService = new PushService( publicKey, privateKey, null );
		  } else {
		    // Or create a GcmNotification, in case of Google Cloud Messaging
		    notification = new Notification(
		      sub.getEndpoint(),
		      sub.getUserPublicKey(),
		      sub.getAuthAsBytes(),
		      payload,
		      TTL
		    );

		    // Instantiate the push service with a GCM API key
		    pushService = new PushService("gcm-api-key");
		    pushService.setPublicKey(publicKey);
		    pushService.setPrivateKey(privateKey);
		  }

		  // Send the notification
		  HttpResponse response = pushService.send(notification);
		  return response.toString();
		}

		private static boolean shouldUseGcm(ISubscription sub) {
			return true;
		}
		
		private class SubscriptionData{
			
			private long userId;
			private ISubscription subscription;
			private Date create;
			
			public SubscriptionData( long userId, ISubscription subscription) {
				super();
				this.userId = userId;
				this.subscription = subscription;
				this.create = Calendar.getInstance().getTime();
			}
			
			public long getUserId() {
				return userId;
			}

			public ISubscription getSubscription() {
				return subscription;
			}
			
			public boolean isPassed() {
				Calendar calendar = Calendar.getInstance();
				Date current = Calendar.getInstance().getTime();
				calendar.setTime(create);
				if( this.subscription == null )
					return true;
				calendar.add(Calendar.MILLISECOND, (int)Double.parseDouble( this.subscription.getExpirationTime()));
				Date end = calendar.getTime();
				return current.after(end);
			}
		}
}