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
    let lat = position.coords.latitude;
    let lng = position.coords.longitude;
    let results=[lat, lng ];
    jump( lat, lng, 17);
  	onCallBack( 'get-geo-location', 'position', results );
}