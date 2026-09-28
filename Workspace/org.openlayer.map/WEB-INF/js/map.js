/**
 * Code based on:
 * @See: https://openlayers.org/en/latest/examples/layer-spy.html
 */

let initialised = false;
let rgb;
let pixelRatio;
let correction = 1.7;

//center on RDM, transforming to map projection
let center = ol.proj.transform([4.42240, 51.9005], 'EPSG:4326', 'EPSG:3857');

let context;

function isInitialised(){
	return true;
}

function initMap(){
	addSelectEvent;
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

function refresh(){
	map.getLayers().forEach(layer => layer.getSource().refresh());	
}

function getLocation(){
	return map.getView().getCenter();
}

function setLocation( latitude, longitude, zoom ){
	let lt = parseFloat(latitude);
	let ln = parseFloat(longitude);
	center = ol.proj.transform([ln, lt], 'EPSG:4326', 'EPSG:3857');
	view.setCenter( center );
	view.setZoom( parseInt( zoom ));
	map.updateSize();
}

function toRadians( angle ){
	let degrees	= ( 360 + angle)%360;
	return degrees*Math.PI/180;
}

function setRotation( angle ){
	map.getView().setRotation( toRadians( angle ));	
	map.updateSize();
}

function getPixel( latitude, longitude ){
	if( context == null )
		return;
	let lat = parseFloat( latitude );
	let lon = parseFloat( longitude );
	let coord = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );
	let pixel = map.getPixelFromCoordinate( coord );
	let pixelAtClick = context.getImageData(pixel[0], pixel[1], 1, 1).data;
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
	let coord1 = ol.proj.transform( [lon1, lat1], 'EPSG:4326', 'EPSG:3857' );
	let pixel1 = map.getPixelFromCoordinate( coord1 );
	
	let coord2 = ol.proj.transform( [lon2, lat2], 'EPSG:4326', 'EPSG:3857' );
	let pixel2 = map.getPixelFromCoordinate( coord2 );
	let results = null;
	try{
		let diff = Math.subtract( pixel2, pixel1);
		let length = parseInt( math.distance( pixel1, pixel2));
		results = new Array(length);
		for( let i=0; i<length; i++){
			let vec = Math.add( pixel1, math.multiply(diff, i/length ));
			results[i] = context.getImageData(vec[0], vec[1], 1, 1).data;
		}
	}
	catch( err ){
		console.log( err );
	}
	return results;
}

function getAreaPixels( ln1, lt1, length, width ){
	if( context == null )
		return;
	let lat1 = parseFloat( lt1 );
	let lon1 = parseFloat( ln1 );
	let coord1 = ol.proj.transform( [lon1, lat1], 'EPSG:4326', 'EPSG:3857' );
	let pixel = map.getPixelFromCoordinate( coord1 );
	let results = new Array(length*width);
	let counter = 0;
	for( let j=0; j<width; j++){
		let y = pixel[1] + parseInt( correction*j/view.getResolution());
		for( let i=0; i<length; i++){
			let x = pixel[0] + parseInt( correction*i/view.getResolution());		
			results[counter] = context.getImageData(x, y, 1, 1).data;
			counter++;
		}
	}
	return results;
}

function getSituationalAwareness( ln1, lt1, radius, sample ){
    if( context == null )
        return;
 
    let lat1 = parseFloat( lt1 );
    let lon1 = parseFloat( ln1 );    
    center = ol.proj.transform( [lon1, lat1], 'EPSG:4326', 'EPSG:3857' );
    let pixel = map.getPixelFromCoordinate( center );

    let projection = view.getProjection();
    let resolutionAtCoords = ol.proj.getPointResolution( projection, view.getResolution(), center );
    
    let adjust = sample*radius;	
    let resolution = resolutionAtCoords*sample;
    let half = parseInt(adjust/2);

    let results = new Array(adjust*adjust);
    
    let counter = 0;
    for( let j=0; j<adjust; j++){
        let y = pixel[1] - parseInt((j-half)/resolution);
        for( let i=0; i<adjust; i++){
            let x = pixel[0] + parseInt((i-half)/resolution);     
            results[counter] = context.getImageData(x, y, 1, 1).data;
            counter++;
        }
    }
	view.setCenter( center );
    return results;
}

/**
* @See: https://codepen.io/mike-000/pen/eYVNGNM
* @See: https://stackoverflow.com/questions/72113059/openlayers-4-getting-pixels-from-a-rotated-map?noredirect=1#comment127421948_72113059
*/
function getAreaPixelsRotation( ln1, lt1, length, width, sample ){
    if( context == null )
        return;
    let lat1 = parseFloat( lt1 );
    let lon1 = parseFloat( ln1 );
    center = ol.proj.transform( [lon1, lat1], 'EPSG:4326', 'EPSG:3857' );
    let pixel = map.getPixelFromCoordinate( center );
    
    let projection = view.getProjection();
    let resolution = sample*ol.proj.getPointResolution( projection, view.getResolution(), center );

    let halfLength = parseInt(sample*length/2);
    let halfWidth = parseInt(sample*width/2 );
	console.log(pixel[0]);
	console.log(pixel[1]);
    let canvas = document.createElement('canvas');
    canvas.width = parseInt( length*resolution);
    canvas.height = parseInt( width*resolution);
    let newContext = canvas.getContext('2d');
    newContext.rotate( view.getRotation());
    newContext.drawImage(context.canvas, -pixel[0], -pixel[1]);

    let results = new Array(sample*length*sample*width);
    let counter = 0;
    for( let j=sample*length; j>=0; j--){
        let y = parseInt((j-halfWidth)/resolution);
        for( let i=0; i<sample*width; i++){
            let x = parseInt((i-halfLength)/resolution);     
            results[counter] = newContext.getImageData(x, y, 1, 1).data;
            counter++;
        }
    }
	view.setCenter( center );
    return results;
}

function getAreaPixelsWithOffset( ln1, lt1, length, width ){
	if( context == null )
		return;
	let lat1 = parseFloat( lt1 );
	let lon1 = parseFloat( ln1 );
	let coord1 = ol.proj.transform( [lon1, lat1], 'EPSG:4326', 'EPSG:3857' );
	let pixel = map.getPixelFromCoordinate( coord1 );
	let results = new Array(length*width);
	let counter = 0;
	let halfLength = pixel[0]-parseInt(length/2);
	for( let j=0; j<width; j++){
		let y = pixel[1] - parseInt( j/view.getResolution());
		for( let i=0; i<length; i++){
			let x = parseInt( (halfLength + i)/view.getResolution());		
			results[counter] = context.getImageData(x, y, 1, 1).data;
			counter++;
		}
	}
	return results;
}

function getAreaPixelsWithAngle( ln1, lt1, length, width, angle ){
	if( context == null )
		return;
	let lat1 = parseFloat( lt1 );
	let lon1 = parseFloat( ln1 );
	let coord1 = ol.proj.transform( [lon1, lat1], 'EPSG:4326', 'EPSG:3857' );
	let pixel = map.getPixelFromCoordinate( coord1 );
	let results = new Array(length*width);
	let counter = 0;
	let halfLength = pixel[0]-parseInt(length/2);
	for( let j=0; j<width; j++){
		let y = correction*j/view.getResolution();
		for( let i=0; i<length; i++){
			let x = correction*i/view.getResolution();
			let xdiff = Math.abs( pixel[0]-x );
			if( xdiff<0.000001)xdiff=0.000001;	
		    let phi = Math.atan(y/(pixel[0]-x)) - toRadians(10+angle );
			let len = Math.sqrt(xdiff*xdiff+y*y);
			let xa = halfLength + parseInt(len*Math.cos(phi));
			let ya = pixel[1] - parseInt(len*Math.sin(phi)); 
			results[counter] = context.getImageData(xa, ya, 1, 1).data;
			counter++;
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
		onCallBack( tp, wktRepresentation, lnglat, null );
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
		let ft = feature;
		let geometry = ft.getGeometry();
		let geomType = geometry.getType();

		//console.log( geometry + ": " + geomType + ": " + tp );
		//Transform the geometry from web mercator (3857) to regular latitude and longitude (4326)
		geometry.transform('EPSG:3857', 'EPSG:4326');

		let lnglat;
		if (geomType === 'Polygon'){
			lnglat = geometry.getCoordinates();  
		}else if (geomType === 'Circle'){
			//Circles to not have a WKT representation, so we approximate this
			//using a polygon
			lnglat = geometry.getCenter();  
			geometry = ol.geom.Polygon.fromCircle(geometry, 12,0);
		}else{
			lnglat = geometry.getCoordinates();  
		}
		let format = new ol.format.WKT();
		let wktRepresentation  = format.writeGeometry(geometry);
		onCallBack( tp, wktRepresentation, lnglat, geomType );
	}
	catch( e ){
		console.log(e);
	}
}

function addSelectEvent(){
	let select = new ol.interaction.Select({
		condition: ol.events.condition.pointerMove,
		style: function(feature) {
			try{
				// Popup showing the position the user clicked
				var popup = new ol.Overlay({
					element: document.getElementById('popup')
				});
				map.addOverlay(popup);	
				popup.setPosition(ft.getGeometry().getCoordinates()); 
				console.log('Selected: ' + feature);
				sendFeature( 'select', feature);      
			}
			catch( e ){
				console.log(e);
			}
		}
	});
	map.addInteraction(select);
}

// view, starting at the center
let view = new ol.View({
	center: center,
	zoom: 17
});

let imagery = new ol.layer.Tile({
	source: new ol.source.OSM(),
    crossOrigin: 'anonymous'
});

// before rendering the layer, determine the pixel ratio
imagery.on('prerender', function(event) {
  context = event.context;
  pixelRatio = event.frameState.pixelRatio;
  context.save();
});

/**
 * Apply a filter on "postcompose" events.
 */
imagery.on('postrender', function(event) {
	context = event.context;
	context.restore();
});

// finally, the map with our custom interactions, controls and overlays
let map = new ol.Map({
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

initMap();