let coords, length, width;

let field_stroke;
let field_style;

let field_source;

init();

function init(){
	field_source = new ol.source.Vector();

    let fieldLayer = new ol.layer.Vector({
        source: field_source
    });
	
    map.addLayer( fieldLayer );
}

//Removes the shapes from the map.
function clearField() {
	field_source.clear();
}

/**
 * Set the stroke of the shape (line)
 * @param line
 * @param wdth
 * @param fill
 * @returns
 */
function setStroke( colour, wdth ){
	let width = parseFloat( wdth );
	field_stroke = new ol.style.Stroke({color: colour, width: width})	
}

/**
 * Create the required shape
 * @param pnts (amount of points of the shape)
 * @param lngth
 * @param wdth
 * @param angl
 * @returns
 */
function setStyle( pnts, lngth, wdth, angl, scal ){
	let length = parseFloat( lngth );
	let width = parseFloat( wdth );
	let angle = parseFloat( angl );
	let points = parseInt( pnts );
	let scale = parseFloat( scal );
	let image = new ol.style.RegularShape({
		stroke: field_stroke,
		points: points,
		radius: length,
		angle: angle
	});
	image.setScale( scale );
	field_style = new ol.style.Style({
		image: image
	});
}

function setField( latitude, longitude, lngth, wdth ){
	let lat = parseFloat( latitude );
	let lon = parseFloat( longitude );
	coords = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );
	let feature =  new ol.Feature(new ol.geom.Point(coords));
	feature.setStyle( field_style );	
	field_source.addFeature( feature );
	map.updateSize();
 }

function setLineStyle( colour, width ){
	setStroke(colour, width);
	field_style = new ol.style.Style({
		stroke: field_stroke
	});
}

/**
 * Draw a line with the given name between the two latlng coordinates
 * @param name
 * @param lat1
 * @param lon1
 * @param lat2
 * @param lon2
 * @returns
 */
function drawLine( name, lat1, lon1, lat2, lon2 ){
	let points = [];
	let lat = parseFloat( lat1 );
	let lon = parseFloat( lon1 );
	let point1 = [lon, lat];
	points.push( point1);

	lat = parseFloat( lat2 );
	lon = parseFloat( lon2 );
	let point2 = [lon, lat];
	points.push( point2);

	let lineString = new ol.geom.LineString( points );
	lineString.transform('EPSG:4326', 'EPSG:3857');
	
	// create the feature
	let feature = new ol.Feature({
	    geometry: lineString,
	    name: name
	});	
	feature.setStyle( field_style );
	field_source.addFeature( feature );
	return map.getLayers().getLength();
}

/**
 * Draw a shape with the given name between the two latlng coordinates
 * @param name
 * @param lat1
 * @param lon1
 * @param lat2
 * @param lon2
 * @returns
 */
function drawShape( name, lat1, lon1, lat2, lon2 ){
	let points = [];
	let lat = parseFloat( lat1 );
	let lon = parseFloat( lon1 );
	let point1 = [lon, lat];
	points.push( point1);

	lat = parseFloat( lat2 );
	lon = parseFloat( lon2 );
	let point2 = [lon, lat];
	points.push( point2);
	let geometry = new ol.geom.Polygon( points );
	geometry.transform('EPSG:4326', 'EPSG:3857');
	
	let feature = new ol.Feature({
		name: name,
		geometry: geometry
	});
	feature.setStyle( field_style );
	field_source.addFeature( feature );
	return map.getLayers().getLength();
}