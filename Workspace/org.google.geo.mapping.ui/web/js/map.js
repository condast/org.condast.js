var map;

function initMap() {
	map = new google.maps.Map(document.getElementById('map'), {
		center: {lat: 52.0930043, lng: 5.0704121},
		zoom: 17
	});
	sendInit( 'INIT', 'done')
}

/**
 * Send the given coordinates to the servlet
 * @param coordinates
 */
function sendInit( tp, e ){
	var tkn = 9812365834502354646;
	$.get("GeoCoderServlet", { token: tkn, type: tp, data: e }).done( function(data) {
		  console.log("data sent: " + data);
	});
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