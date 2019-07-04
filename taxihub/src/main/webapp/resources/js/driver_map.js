var map;
var markers = []; // Create a marker array to hold your markers

function initMap() {
    var mapOptions = {
        zoom: 12,
        center: new google.maps.LatLng(21.0031413, 105.8098352),
        mapTypeId: google.maps.MapTypeId.roadmap
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);
    reloadMarkers();
}

function reloadMarkers() {
    // Loop through markers and set map to null for each
    for (var j = 0; j < markers.length; j++) {
        markers[j].setMap(null);
    }

    // Reset the markers array
    markers = [];

    var locationJsonText = document.getElementById("mapForm:locationJson").innerHTML;
    var datajson = JSON.parse(locationJsonText);
    for ( var i = 0; i < datajson.length; i++) {
        var location = datajson[i];

        var myLatLng = new google.maps.LatLng(location['latitude'], location['longitude']);
        var marker = new google.maps.Marker({
            position: myLatLng,
            map: map,
            icon: "https://hub.ontaxi.vn/javax.faces.resource/images/car.png.jsf",
            title: location['driverInfo']
        });

        // Push marker to markers array
        markers.push(marker);
    }
}
