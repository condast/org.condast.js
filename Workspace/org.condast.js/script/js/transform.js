<script>
// A bounce easing method (from https://github.com/DmitryBaranovskiy/raphael).
function bounce(t) {
	var s = 7.5625, p = 2.75, l;
	if (t < (1 / p)) {
		l = s * t * t;
	} else {
		if (t < (2 / p)) {
			t -= (1.5 / p);
			l = s * t * t + 0.75;
		} else {
			if (t < (2.5 / p)) {
				t -= (2.25 / p);
				l = s * t * t + 0.9375;
			} else {
				t -= (2.625 / p);
				l = s * t * t + 0.984375;
			}
		}
	}
	return l;
}

// An elastic easing method (from https://github.com/DmitryBaranovskiy/raphael).
function elastic(t) {
	return Math.pow(2, -10 * t) * Math.sin((t - 0.075) * (2 * Math.PI) / 0.3) + 1;
}

function getLocation( latitude, longitude ){
	var lonlat = new Array(Number(latitude), Number(longitude)); 
	return ol.proj.fromLonLat( lonlat );	
}

/**
 * Various transformations on the map
 * @param location
 * @returns
 */
function doBounce( latitude, longitude) {
	var lonlat = new Array(Number(latitude), Number(longitude)); 
	var location = ol.proj.fromLonLat( lonlat );
    view.animate({
    	center: location,
		easing: bounce
	});
}

function doElastic( latitude, longitude) {
	var location = getLocation( latitude, longitude );
    view.animate({
    	center: location,
    	duration: 2000,
		easing: elastic
	});
}

function doPan( latitude, longitude ) {
	var lonlat = new Array(Number(latitude), Number(longitude)); 
	var location = ol.proj.fromLonLat( lonlat );
    view.animate({
		center: location
	});
}

function doRotate( rad ) {
	// rotate 360 degrees
	var rotate = view.animate({
		rotation: Math.PI * rad
	});
}

function doZoom(factor) {
	// zoom from the current resolution
    var num = Number(factor);
	var zm = view.getZoom();
	view.animate({
		resolution: view.getResolution(),
		zoom: zm + num
	});
}

function setZoom( factor ){
    var num = Number(factor);
	var zm = view.getZoom();
	view.setZoom( zm + num );
}
</script>