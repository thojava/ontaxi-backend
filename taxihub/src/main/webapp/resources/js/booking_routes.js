function initMap() {
    var mapOptions = {
        zoom: 13,
        center: new google.maps.LatLng(21.0031413, 105.8098352),
        mapTypeId: google.maps.MapTypeId.roadmap
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);

    var locationJsonText = document.getElementById("locationJson").innerHTML;
    var datajson = JSON.parse(locationJsonText);
    var flightPath = new google.maps.Polyline({
        path: datajson,
        geodesic: true,
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 2
    });

    var markers = [];
    for ( var i = 0; i < datajson.length; i++) {
        var location = datajson[i];

        var myLatLng = new google.maps.LatLng(location['lat'], location['lng']);
        var marker = new google.maps.Marker({
            position: myLatLng,
            icon: "http://ontaxi.vn:8080/javax.faces.resource/images/dot.png.jsf",
            title: "" + location['acc'],
            map: map
        });

        // Push marker to markers array
        markers.push(marker);
    }

    flightPath.setMap(map);
}

