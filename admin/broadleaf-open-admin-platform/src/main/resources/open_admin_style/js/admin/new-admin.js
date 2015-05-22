/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
$(document).ready(function(){
    $(".secondary-nav").css("width", 0);
    $(".secondary-nav").show();

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

    $('.datetimepicker').datetimepicker({
        format:'l, F d, Y \@ g:ia',
        step: 10
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

    $('table.datatable').DataTable();

    $('select').selectize({
        sortField: 'text'
    });


    $('.radio-label').on("click", function(e) {
        e.preventDefault();
        $(this).prev('input').prop("checked", true).change();
    });

    window.remodalGlobals = {
        namespace: "modal",
        defaults: {
            hashTracking: false
        }
    };

    $(".nav-section[data-toggle]").on("click", function(n) {
        n.preventDefault();
        var selector = $(this).data("toggle");

        $(this).blur();

        if ($(this).hasClass("active")) {
            $(".nav-section").removeClass("active");
            close($(".secondary-nav"));
        } else {
            var el1 = $(".nav-section.active");
            if (el1.length != 0 ) close($(".secondary-nav"));

            $(".nav-section").removeClass("active");
            $(this).addClass("active");

            open($(selector));
        }
    });

    $(".nav-close").on("click", function(x) {
        x.preventDefault();
        $(".nav-section").removeClass("active");
        close($(".secondary-nav"));
    });
});

$(".nav-section[data-toggle]").on("click", function(n) {
    n.preventDefault();
    var selector = $(this).data("toggle");

    $(this).blur();

    if ($(this).hasClass("active")) {
        $(".nav-section").removeClass("active");
        close($(".secondary-nav"));
    } else {
        var el1 = $(".nav-section.active");
        if (el1.length != 0 ) close($(".secondary-nav"));

        $(".nav-section").removeClass("active");
        $(this).addClass("active");

        open($(selector));
    }
});

function open(el) {
    $(el).animate({
        width: "301px"
    }, function() {
        // Animation complete.
    });
}

function close(el) {
    $(el).animate({
        width: "0"
    }, function() {
        // Animation complete.
    });
}
