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
(function ($, window, undefined) {
    'use strict';
    
    var $doc = $(document),
        Modernizr = window.Modernizr;
    
    $(document).ready(function() {
        $.fn.foundationAlerts           ? $doc.foundationAlerts() : null;
        $.fn.foundationButtons          ? $doc.foundationButtons() : null;
        $.fn.foundationAccordion        ? $doc.foundationAccordion() : null;
        $.fn.foundationNavigation       ? $doc.foundationNavigation() : null;
        $.fn.foundationTopBar           ? $doc.foundationTopBar() : null;
        $.fn.foundationMediaQueryViewer ? $doc.foundationMediaQueryViewer() : null;
        $.fn.foundationTabs             ? $doc.foundationTabs({
            deep_linking: false,
            callback: function() {
                BLCAdmin.initializeFields(BLCAdmin.getActiveTab());
                BLCAdmin.updateFields(BLCAdmin.getActiveTab());
            }
        }) : null;
        $.fn.foundationTooltips         ? $doc.foundationTooltips() : null;
        $.fn.foundationMagellan         ? $doc.foundationMagellan() : null;
        $.fn.foundationClearing         ? $doc.foundationClearing() : null;
        $.fn.placeholder                ? $('input, textarea').placeholder() : null;
        $.fn.broadleafAccordion         ? $doc.broadleafAccordion() : null;
        $.fn.broadleafTabs              ? $doc.broadleafTabs() : null;
        $.fn.broadleafListgrid          ? $doc.broadleafListgrid() : null;
    
        BLCAdmin.initializeFields();

    });

    // UNCOMMENT THE LINE YOU WANT BELOW IF YOU WANT IE8 SUPPORT AND ARE USING .block-grids
    // $('.block-grid.two-up>li:nth-child(2n+1)').css({clear: 'both'});
    // $('.block-grid.three-up>li:nth-child(3n+1)').css({clear: 'both'});
    // $('.block-grid.four-up>li:nth-child(4n+1)').css({clear: 'both'});
    // $('.block-grid.five-up>li:nth-child(5n+1)').css({clear: 'both'});

    // Hide address bar on mobile devices (except if #hash present, so we don't mess up deep linking).
    if (Modernizr.touch && !window.location.hash) {
        $(window).load(function () {
            setTimeout(function () {
                window.scrollTo(0, 1);
            }, 0);
        });
    }
  
    if (!String.prototype.endsWith) {
        String.prototype.endsWith = function(suffix) {
            return this.indexOf(suffix, this.length - suffix.length) !== -1;
        };
    }
      
    if (!Array.prototype.last) {
        Array.prototype.last = function() {
            return this[this.length - 1];
        };
    }
    
    if (!$.prototype.isOverflowed) {
        $.prototype.isOverflowed = function() {
            var element = $(this)
                .clone()
                .css({display: 'inline', width: 'auto', visibility: 'hidden'})
                .appendTo('body');

            var elementWidth = element.width();
            element.remove();
            
            return (elementWidth > $(this).width());
        };
    }
    
    if (!jQuery.fn.outerHTML) {
        jQuery.fn.outerHTML = function() {
            return jQuery('<div />').append(this.eq(0).clone()).html();
        };
    }

    $(document).ready(function() {
        // Send JavaScript errors to the server for logging.
        window.onerror = function(message, url, lineNumber) {
            BLC.ajax({
                url: BLC.servletContext + "/logJavaScriptError",
                type: "POST",
                data: {message: message, url: url, lineNumber: lineNumber},
                cache: false
            }, function(data) {
                // Nothing to do here.
            });

            return false;
        };

        // Synchronize URL hash to tabs
        if($(document).find('.nav-tabs').length > 0) {
            // initial page visit
            if (window.location.hash === '') {
                window.location.hash = $('.nav-tabs li.active > a').attr('href');
            } else {
                $('.nav-tabs li > a[href="' + getCurrentHash() + '"]').click();
            }

            window.onhashchange = function() {
                // If any modals are open when this event is triggered, the modals should be closed.
                BLCAdmin.hideAllModals();

                // if this was a session timeout, don't click a new tab
                if (window.location.hash.indexOf('sessionTimeout') !== -1) {
                    return false;
                }
                $('.nav-tabs li').removeClass('active');
                $('.nav-tabs li > a[href="' + window.location.hash + '"]').closest('li').addClass('active');
                $('.tabs-content .entityFormTab').removeClass('active');
                $('.tabs-content .entityFormTab.' + getCurrentHashVal() + 'Tab').addClass('active');
                $('.nav-tabs li > a[href="' + getCurrentHash() + '"]').click();
            };
            $(document).on('click', '.nav-tabs li > a', function () {
                if (BLCAdmin.getModals().length == 0) {
                    window.location.hash = $(this).attr('href');
                }
            });
        }
    });
  
})(jQuery, this);
