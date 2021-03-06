package nl.martijndwars.webpush.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import nl.martijndwars.webpush.Subscription;

@Parameters(separators = "=", commandDescription = "Send a push notification")
public class SendNotificationCommand {
    @Parameter(names = "--subscription", description = "A subscription in JSON format.", required = true)
    private String subscription;

    @Parameter(names = "--publicKey", description = "The public key as base64url encoded string.", required = true)
    private String publicKey;

    @Parameter(names = "--privateKey", description = "The private key as base64url encoded string.", required = true)
    private String privateKey;

    @Parameter(names = "--payload", description = "The message to send.")
    private String payload = "Hello, world!";

    @Parameter(names = "--ttl", description = "The number of seconds that the push service should retain the message.")
    private int ttl;

    public Subscription getSubscription() {
        Gson gson = new Gson();
        return gson.fromJson(subscription, Subscription.class);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPayload() {
        return payload;
    }

    public int getTtl() {
        return ttl;
    }
}
