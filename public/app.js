$(document).ready(function() {
    $.ajax({
        url: window.location.protocol + "//" + window.location.host + ":8080/account/deposit",
        dataType: "json"
    }).then(function(data) {
       $('.accountNumber').append(data.accountNumber);
       $('.balance').append(data.balance);
       $('.version').append(data.version);
    });
});
