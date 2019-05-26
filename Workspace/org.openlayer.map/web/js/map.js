/**
 * Code based on:
 * @See; https://openlayers.org/en/v4.6.5/examples/layer-spy.html
 */
var initialised = false;
var rgb;
var pixelRatio;

//center on RDM, transforming to map projection
var center = ol.proj.transform([4.42240, 51.9005], 'EPSG:4326', 'EPSG:3857');

var context;

function isInitialised(){
	return true;
}

/**
  * Clear the interactions
*/
function clear(){
	let iter = map.getInteractions();
	let i=0;
	for( i=0; i<iter.length; i++ ) { 
    	map.removeInteraction(iter[i]); 
	}
}

function getPixel( latitude, longitude ){
	let lat = parseFloat( latitude );
	let lon = parseFloat( longitude );
	var coord = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );
	var pixel = map.getPixelFromCoordinate( coord );
	if( context == null )
		return;
	var pixelAtClick = context.getImageData(pixel[0]*pixelRatio, pixel[1]*pixelRatio, 1, 1).data;
	rgb = [0,0,0,0];
	for( var i=0;i<pixelAtClick.length;i++ ){
		rgb[i] = pixelAtClick[i];
	}
	return rgb;
}

function getPixels( ln1, lt1, ln2, lt2 ){
	if( context == null )
		return;
	let lat1 = parseFloat( lt1 );
	let lon1 = parseFloat( ln1 );
	let lat2= parseFloat( lt2 );
	let lon2 = parseFloat( ln2 );
	var coord1 = ol.proj.transform( [lon1, lat1], 'EPSG:4326', 'EPSG:3857' );
	var pixel1 = map.getPixelFromCoordinate( coord1 );
	
	var coord2 = ol.proj.transform( [lon2, lat2], 'EPSG:4326', 'EPSG:3857' );
	var pixel2 = map.getPixelFromCoordinate( coord2 );
	var xdiff = pixel2[0]-pixel1[0];
	var ydiff = pixel2[1]-pixel1[1];
	var length = Math.round( Math.sqrt( xdiff*xdiff+ ydiff*ydiff ));
	//var sign = (xdiff>=0)?1:-1;
	var sinphi = parseFloat( ydiff/length );
	var cosphi = parseFloat( xdiff/length );
	var results = new Array(length);
	var maxx = 0;
	for( var i=0; i<length; i++){
		let x = pixel1[0] + i*cosphi;
		let y = pixel1[1] + i*sinphi;
		results[i] = context.getImageData(x*pixelRatio, y*pixelRatio, 1, 1).data;
	}
	return results;
}

function getAreaPixels( ln1, lt1, length, width ){
	if( context == null )
		return;
	let lat1 = parseFloat( lt1 );
	let lon1 = parseFloat( ln1 );
	var coord1 = ol.proj.transform( [lon1, lat1], 'EPSG:4326', 'EPSG:3857' );
	var pixel1 = map.getPixelFromCoordinate( coord1 );
	var results = new Array(length);
	for( var i=0; i<length; i++){
		let x = pixel1[0] + i;		
		for( var j=0; j<width; j++){
			let y = pixel1[1] + j;
			results[i] = context.getImageData(x*pixelRatio, y*pixelRatio, 1, 1).data;
		}
	}
	return results;
}
/**
 * Send the given coordinates as a JAVA callback
 * @param coordinates
 */
function sendCoordinates( tp, e ){
	try{
		let geometry = e.feature.getGeometry();		
		//Transform the geometry from web mercator (3857) to regular latitude and longitude (4326)
		geometry.transform('EPSG:3857', 'EPSG:4326');
		let lnglat = geometry.getCoordinates();  
		let format = new ol.format.WKT();
		let wktRepresentation  = format.writeGeometry(geometry);
		onCallBack( tp, wktRepresentation, lnglat );
	}
	catch( e ){
		console.log(e);
	}
}

/** Send the given coordinates as a JAVA callback
* @param coordinates
*/
function sendFeature( tp, feature ){
	try{
		let geometry = feature.getGeometry();
		console.log( geometry);
		//Transform the geometry from web mercator (3857) to regular latitude and longitude (4326)
		geometry.transform('EPSG:3857', 'EPSG:4326');
		let lnglat = geometry.getCoordinates();  
		let format = new ol.format.WKT();
		let wktRepresentation  = format.writeGeometry(geometry);
		onCallBack( tp, wktRepresentation, lnglat );
	}
	catch( e ){
		console.log(e);
	}
}
// view, starting at the center
var view = new ol.View({
	center: center,
	zoom: 17
});

var imagery = new ol.layer.Tile({
	source: new ol.source.OSM(),
    crossOrigin: 'anonymous'
});


// before rendering the layer, determine the pixel ratio
imagery.on('precompose', function(event) {
  context = event.context;
  pixelRatio = event.frameState.pixelRatio;
  context.save();
});

/**
 * Apply a filter on "postcompose" events.
 */
imagery.on('postcompose', function(event) {
	context = event.context;
	context.restore();
});

// finally, the map with our custom interactions, controls and overlays
var map = new ol.Map({
	target: 'map',
	layers: [imagery],
	interactions: ol.interaction.defaults({
	    doubleClickZoom :false,
	    dragAndDrop: false,
	    keyboardPan: false,
	    keyboardZoom: false,
	    mouseWheelZoom: false,
	    pointer: false,
	}),
	controls: ol.control.defaults({
		attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
			collapsible: false
		})
	}),
	view: view
});