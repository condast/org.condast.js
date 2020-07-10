const UPDATE_URL = '${context.commons.push}/update';

self.addEventListener('install', function(event) {
  console.log('Installing push service');
  event.waitUntil(self.skipWaiting());
});

self.addEventListener('activate', function(event) {
  console.log('Activating push service');
  event.waitUntil(self.clients.claim());
});

/**
 * Show the push notification
 * @param event
 * @returns
 */
self.addEventListener('push', function(event) {
	const payload = event.data ? event.data.text() : 'no payload';
	if( payload === null )
		return;
	//console.log( payload);
	const options = JSON.parse(payload);
	options.requireInteraction=true;
	const maxVisibleActions = Notification.maxActions; 
	//console.log( options.title);
	if ( options.actions.length > maxVisibleActions) { 
		options.body = 'This notification will only display' +
		'${maxVisibleActions} actions.';
	}

	//	Keep the service worker alive until the notification is created.
	const promiseChain = self.registration.showNotification( options.title, options); 
	event.waitUntil(promiseChain);
});


/**
 * Respond to clicking on the notification
 * @param event
 * @returns
 */
self.addEventListener('notificationclick', function(event) {
	console.log('one');
	event.notification.close();
	const notification = !event.action?'DONT_CARE': event.action;
	console.log(event.notification.data);
	if(( notification == 'HELP') || ( notification == 'PAUSE')){
		const data = event.notification.data;
		const promiseChain = clients.openWindow(data.uri);
		event.waitUntil(promiseChain);
	}
	update( notification, event );
});

self.addEventListener('notificationclose', function(event) {
	console.log('three');
	const dismissedNotification = event.notification;
	const notification = !event.action?'SHUT_UP': event.action;
	console.log('close: ' + notification);
	update( notification, event );
});

function update( notification, event ){
	console.log('Notification closed!' + event.action);
	console.log(notification);
	const data = event.notification.data;
	console.log(data.adviceId);
	const url = UPDATE_URL + '?id='+ 
		data.userId + '&token=12&adviceid=' + data.adviceId + '&notification=' + notification;
	console.log( url );
	fetch(url , {
		method: 'get',
		headers: {
			'Content-type': 'application/json'
		}
	}).then( function(response){
		if (!response.ok) {
			console.log(response.status );
			throw new Error('An error occurred' + response.status);
		}
		console.log('Response received');
		return response;
	});	
}