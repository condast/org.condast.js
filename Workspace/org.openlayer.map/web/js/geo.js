//The features are not added to a regular vector layer/source,
//but to a feature overlay which holds a collection of features.
//This collection is passed to the modify and also the draw
//interaction, so that both can add or modify features.
var overlayStyle = new ol.style.Style({
	fill: new ol.style.Fill({
		color: 'rgba(255, 255, 255, 0.2)'
	}),
	stroke: new ol.style.Stroke({
		color: '#ffcc33',
		width: 2
	}),
	image: new ol.style.Circle({
		radius: 7,
		fill: new ol.style.Fill({
			color: '#ffcc33'
		})
	})
});
	

var collection = new ol.Collection();
var featureOverlay = new ol.layer.Vector({
  map: map,
  source: new ol.source.Vector({
    features: collection,
    useSpatialIndex: false // optional, might improve performance
  }),
  style: overlayStyle,
  updateWhileAnimating: true, // optional, for instant visual feedback
  updateWhileInteracting: true // optional, for instant visual feedback
});
featureOverlay.setMap(map);

var select = new ol.interaction.Select({
	  wrapX: false
});

//the SHIFT key must be pressed to delete vertices, so
//that new vertices can be drawn at the same position
//of existing vertices
var modify = new ol.interaction.Modify({
	features: select.getFeatures(),
	deleteCondition: function(event) {
		return ol.events.condition.shiftKeyOnly(event) &&
			ol.events.condition.singleClick(event);
		}
	}
);
map.addInteraction(modify);

var draw; // global so we can remove it later
var pointDraw;
var pointer = 0;

function initInteraction() {
	draw = addInteraction( 'LineString' );
	draw.on('drawend', function(e) {
		sendCoordinates( 'drawend', e );
	});
	draw.on('drawstart', function(e) {
		sendCoordinates( 'drawstart', e );
	});

	pointDraw = addInteraction('Point');
	pointDraw.on('drawend', function(e) {
		sendCoordinates( 'drawend', e );
	});
}

/**
 * Send the given coordinates to the servlet
 * @param coordinates
 */
function sendCoordinates( tp, e ){
	var geometry = e.feature.getGeometry();
	var coords = geometry.getCoordinates();  
	var lnglat = ol.proj.transform( coords, 'EPSG:3857', 'EPSG:4326');
	onCallBack( tp, geometry.getType(), lnglat );
}

function addInteraction( tp) {
	var drw = new ol.interaction.Draw({
		features: select.getFeatures(),
		type: (tp)
	    }
    );
    map.addInteraction(drw);
    return drw;
}

/**
* Let user change the geometry type.
*/
function typeSelect( type ){
 try{
	map.removeInteraction(draw);
	draw = addInteraction( type );
  }
  catch( err ){
	  alert( err );
  }
}

function jump( lat, lon, zoom) {
	var lt = parseFloat( lat );
	var ln = parseFloat( lon );
	var center = ol.proj.transform( [ln, lt], 'EPSG:4326', 'EPSG:3857' );
	view.setCenter( center );
	view.setZoom( parseInt( zoom ));
}

function zoom( zoom ){
    view.setZoom( parseInt( zoom ));
}

function zoomin(){
    view.setZoom( view.getZoom() - 1);
}

function zoomout(){
    view.setZoom( view.getZoom() + 1);
}

initInteraction();