package nl.martijndwars.webpush.core;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.condast.commons.messaging.push.ISubscription;
import org.condast.commons.strings.StringUtils;

public class Subscription implements ISubscription {

	public enum Keys{
		P256DH,
		AUTH;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	private String endpoint;
	private String expirationTime;

	private String key;
	private String auth;

	public Subscription() {
		super();
		// Add BouncyCastle as an algorithm provider
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}	
	}

	public boolean checkPublicKey( String publicKey ) throws UnsupportedEncodingException {
		return StringUtils.isEmpty(key)? false: key.equals(publicKey);
	}

	public Subscription(String endpoint, String expirationTime, Map<String, String> keys) {
		this();
		this.endpoint = endpoint;
		this.expirationTime = expirationTime;
	}

	/* (non-Javadoc)
	 * @see org.condast.commons.messaging.push.ISubscription#getEndpoint()
	 */
	@Override
	public String getEndpoint() {
		return endpoint;
	}

	/* (non-Javadoc)
	 * @see org.condast.commons.messaging.push.ISubscription#getExpirationTime()
	 */
	@Override
	public String getExpirationTime() {
		return expirationTime;
	}

	/**
	 * Returns the base64 encoded auth string as a byte[]
	 */
	public byte[] getAuthAsBytes() {
		return Base64.getDecoder().decode(getAuth());
	}

	public String getKey() {
		return key;
	}

	public String getAuth() {
		return auth;
	}

	/**
	 * Returns the base64 encoded public key string as a byte[]
	 */
	public byte[] getKeyAsBytes() {
		return Base64.getDecoder().decode(getKey());
	}

	/**
	 * Returns the base64 encoded public key as a PublicKey object
	 */
	public PublicKey getUserPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		KeyFactory kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
		ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
		ECPoint point = ecSpec.getCurve().decodePoint(getKeyAsBytes());
		ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);

		return kf.generatePublic(pubSpec);
	}

	public static boolean isValidSubscription( Subscription subscription) {
		if( subscription == null )
			return false;
		return ( subscription != null ) && !StringUtils.isEmpty(subscription.getEndpoint()) &&  
				!StringUtils.isEmpty(subscription.getKey()) && !StringUtils.isEmpty(subscription.getAuth());
	}
}
