
var shape_source;

var draw;
var geometry;

init();

function init(){
	shape_source = new ol.source.Vector();

	let shapeLayer = new ol.layer.Vector({
		source: shape_source
	});

	map.addLayer( shapeLayer );
}

//Removes the markers from the map, but keeps them in the array.
function clearShapes() {
	shape_source.clear();
	map.removeInteraction(draw);
}

function setShape( name, value ) {
	if (value === 'None')
		return;

	let geometryFunction, maxPoints;
	if (value === 'Square') {
		value = 'Circle';
		geometryFunction = ol.interaction.Draw.createRegularPolygon(4);
	} else if (value === 'Box') {
		value = 'LineString';
		maxPoints = 2;
		geometryFunction = function(coordinates, geometry) {
			if (!geometry) {
				geometry = new ol.geom.Polygon(null);
			}
			var start = coordinates[0];
			var end = coordinates[1];
			geometry.setCoordinates([
				[start, [start[0], end[1]], end, [end[0], start[1]], start]
				]);
			return geometry;
		};
	}
	draw = new ol.interaction.Draw({
		source: shape_source,
		type: /** @type {ol.geom.GeometryType} */ (value),
		geometryFunction: geometryFunction,
		maxPoints: maxPoints
	});
	draw.on('drawend',function(e){
		sendCoordinates( 'drawend', e );
	});	
	map.addInteraction(draw);
}
