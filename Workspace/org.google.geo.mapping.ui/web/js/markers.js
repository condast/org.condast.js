function createMarker( name, latitude, longtitude, image ){
	var location = new google.maps.LatLng(latitude, longtitude);
    //var image = 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png';
    var marker = new google.maps.Marker({
      position: location,
      label: name,
      map: map,
      icon: image
    });
    
    marker.addListener('click', function() {
    	send( 'TYPE', 'marker:' + name );
      });
    return marker;
}

//Adds a marker to the map.
function addEaterMarker(name, latitude, longtitude ) {
    var image = 'images/restaurant-32.png';
    var marker = createMarker( name, latitude, longtitude, image );
}

//Adds a marker to the map.
function addMarker(name, latitude, longtitude ) {
	var location = new google.maps.LatLng(latitude, longtitude);
	// Add the marker at the clicked location, and add the next-available label
	// from the array of alphabetical characters.
	var marker = new google.maps.Marker({
		position: location,
		label: name,
		map: map
	});
    marker.addListener('click', function() {
    	send( 'TYPE', 'marker:' + name );
      });
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

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
  clearMarkers();
  markers = [];
}