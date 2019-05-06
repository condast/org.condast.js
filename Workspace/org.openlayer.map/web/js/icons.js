/**
 * Initialise the vector source and vector for icon management
 * and add it to the map
 */
var iconVectorSource;
var iconVector;
init();

function init(){
	iconVectorSource = new ol.source.Vector();
	iconVector = new ol.layer.Vector({
	    style: function(feature) {
	        return feature.get('style');
	    },
		source: iconVectorSource
	})
	map.addLayer( iconVector );
}

function createStyle( path, opacity, img ){
	//create the style
	return new ol.style.Style({
        image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
            anchor: [0.5, 0.96],
            crossOrigin: 'anonymous',
            src: path,
            img: img,
            imgSize: img ? [img.width, img.height] : undefined
		}))
	});	
}

//Removes the markers from the map, but keeps them in the array.
function clearIcons() {
	iconVectorSource.clear();
}

function addIcon( id, name, latitude, longitude, path, opacity ){
	var lat = parseFloat( latitude );
	var lon = parseFloat( longitude );
	var coords = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );

	var iconFeature = new ol.Feature( new ol.geom.Point(coords) );
	iconFeature.set('style', createStyle( path, opacity, undefined));
	iconFeature.setId( id );
	iconVectorSource.addFeature( iconFeature );
	addSelectEvent( iconFeature );
	var index = map.getLayers().getLength();
	return index;
}

function replaceIcon( id, path, opacity ){
	var iconFeature = iconVectorSource.getFeatureById(id);
	if( iconFeature != null )
		iconFeature.set('style', createStyle( path, opacity, undefined));
}

function removeIcon( id ){
	var iconFeature = iconVectorSource.getFeatureById(id);
	if( iconFeature != null )
		iconVectorSource.removeFeature( iconVectorSource.getFeatureById(id));
}

function popup( location ){
    // Vienna marker
    var marker = new ol.Overlay({
      position: location,
      positioning: 'center-center',
      element: document.getElementById('marker'),
      stopEvent: false
    });
    map.addOverlay(marker);

    // Vienna label
    var vienna = new ol.Overlay({
      position: pos,
      element: document.getElementById('vienna')
    });
    map.addOverlay(vienna);
}

function addSelectEvent( feature ){
	var select = new ol.interaction.Select({
		condition: ol.events.condition.pointerMove,
		style: function(feature) {
			// Popup showing the position the user clicked
			var popup = new ol.Overlay({
				element: document.getElementById('popup')
			});
			map.addOverlay(popup);	
			popup.setPosition(feature.getGeometry().getCoordinates());       
		}
	});
	map.addInteraction(select);
	//select = new ol.interaction.Select({
	//	condition: ol.events.condition.doubleclick,
	//	style: function(feature) {
	//		sendFeature( 'selected', feature )
	//	}
	//});
	//map.addInteraction(select);
}