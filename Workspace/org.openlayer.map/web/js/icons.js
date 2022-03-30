/**
 * Initialise the vector source and vector for icon management
 * and add it to the map
 */
let iconVectorSource;
let styleMap;

init();

function init(){
	console.log( "INITALISING ICONS ");
	styleMap = new Map();
	iconVectorSource = new ol.source.Vector();
	let iconVector = new ol.layer.Vector({
	    style: function(feature) {
	        return feature.get('style');
	    },
		source: iconVectorSource
	})
	map.addLayer( iconVector );
}

//Path points to the image
function createStyle( img ){
	//create the style
	let style = styleMap.get( img );
	if( style == null ){
		style = new ol.style.Style({
        	image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
            	anchor: [0.5, 0.96],
            	crossOrigin: 'anonymous',
            	src: img
   			}))
		});	
		styleMap.set( img, style );
	}
	return style;
}

//Removes the markers from the map, but keeps them in the array.
function clearIcons() {
	//console.log( "CLEAR ICONS ");
	iconVectorSource.clear();
	styleMap.clear();
}

//Path: path to the image
function addIcon( id, name, latitude, longitude, img ){
	let lat = parseFloat( latitude );
	let lon = parseFloat( longitude );
	let coords = ol.proj.transform( [lon, lat], 'EPSG:4326', 'EPSG:3857' );

	let iconFeature = new ol.Feature( new ol.geom.Point(coords) );
	iconFeature.set('style', createStyle(img));
	iconFeature.setId( id );
	iconFeature.set( 'name', name );
	iconVectorSource.addFeature( iconFeature );
	addSelectEvent( iconFeature );
}

function addIcons( ...icons ){
	for (i = 0; i < icons.length; i++) {
		let icon = icons[i];
		try{
			addIcon( icon.id, icon.name, icon.latitude, icon.longitude, icon.path);
		}
		catch( e ){ 
	    	console.log( e );
	    }
	}
}

function replaceIcon( id, path ){
	let iconFeature = iconVectorSource.getFeatureById(id);
	if( iconFeature != null )
		iconFeature.set('style', createStyle( path));
}

function removeIcon( id ){
	let iconFeature = iconVectorSource.getFeatureById(id);
	if( iconFeature != null )
		iconVectorSource.removeFeature( iconVectorSource.getFeatureById(id));
}

function popup( location ){
    // Vienna marker
    let marker = new ol.Overlay({
      position: location,
      positioning: 'center-center',
      element: document.getElementById('marker'),
      stopEvent: false
    });
    map.addOverlay(marker);

    // Vienna label
    let vienna = new ol.Overlay({
      position: pos,
      element: document.getElementById('vienna')
    });
    map.addOverlay(vienna);
}

function addSelectEvent( feature ){
	let ft = feature;
	let select = new ol.interaction.Select({
		condition: ol.events.condition.pointerMove,
		style: function(feature) {
			// Popup showing the position the user clicked
			var popup = new ol.Overlay({
				element: document.getElementById('popup')
			});
			map.addOverlay(popup);	
			popup.setPosition(ft.getGeometry().getCoordinates());       
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