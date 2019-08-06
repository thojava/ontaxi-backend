var config = {
    showMode: 'full',//full | min | none
    top: 5,
    left: 5,
    //right: 810,
    appendToElement: 'callPosition',
    arrowLeft: 155,
    arrowDisplay: 'top',//top | bottom | none

    //list your Stringee Number
    fromNumbers: [{alias: '024436626626', number: '824436626626'}]
};
StringeeSoftPhone.init(config);
var access_token2 = $('#access_token').text();

StringeeSoftPhone.on('displayModeChange', function (event) {
    console.log('displayModeChange', event);
    if (event === 'min') {
        StringeeSoftPhone.config({arrowLeft: 75});
    } else if (event === 'full') {
        StringeeSoftPhone.config({arrowLeft: 155});
    }
});

StringeeSoftPhone.on('incomingCall', function (incomingcall) {
    showCallDialog([{name:'phone', value:incomingcall.fromNumber}]);
});

StringeeSoftPhone.on('requestNewToken', function () {
    console.log('requestNewToken+++++++');
    StringeeSoftPhone.connect(access_token2);
});

StringeeSoftPhone.connect(access_token2);