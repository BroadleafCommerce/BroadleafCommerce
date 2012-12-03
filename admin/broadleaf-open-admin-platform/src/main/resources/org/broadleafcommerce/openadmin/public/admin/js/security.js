$(document).ready(function() {

    //handle login form submission and retain hash in address bar
    $('#login form').on('submit', function() {
        var hash = location.hash;
        if (hash != null && hash.indexOf("#") == 0) {
            hash = hash.substring(1, hash.length);
            hash = "?" + hash;
        }
        var url = $(this).attr('action') + hash;
        $(this).attr('action', url);
        return true;
    });

});