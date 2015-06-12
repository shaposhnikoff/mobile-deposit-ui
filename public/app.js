$(document).ready(function() {
    $.ajax({
        url: "http://mobile-deposit-api/account/deposit"
    }).then(function(data) {
       $('.accountNumber').append(data.accountNumber);
       $('.balance').append(data.balance);
       $('.version').append(data.version);
    });
});
