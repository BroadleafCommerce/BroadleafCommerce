$(document).ready(function(){

    equalheight = function(container){

        var currentTallest = 0,
            currentRowStart = 0,
            rowDivs = new Array(),
            $el,
            topPosition = 0;
        $(container).each(function() {

            $el = $(this);
            $($el).height('auto')
            topPostion = $el.position().top;

            if (currentRowStart != topPostion) {
                for (currentDiv = 0 ; currentDiv < rowDivs.length ; currentDiv++) {
                    rowDivs[currentDiv].height(currentTallest);
                }
                rowDivs.length = 0; // empty the array
                currentRowStart = topPostion;
                currentTallest = $el.height();
                rowDivs.push($el);
            } else {
                rowDivs.push($el);
                currentTallest = (currentTallest < $el.height()) ? ($el.height()) : (currentTallest);
            }
            for (currentDiv = 0 ; currentDiv < rowDivs.length ; currentDiv++) {
                rowDivs[currentDiv].height(currentTallest);
            }
        });
    }

    $(window).load(function() {
        equalheight('.equals .equal');
    });


    $(window).resize(function(){
        equalheight('.equals .equal');
    });

    $('#datetimepicker').datetimepicker({
        format:'l, F d, Y \@ g:ia',
        step: 10,
    });

    $('.help-tip').tipr({
        'speed': 300,
        'mode': 'top'
    });

    $('.datepicker').pickadate({
        format: 'yyyy-mm-dd'
    });

    $('.timepicker').pickatime({
        format: 'h:i A',
        formatSubmit: 'H:i:s',
        interval: 15
    });

    $('#data-table').DataTable({
        "order": [[ 0, "asc" ]],
        paging: true
    });

});
