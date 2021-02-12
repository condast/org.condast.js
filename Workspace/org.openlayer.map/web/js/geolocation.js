function getGeoLocation() {
    console.log( "finding Geolocation" );
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(showPosition);
  } else {
    console.log( "Geolocation is not supported by this browser." );
  }
}

function showPosition(position)
{
    var lat = position.coords.latitude;
    var lng = position.coords.longitude;
    jump( lat, lng, 17);
    alert("Current position: " + lat + " " + lng);
	onCallBack( 'get-position', 'position', geometry.getCoordinates() );
}