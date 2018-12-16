const result = checkPush();

function checkPush(){
	if (!('serviceWorker' in navigator)) {
		console.log("Not in navigator");
		return false;
	}

	if (!('PushManager' in window)) {
		console.log("No manager");
		return false;
	}

	window.addEventListener('load', () => {
		navigator.serviceWorker.register('/test/js/push/collin-service.js');

		navigator.serviceWorker.ready
		.then(function(registration) {
			alert('registered');
			//Use the PushManager to get the user’s subscription to the push service.
			return registration.pushManager.getSubscription()
			.then(async function(subscription) {

				//If a subscription was found, return it.
				if (subscription) {
					return subscription;
				}

				//Get the server’s public key
				const response = await fetch('./vapidPublicKey');
				const vapidPublicKey = await response.text();

				//Chrome doesn’t accept the base64-encoded (string) vapidPublicKey yet urlBase64ToUint8Array() is defined in /tools.js
				const convertedVapidKey = urlBase64ToUint8Array(vapidPublicKey);

				//Otherwise, subscribe the user (userVisibleOnly allows to specify that we don’t plan to send notifications that don’t have a visible effect for the user).
				return registration.pushManager.subscribe({
					userVisibleOnly: true,
					applicationServerKey: convertedVapidKey
				});
			});
		}).then(function(subscription) {

			console.log("fetch button");
			//Send the subscription details to the server using the Fetch API.
			fetch('./register', {
				method: 'post',
				headers: {
					'Content-type': 'application/json'
				},
				body: JSON.stringify({
					subscription: subscription
				}),
			});	
			
			console.log("register button");
			document.getElementById('doIt').onclick = function() {
				const delay = document.getElementById('notification-delay').value;
				const ttl = document.getElementById('notification-ttl').value;
				console.log('Requesting notification');
				fetch('./sendNotification', {
					method: 'post',
					headers: {
						'Content-type': 'application/json'
					},
					body: JSON.stringify({
						subscription: subscription,
						delay: delay,
						ttl: ttl,
					}),
				});
			};

		});
	});
	return true;
}