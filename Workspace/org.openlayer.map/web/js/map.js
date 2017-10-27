var initialised = false;

function isInitialised(){
	return true;
}

var layer = new ol.layer.Tile({
	source: new ol.source.OSM()
});

// create an interaction to add to the map that isn't there by default
var interaction = new ol.interaction.DragRotateAndZoom();

// create a control to add to the map that isn't there by default
var control = new ol.control.FullScreen();

// center on RDM, transforming to map projection
var center = ol.proj.transform([4.912, 51.743], 'EPSG:4326', 'EPSG:3857');

// an overlay to position at the center
var overlay = new ol.Overlay({
	position: center,
	element: document.getElementById('overlay')
});

// view, starting at the center
var view = new ol.View({
	center: center,
	zoom: 17
});

// finally, the map with our custom interactions, controls and overlays
var map = new ol.Map({
	target: 'map',
	layers: [layer],
	interactions: [interaction],
	controls: [control],
	overlays: [overlay],
	view: view
}); 