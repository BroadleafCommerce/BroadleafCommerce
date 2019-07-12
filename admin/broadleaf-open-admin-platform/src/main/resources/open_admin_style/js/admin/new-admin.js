/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
$(document).ready(function(){

    equalheight = function(container){

        var currentTallest = 0,
            currentRowStart = 0,
            rowDivs = new Array(),
            $el,
            topPosition = 0;
        $(container).each(function() {

            $el = $(this);
            $($el).height('auto');
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
    };

    $(window).load(function() {
        equalheight('.equals .equal');
    });


    $(window).resize(function(){
        equalheight('.equals .equal');
    });

    window.remodalGlobals = {
        namespace: "modal",
        defaults: {
            hashTracking: false
        }
    };

    var hideSecondaryNav = function() {
        $(".nav-section").removeClass("active");
        $(".secondary-nav.slide-in").removeClass('slide-in slide-open').addClass('slide-out');
        $('.nav-backdrop').remove();
    };

    var showNavigationBackdrop = function() {
        if (!$('.nav-backdrop').length) {
            var backdrop = $('<div>', {
                'class': 'nav-backdrop modal-backdrop',
                'click': function () {
                    hideSecondaryNav();
                }
            });
            backdrop.appendTo($('.main-content'));
        }
    };

    $(".nav-section[data-toggle]").on("click", function(n) {
        n.preventDefault();
        var selector = $(this).data("toggle");

        var selectorHeader = $(this).data("toggle") + '-header';
        $(selectorHeader).css('left', $(selectorHeader).width() * -1 + 'px');
        $(selectorHeader).css('display', 'none');

        showNavigationBackdrop();

        $(this).blur();

        if ($(this).hasClass("active")) {
            hideSecondaryNav();
        } else {
            var isOpen = $('.secondary-nav.slide-in').length;
            $(".nav-section").removeClass("active");
            $(this).addClass("active");
            $(".secondary-nav").hide();
            $(".secondary-nav").removeClass('slide-in slide-open slide-out');
            $(selector).show();
            $(selector).addClass('slide-in');

            if (isOpen) {
                $(selector).addClass('slide-open');
            }
        }
    });
});
