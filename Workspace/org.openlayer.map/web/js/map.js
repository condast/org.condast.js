var initialised = false;

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

/**
 * Send the given coordinates as a JAVA callback
 * @param coordinates
 */
function sendCoordinates( tp, e ){
	try{
		let geometry = e.feature.getGeometry();
		console.log(geometry.getType());
		let coords = geometry.getCoordinates();  
		let lnglat = ol.proj.transform( coords, 'EPSG:3857', 'EPSG:4326');
		let format = new ol.format.WKT();
		let wktRepresenation  = format.writeGeometry(geometry);
		onCallBack( tp, wktRepresenation, lnglat );
	}
	catch( e ){
		console.log(e);
	}
}

// center on RDM, transforming to map projection
var center = ol.proj.transform([4.912, 51.743], 'EPSG:4326', 'EPSG:3857');

// view, starting at the center
var view = new ol.View({
	center: center,
	zoom: 17
});

// finally, the map with our custom interactions, controls and overlays
var map = new ol.Map({
	target: 'map',
	layers: [new ol.layer.Tile({
		source: new ol.source.OSM(),
        crossOrigin: 'anonymous'
	})
	],
	interactions: ol.interaction.defaults({
	    doubleClickZoom :false,
	    dragAndDrop: false,
	    keyboardPan: false,
	    keyboardZoom: false,
	    mouseWheelZoom: false,
	    pointer: false,
	    select: false
	}),
	controls: ol.control.defaults({
		attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
			collapsible: false
		})
	}),
	view: view
});