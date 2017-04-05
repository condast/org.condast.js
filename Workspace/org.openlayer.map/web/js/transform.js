/**
 * Various transformations on the map
 * @param location
 * @returns
 */
function doBounce( latitude, longitude) {

    var location = ol.proj.transform([ latitude, longitude], 'EPSG:4326', 'EPSG:3857');

    // bounce by zooming out one level and back in
	var bounce = ol.animation.bounce({
		resolution: map.getView().getResolution() * 2
	});
	// start the pan at the current center of the map
	var pan = ol.animation.pan({
		source: map.getView().getCenter()
	});
	map.beforeRender(bounce);
	map.beforeRender(pan);
	// when we set the center to the new location, the animated move will
	// trigger the bounce and pan effects
	map.getView().setCenter(location);
}

function doPan( latitude, longitude ) {
	var location = ol.proj.transform([ latitude, longitude], 'EPSG:4326', 'EPSG:3857');

    //alert(ol.animation);
	// pan from the current center
	var pan = ol.View#animate.pan({
		source: map.getView().getCenter()
	});
    alert(pan);
	map.beforeRender(pan);
	// when we set the new location, the map will pan smoothly to it
    alert('hoi3');
	map.getView().setCenter(location);
	alert(location);
}

function doRotate( rad ) {
	// rotate 360 degrees
	var rotate = ol.animation.rotate({
		rotation: Math.PI * rad
	});
	map.beforeRender(rotate);
}

function doZoom(factor) {
	// zoom from the current resolution
	var zoom = ol.animation.zoom({
		resolution: map.getView().getResolution()
	});
	map.beforeRender(zoom);
	// setting the resolution to a new value will smoothly zoom in or out
	// depending on the factor
	map.getView().setResolution(map.getView().getResolution() * factor);
}