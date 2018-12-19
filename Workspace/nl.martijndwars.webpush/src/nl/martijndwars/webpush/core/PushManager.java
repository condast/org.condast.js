package nl.martijndwars.webpush.core;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
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
	
	public static final String S_PUBLIC_KEY = "BDvq04Lz9f7WBugyNHW2kdgFI7cjd65fzfFRpNdRpa9zWvi4yAD8nAvgb8c8PpRXdtgUqqZDG7KbamEgxotOcaA";
	public static final String S_CODED = "BMfyyFPnyR8MRrzPJ6jloLC26FyXMcrL8v46d7QEUccbQVArghc9YHC6USyp4TggrFleNzAUq8df0RiSS13xwtM";

	/** The Time to live of GCM notifications */
	private static final int TTL = 255;


	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Collection<ISubscription> subscriptions;
	
	public PushManager() {
		subscriptions = new ArrayList<>();
	}

	public ISubscription subscribe( long id, String token, String subscription ) {
		logger.info( subscription );
		Gson gson = new Gson();
		Subscription sub = gson.fromJson(subscription, Subscription.class );
		if( !Subscription.isValidSubscription(sub)) 
			return null;
		subscriptions.add(sub);
		return sub;
	}
	
	public void unsubscribe( ISubscription subscription ) {
		this.subscriptions.remove(subscription);
	}
	
	public ISubscription[] getSubscriptions() {
		return this.subscriptions.toArray( new ISubscription[ subscriptions.size()]);
	}
	
	public void refresh() {
		//for( ISub)
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
			// TODO Auto-generated method stub
			return true;
		}	
}