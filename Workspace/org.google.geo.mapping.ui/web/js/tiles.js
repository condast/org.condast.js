var TILE_SIZE = 256;

var tileSize = TILE_SIZE;

function setTileSize( tile ){
	tileSize = tile;
}

function setLocation( latitude, longtitude, zoom ){
	var location = new google.maps.LatLng( latitude, longtitude);
    map.setZoom( zoom );
	map.setCenter( location);	
}

function createLocationInfo( name, description, latitude, longtitude, zoom ){
	var location = new google.maps.LatLng(latitude, longtitude);
	var coordInfoWindow = new google.maps.InfoWindow();
	map.setZoom(zoom);
	coordInfoWindow.setContent(createInfoWindowContent(location, map.getZoom(), name + ": " + description));
	coordInfoWindow.setPosition(location);
	coordInfoWindow.open(map);

	map.addListener('zoom_changed', function() {
		coordInfoWindow.setContent(createInfoWindowContent(location, map.getZoom()));
		coordInfoWindow.open(map);
	});	
}

function createInfoWindowContent(latLng, zoom, text) {
	var scale = 1 << zoom;

	var worldCoordinate = project(latLng);

	var pixelCoordinate = new google.maps.Point(
			Math.floor(worldCoordinate.x * scale),
			Math.floor(worldCoordinate.y * scale));

	var tileCoordinate = new google.maps.Point(
			Math.floor(worldCoordinate.x * scale / tileSize),
			Math.floor(worldCoordinate.y * scale / tileSize));

	return [
	        text,
	        'LatLng: ' + latLng,
	        'Zoom level: ' + zoom,
	        'World Coordinate: ' + worldCoordinate,
	        'Pixel Coordinate: ' + pixelCoordinate,
	        'Tile Coordinate: ' + tileCoordinate
	        ].join('<br>');
}

// The mapping between latitude, longitude and pixels is defined by the web
// mercator projection.
function project(latLng) {
	var siny = Math.sin(latLng.lat() * Math.PI / 180);

	// Truncating to 0.9999 effectively limits latitude to 89.189. This is
	// about a third of a tile past the edge of the world tile.
	siny = Math.min(Math.max(siny, -0.9999), 0.9999);

	return new google.maps.Point(
			tileSize * (0.5 + latLng.lng() / 360),
			tileSize * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)));
}