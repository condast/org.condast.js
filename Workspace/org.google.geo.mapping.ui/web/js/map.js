var map;
var initialised = false;

function initMap() {
	map = new google.maps.Map(document.getElementById('map'), {
		center: {lat: 52.0930043, lng: 5.0704121},
		zoom: 17
	});
	initialised = true;
	return initialised;
}

function isInitialised(){
	return initialised;
}

/**
 * Send the given coordinates to the servlet
 * @param coordinates
 */
function send( tp, e ){
	var tkn = 9812365834502354646;
    try{
    	jsExecuted( tp, tkn, e, true );
		console.log("callback: " + tp);
    }catch(err){
    	console.log( err );
    }
}