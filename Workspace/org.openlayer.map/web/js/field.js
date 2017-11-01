var coords, length, width;

function setField( latitude, longitude, lngth, wdth ){
	var lat = parseFloat( latitude );
	var lon = parseFloat( longitude );
	coords = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );
	length = parseFloat( lngth );
	width = parseFloat( wdth );

	console.log( length + ",  " + width );
	var rect_stroke = new ol.style.Stroke({color: 'black', width: 1});

	var rectangle = new ol.style.Style({
		image: new ol.style.RegularShape({
			stroke: rect_stroke,
			points: 4,
			radius: length,
			radius2: width,
			angle: Math.PI / 4
		})
	});

	var feature =  new ol.Feature(new ol.geom.Point(coords));
	feature.setStyle( rectangle );
	
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