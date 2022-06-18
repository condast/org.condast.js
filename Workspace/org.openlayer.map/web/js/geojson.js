let styles;

let styleFunction = function(feature) {
    return styles[feature.getGeometry().getType()];
};

 function addStyle( jsonStyle ){
	 
 }     
/**
 * Draw a gson object t with the given style
 * @param style
 * @param geojsonObject
 * @returns
 */
function draw( style, geojsonObject ){
    let vectorSource = new ol.source.Vector({
        features: (new ol.format.GeoJSON()).readFeatures(geojsonObject)
      });

    let vectorLayer = new ol.layer.Vector({
        source: vectorSource,
        style: style
      });
	map.addLayer( vector );
	alert('gson');
}