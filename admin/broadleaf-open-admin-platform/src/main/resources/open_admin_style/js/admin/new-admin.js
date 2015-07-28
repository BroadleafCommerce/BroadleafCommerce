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

    $('table.datatable').DataTable();

    $('select:not(".selectize-collection")').find('select:not(".selectize-adder")').selectize({
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
            $(".secondary-nav").hide();
        } else {
            $(".nav-section").removeClass("active");
            $(this).addClass("active");
            $(".secondary-nav").hide();
            $(selector).show();
        }
    });

    $(".nav-close").on("click", function(x) {
        x.preventDefault();
        $(".secondary-nav").hide();
        $(".nav-section").removeClass("active");
    });

    $(document).find(".color-picker").spectrum({
        showButtons: false,
        preferredFormat: "hex6",
        change: function(color) {
            $(this).closest('.field-box').find('input.color-picker-value').val(color);
        },
        move: function(color) {
            $(this).closest('.field-box').find('input.color-picker-value').val(color);
        }
    });
});

$('body').on('change', 'input.color-picker-value', function() {
    var $this = $(this);
    $this.closest('.field-box').find('input.color-picker').spectrum('set', $this.val());
});
