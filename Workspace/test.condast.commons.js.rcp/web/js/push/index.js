const result = go();

function go(){
	window.addEventListener('load', () => {
		registerServiceWorker();
	});
}

function registerServiceWorker() { 
	if (!('serviceWorker' in navigator)) {
		console.log("Not in navigator");
		return false;
	}

	if (!('PushManager' in window)) {
		console.log("No manager");
		return false;
	}

	return navigator.serviceWorker.register('/test/js/push/collin-service.js') .then( 
			function( registration) { 
				console.log(' Service worker successfully registered.'); 
				askPermission().then(() => {
					console.log(' Subscribe.'); 
					const APP_SERVER_KEY = 'BDvq04Lz9f7WBugyNHW2kdgFI7cjd65fzfFRpNdRpa9zWvi4yAD8nAvgb8c8PpRXdtgUqqZDG7KbamEgxotOcaA';

					const options = {
							userVisibleOnly: true,
							applicationServerKey: urlBase64ToUint8Array(APP_SERVER_KEY)
					}
					return registration.pushManager.subscribe(options)
				}).then((pushSubscription) => {
					console.log(' Subscription successful'); 
					// we got the pushSubscription object
					callServer( pushSubscription );
					setButton( pushSubscription);
				}).catch( function( err) { 
					console.error(' Unable to register service worker.', err);
				});
			});
}

function askPermission() { 
	console.log("ask permission");
	return new Promise( function( resolve, reject) { 
		const permissionResult = Notification.requestPermission( 
				function( result) { 
					resolve( result); 
				}); 
		console.log( permissionResult);
		if (permissionResult) { 
			permissionResult.then( resolve, reject); 
		}
	}).then( function( permissionResult){ 
		if (permissionResult !== 'granted') { 
			throw new Error(' We weren\'t granted permission.'); 
		} 
	}); 
}

function callServer(subscription) {
	// Get public key and user auth from the subscription object
    var key = subscription.getKey ? subscription.getKey('p256dh') : '';
    var auth = subscription.getKey ? subscription.getKey('auth') : '';

	console.log("fetch from server");
	//Send the subscription details to the server using the Fetch API.
	fetch('http://localhost:10080/moodle/push/subscribe?id=1&token=12', {
		method: 'post',
		headers: {
			'Content-type': 'application/json'
		},
		body: JSON.stringify({
            endpoint: subscription.endpoint,
            // Take byte[] and turn it into a base64 encoded string suitable for
            // POSTing to a server over HTTP
            key: key ? btoa(String.fromCharCode.apply(null, new Uint8Array(key))) : '',
            auth: auth ? btoa(String.fromCharCode.apply(null, new Uint8Array(auth))) : ''
		}),
	}).then( function(response){
		if (!response.ok) {
			throw new Error('An error occurred')
		}
		console.log('Response received');
		return response;
	});	
}

function setButton(){
	console.log("register button: " );
	document.getElementById('doIt').onclick = function() {
		const delay = document.getElementById('notification-delay').value;
		const ttl = document.getElementById('notification-ttl').value;
		console.log('Requesting notification');
		pushButton();
	};
	console.log('Notification sent');
};

function pushButton() {
	console.log("Push the button");
	//Send the subscription details to the server using the Fetch API.
	fetch('http://localhost:10080/moodle/module/advice?id=1&token=12&module-id=1&advice-id=1&progress=20', {
		method: 'get',
		headers: {
			'Content-type': 'application/json'
		}
	}).then( function(response){
		if (!response.ok) {
			throw new Error('An error occurred')
		}
		console.log('Response received');
		return response;
	});	
}