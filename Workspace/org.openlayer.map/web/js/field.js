var coords, length, width;

var field_stroke;
var field_style;

var field_features = [];

/**
 * Set the stroke of the shape (line)
 * @param line
 * @param wdth
 * @param fill
 * @returns
 */
function setStroke( colour, wdth ){
	var width = parseFloat( wdth );
	field_stroke = new ol.style.Stroke({color: colour, width: width})	
}

function setStyle( pnts, lngth, wdth, angl ){
	var length = parseFloat( lngth );
	var width = parseFloat( wdth );
	var angle = parseFloat( angl );
	var points = parseInt( pnts );
	field_style = new ol.style.Style({
		image: new ol.style.RegularShape({
			stroke: field_stroke,
			points: points,
			radius: length,
			angle: angle
		})
	});
}

function setField( latitude, longitude, lngth, wdth ){
	var lat = parseFloat( latitude );
	var lon = parseFloat( longitude );
	coords = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );
	var feature =  new ol.Feature(new ol.geom.Point(coords));
	feature.setStyle( field_style );
	
	field_features.push( feature );

	var shape_source = new ol.source.Vector({
        features: field_features
    });

    var fieldLayer = new ol.layer.Vector({
        source: shape_source
    });
	
    map.addLayer( fieldLayer );
 }

function setLineStyle( colour, wdth ){
	setStroke(wdth);
	field_style = new ol.style.Style({
		color: colour,
		stroke: field_stroke
	});
}

/**
 * Draw a lint withe the given name between the two latlng coordinates
 * @param name
 * @param lat1
 * @param lon1
 * @param lat2
 * @param lon2
 * @returns
 */
function drawLine( name, lat1, lon1, lat2, lon2 ){
	var points = [];
	var lat = parseFloat( lat1 );
	var lon = parseFloat( lon1 );
	var point1 = [lon, lat];
	points.push( point1);

	lat = parseFloat( lat2 );
	lon = parseFloat( lon2 );
	var point2 = [lon, lat];
	points.push( point2);

	var lineString = new ol.geom.LineString( points );
	lineString.transform('EPSG:4326', 'EPSG:3857');
	
	// create the feature
	var feature = new ol.Feature({
	    geometry: lineString,
	    name: name
	});	

	var source = new ol.source.Vector({
		features: [feature]
	});

	var vector = new ol.layer.Vector({
		source: source,
		style: [field_style]
	});

	map.addLayer( vector );
}