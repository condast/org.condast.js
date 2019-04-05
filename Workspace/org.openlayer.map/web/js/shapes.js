
var shape_source;
var shape_layer;

var draw;
var geometry;

init();

function init(){
	shape_source = new ol.source.Vector();
	shape_layer = new ol.layer.Vector({
		source: shape_source
	});

	map.addLayer( shape_layer );
}

//Removes the markers from the map, but keeps them in the array.
function clearShapes() {
	shape_source.clear();
	//shape_layer.clear();
	map.removeInteraction(draw);
}

function setShape( name, value ) {
	if(draw != null )
		map.removeInteraction(draw);
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

function addShape( wkt_str){
	let format = new ol.format.WKT();
	let geometry = format.readGeometry(wkt_str );
	geometry.transform('EPSG:4326', 'EPSG:3857');
	let feature = new ol.Feature({
		name: name,
		geometry: geometry
	});
	shape_source.addFeature( feature );
	onCallBack( 'add-shape', wkt_str, geometry.getCoordinates() );
}

function getShape( name ){
	let format = new ol.format.WKT();
	let feature = shape_source.getFeature( name );
	let wktRepresentation  = format.writeGeometry( feature.geometry);
	onCallBack( 'get-shape', wktRepresentation, name );
}

function removeShape( name ){
	shape_source.removeFeature( name );
}