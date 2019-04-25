var obj = document.createElement("audio");
obj.src = "https://www.tulumba.com/MP3/144851/Track%2001.mp3";
obj.volume = 1;
obj.autoPlay = false;
obj.preLoad = true;

function playAudio() {
    if (document.getElementsByClassName("not_viewed_order").length > 0) {
        obj.play();
    }
}