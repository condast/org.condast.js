const SUBSCRIBE_URL = 'push/subscribe';

var token=${authentication.commons.token};
var subscriptionId = ${worker.id.create};
	
function registerServiceWorker() { 
	console.log('subscription id: ' + subscriptionId );
	if (!('serviceWorker' in navigator)) {
		console.log("Not in navigator");
		return false;
	}

	if (!('PushManager' in window)) {
		console.log("No manager");
		return false;
	}

	return navigator.serviceWorker.register('${context.commons.push}/push-service.js').then( 
			function( registration) { 
				console.log(' Service worker successfully registered.'); 
				askPermission().then(() => {
					console.log(' Subscribe.'); 
					const APP_SERVER_KEY = '${vapid.public.key}';

					const options = {
							userVisibleOnly: true,
							applicationServerKey: urlBase64ToUint8Array(APP_SERVER_KEY)
					}
					return registration.pushManager.subscribe(options)
				}).then((pushSubscription) => {
					console.log('Subscription successful'); 
					// we got the pushSubscription object
					callServer( subscriptionId, pushSubscription );
				}).catch( function( err) { 
					console.error('Permission denied:', err);
					throw(err);
				});
			}).catch( function( err) { 
				console.error('Unable to register service worker:', err);
				throw(err);
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
		console.log(permissionResult);
		if(( permissionResult !== 'default') && (permissionResult !== 'granted')) { 
			throw new Error(' We weren\'t granted permission.'); 
		} 
	}); 
}

function callServer(subid, subscription) {
	// Get public key and user auth from the subscription object
    var key = subscription.getKey ? subscription.getKey('p256dh') : '';
    var auth = subscription.getKey ? subscription.getKey('auth') : '';

	console.log("fetch from server");
	//Send the subscription details to the server using the Fetch API.
	fetch( SUBSCRIBE_URL + '?id='+ subid + '&token=' + token, {
		method: 'post',
		headers: {
			'Accept': 'text/plain',
			'Cache': 'default',
        	'Content-Type': 'application/json; charset=UTF-8',
        	'Access-Control-Allow-Origin': '*'		
        },
		body: JSON.stringify({
            endpoint: subscription.endpoint,
            // Take byte[] and turn it into a base64 encoded string suitable for
            // POSTing to a server over HTTP
            key: key ? btoa(String.fromCharCode.apply(null, new Uint8Array(key))) : '',
            auth: auth ? btoa(String.fromCharCode.apply(null, new Uint8Array(auth))) : ''
		}),
	}).then( function(response){
		console.log('Processing Response');
		if (!response.ok) {
			throw new Error('An error occurred')
		}
		console.log('Response received');
	    window.location.href='${context.commons.home}?id=' + subscriptionId;
	}).catch( function( err) { 
		console.error('Permission denied:', err);
		throw(err);
	});	
}

//This function is needed because Chrome doesn't accept a base64 encoded string
//as value for applicationServerKey in pushManager.subscribe yet
//https://bugs.chromium.org/p/chromium/issues/detail?id=802280
function urlBase64ToUint8Array(base64String) {
	var padding = '='.repeat((4 - base64String.length % 4) % 4);
	var base64 = (base64String + padding)
	.replace(/\-/g, '+')
	.replace(/_/g, '/');

	var rawData = window.atob(base64);
	var outputArray = new Uint8Array(rawData.length);

	for (var i = 0; i < rawData.length; ++i) {
		outputArray[i] = rawData.charCodeAt(i);
	}
	return outputArray;
}

//Register the service worker
window.addEventListener("load", function(event) {
	registerServiceWorker();
	console.log("Service worker started");
});
