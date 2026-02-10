let collection = new ol.Collection();

let select = new ol.interaction.Select({
	  wrapX: false
});

//the SHIFT key must be pressed to delete vertices, so
//that new vertices can be drawn at the same position
//of existing vertices
let modify = new ol.interaction.Modify({
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
	try{
		draw = new ol.interaction.Draw( 'LineString' );
		draw.on('drawend', function(e) {
			sendCoordinates( 'drawend', e );
		});
		draw.on('drawstart', function(e) {
			sendCoordinates( 'drawstart', e );
		});
	}
	catch( err ){
		console.log( err);
	}

	pointDraw = addInteraction('Point');
	pointDraw.on('drawend', function(e) {
		sendCoordinates( 'drawend', e );
	});
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
    view.setZoom( view.getZoom() + 1);
    console.log( view.getZoom());
}

function zoomout(){
    view.setZoom( view.getZoom() - 1);
    console.log( view.getZoom());
}

initInteraction();