var markers = [];

function createMarker( name, latitude, longitude, image ){
	var location = new google.maps.LatLng(latitude, longitude);
	var marker = new google.maps.Marker({
		position: location,
		label: name,
		icon: image,
		map: map
	});
	markers.push( marker );
	google.maps.event.addListener(marker,'click', function() {
		var index = markers.indexOf( marker );
		onMarkerClicked( 'MARKER_CLICKED', name, index.toString() );
	});
	send('CREATE_MARKER', 'COMPLETE');
	return marker;
}

function setMarkerIcon( index, image ){
	markers[index].setIcon( image );
}

//Adds a marker to the map.
function addMarkerWithImage(name, latitude, longitude, image ) {
	createMarker( name, latitude, longitude, image );
}

//Adds a marker to the map.
function addMarker(name, latitude, longitude ) {
	var location = new google.maps.LatLng(latitude, longitude);
	// Add the marker at the clicked location, and add the next-available label
	// from the array of alphabetical characters.
	var marker = new google.maps.Marker({
		position: location,
		label: name,
		map: map
	});
	markers.push( marker );
	google.maps.event.addListener(marker,'click', function() {
		var index = markers.indexOf( marker );
		onMarkerClicked( 'MARKER_CLICKED', name, index.toString() );
	});
	send('ADD_MARKER', 'COMPLETE');
}

//Sets the map on all markers in the array.
function setMapOnAll() {
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(map);
	}
}

//Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(null);
	}
}

//Shows any markers currently in the array.
function showMarkers() {
	setMapOnAll(map);
}

function fitBounds( zoom ) {
	var bounds = new google.maps.LatLngBounds();
	center = bounds.getCenter();
	$.each(markers, function (index, marker) {
		bounds.extend(marker.position);
	});
	map.fitBounds(bounds);

	var listener = google.maps.event.addListener(map, "idle", function() { 
		map.setZoom( parseInt( zoom )); 
		google.maps.event.removeListener(listener); 
	});
}

//Deletes all markers in the array by removing references to them.
function deleteMarkers() {
	clearMarkers();
	markers = [];
}