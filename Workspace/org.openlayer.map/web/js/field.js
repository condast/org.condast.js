var coords, length, width;

var field_stroke;
var field_style;

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

function setStyle( pnts, lngth, wdth, angle ){
	var length = parseFloat( lngth );
	var width = parseFloat( wdth );
	var points = parseInt( pnts );
	field_style = new ol.style.Style({
		image: new ol.style.RegularShape({
			stroke: field_stroke,
			points: points,
			radius: length,
			radius2: width,
			angle: angle
		})
	});
}

function setField( latitude, longitude, lngth, wdth ){
	var lat = parseFloat( latitude );
	var lon = parseFloat( longitude );
	coords = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );
	length = parseFloat( lngth );
	width = parseFloat( wdth );

	console.log( length + ",  " + width );
	setStroke('red', '2');
	//field_stroke = new ol.style.Stroke({color: 'black', width: 1});
	var angle = Math.PI / 4 ;
	setStyle('4', length, width, angle);

	var feature =  new ol.Feature(new ol.geom.Point(coords));
	feature.setStyle( field_style );
	
	var shapes = new Array(1);
	shapes[0] = feature;

	var shape_source = new ol.source.Vector({
        features: shapes
    });

    var fieldLayer = new ol.layer.Vector({
        source: shape_source
    });
	
    map.addLayer( fieldLayer );
}

function setLineStyle( colour, wdth ){
	field_style = new ol.style.Style({
		color: colour,
		stroke: field_stroke
	});
}

function drawLine( name, lat1, lon1, lat2, lon2 ){
	var linearr = new Array(2);
	var lat = parseFloat( lat1 );
	var lon = parseFloat( lon1 );
	coords = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );
	linearr[0] = coords;

	lat = parseFloat( lat2 );
	lon = parseFloat( lon2 );
	coords = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );

	var lineString = new ol.geom.LineString(coords);
	// transform to EPSG:3857
	//lineString.transform('EPSG:4326', 'EPSG:3857');
	setLineStyle('blue', 3);
	
	// create the feature
	var feature = new ol.Feature({
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