var player;
var wdt;
var hgt;
var vidid;
var time_update_interval;
var refresh;

// 3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.
function onYouTubeIframeAPIReady() {
  player = new YT.Player('player', {
    height: hgt,
    width: wdt,
    videoId: vidid,
    events: {
      'onReady': onPlayerReady,
      'onStateChange': onPlayerStateChange
   }
  });
}

// 2. This code loads the IFrame Player API code asynchronously.
function initialise( width, height, videoId, rfrsh ){
  var tag = document.createElement('script');
  wdt = width;
  hgt = height;
  vidid = videoId;
  refresh = rfrsh;
  
  tag.src = "https://www.youtube.com/iframe_api";
  tag.class ="container";
  var firstScriptTag = document.getElementsByTagName('script')[0];
  firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
  
  // Clear any old interval.
  clearInterval(time_update_interval);

  // Start interval to update elapsed time display and
  // the elapsed part of the progress bar every second.
  time_update_interval = setInterval(function () {
     try{
       getPlayerData( player.getDuration(), player.getCurrentTime() );
     }catch( e ){
     	console.log( e );
     } 
  }, refresh) 
}

 // 4. The API will call this function when the video player is ready.
 function onPlayerReady(event) {
   getPlayerData( player.getDuration(), player.getCurrentTime() ); 
   event.target.playVideo();
 }

 // 5. The API calls this function when the player's state changes.
 //    The function indicates that when playing a video (state=1),
 //    the player should play for six seconds and then stop.
 var done = false;
 function onPlayerStateChange(event) {
   if (event.data == YT.PlayerState.PLAYING && !done) {
     //setTimeout(stopVideo, 6000);
     //done = true;
   }
 }
 
 //Set the loop function
 function setLoop( choice ){
 	player.setLoop( choice );
 }
 
 function changeVideo( videoId ){
 	try{
 	  player.stopVideo();
 	  player.loadVideoById(videoId);
 	  player.playVideo();
 	}
 	catch( e ){
 	  console.log( e );
 	}
 }
 
 function stopVideo() {
   player.stopVideo();
 }