$(document).ready(function() {

    //handle login form submission and retain hash in address bar
    $('#login form').on('submit', function() {
        var url = $(this).attr('action') + location.hash;
        $(this).attr('action', url);
        return true;
    });

});