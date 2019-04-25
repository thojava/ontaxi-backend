function initMap() {
    var fromLocation = document.getElementById('newOrderForm:fromLocation');
    console.log(fromLocation);
    var fromAutoComplete = new google.maps.places.Autocomplete(fromLocation);
    fromAutoComplete.addListener('place_changed', function () {
        var address_components = fromAutoComplete.getPlace().address_components;
        document.getElementById("newOrderForm:fromDistrict").value = "";
        for (var i = 0; i < address_components.length; i++) {
            var addressType = address_components[i].types[0];
            var elementsByClassName = fromLocation.parentNode.getElementsByClassName(addressType);
            if (elementsByClassName.length > 0) {
                elementsByClassName[0].value = address_components[i]['short_name'];
            }
        }
    });

    var toLocation = document.getElementById('newOrderForm:toLocation');
    var toAutoComplete = new google.maps.places.Autocomplete(toLocation);
    toAutoComplete.addListener('place_changed', function () {
        document.getElementById("newOrderForm:toDistrict").value = "";
        var address_components = toAutoComplete.getPlace().address_components;
        for (var i = 0; i < address_components.length; i++) {
            var addressType = address_components[i].types[0];
            var elementsByClassName = toLocation.parentNode.getElementsByClassName(addressType);
            if (elementsByClassName.length > 0) {
                elementsByClassName[0].value = address_components[i]['short_name'];
            }
        }
    });
}