var markers = [];
var mindex = 0;

function createMarker( name, latitude, longitude, image ){
	var location = new google.maps.LatLng(latitude, longitude);
 	var marker = new google.maps.Marker({
      position: location,
      label: name,
      map: map,
      icon: image
    });
   
    marker.addListener('click', function() {
    	onMarkerClicked( 'MARKER_CLICKED', name );
    });
   //markers[mindex++] = marker;
   send('CREATE_MARKER', 'COMPLETE');
   return marker;
}

//Adds a marker to the map.
function addMarker(name, latitude, longitude, image ) {
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
    marker.addListener('click', function() {
    	onMarkerClicked( 'MARKER_CLICKED', name );
    });
	send('ADD_MARKER', 'COMPLETE');
}

// Sets the map on all markers in the array.
function setMapOnAll(map) {
  for (var i = 0; i < markers.length; i++) {
    markers[i].setMap(map);
  }
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
  setMapOnAll(null);
}

// Shows any markers currently in the array.
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

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
  clearMarkers();
  markers = [];
}