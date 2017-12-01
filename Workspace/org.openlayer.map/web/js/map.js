var initialised = false;

function isInitialised(){
	return true;
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
		source: new ol.source.OSM()
	})
	],
<<<<<<< HEAD
	interactions: ol.interaction.defaults({
	    doubleClickZoom :false,
	    dragAndDrop: false,
	    keyboardPan: false,
	    keyboardZoom: false,
	    mouseWheelZoom: false,
	    pointer: false,
	    select: false
	}),
=======
	interactions: null,
>>>>>>> refs/remotes/origin/keesp
	controls: ol.control.defaults({
		attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
			collapsible: false
		})
	}),
	view: view
}); 