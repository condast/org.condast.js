const result = checkPush();

var subscription;

function go(){
	registerServiceWorker();
}

function registerServiceWorker() { 
	return navigator.serviceWorker.register('/test/js/push/collin-service.js') .then( 
		function( registration) { 
				console.log(' Service worker successfully registered.'); 
				return registration; }
		).catch( function( err) { 
				console.error(' Unable to register service worker.', err);
		});
}

function askPermission() { 
	return new Promise( function( resolve, reject) { 
				const permissionResult = Notification.requestPermission( 
					function( result) { 
						resolve( result); }); 
					if (permissionResult) { 
						permissionResult.then( resolve, reject); 
					}
			}).then( function( permissionResult){ 
					if (permissionResult !== 'granted') { 
						throw new Error(' We weren\'t granted permission.'); 
					} 
	}); 
}

function setButton(){
	console.log("register button");
	document.getElementById('doIt').onclick = function() {
		const delay = document.getElementById('notification-delay').value;
		const ttl = document.getElementById('notification-ttl').value;
		console.log('Requesting notification');
		fetch('http://localhost:10080/moodle/module/advice?id=1&token=12&module-id=1&activity-id=1', {
			method: 'get',
			headers: {
				'Content-type': 'application/json'
			},
		});
	};
}

function checkPush(){
	setButton();
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
				const vapidPublicKey = 'BDvq04Lz9f7WBugyNHW2kdgFI7cjd65fzfFRpNdRpa9zWvi4yAD8nAvgb8c8PpRXdtgUqqZDG7KbamEgxotOcaA';

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
			fetch('http://localhost:10080/moodle/module/advice?id=1&token=12&', {
				method: 'post',
				headers: {
					'Content-type': 'application/json'
				},
				body: JSON.stringify({
					subscription: subscription
				}),
			});	

		});	
	});
	return true;
}