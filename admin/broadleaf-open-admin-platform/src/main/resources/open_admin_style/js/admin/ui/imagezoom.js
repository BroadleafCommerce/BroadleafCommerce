/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
(function($, BLCAdmin) {
    
    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.imagezoom = {
        
        showImage : function(img) {
    	    // Create the container div that will house both the spinner for loading and the image itself
            var $img = $(img);
    	    var $container = $('<div>', {
    	        'id' : 'thumbnail-zoom'
    	    });
            $container.css('left', $img.offset().left + $img.outerWidth() + 5);
            $container.css('top', $img.offset().top);
    	    $container.append($('<i>', { 'class' : 'icon-spin icon-spinner' }));
    	    $('body').append($container);
    	    
    	    // Create the container image
    	    var url = $img.data('fullurl') || $img.parent().attr('href');
    	    var $img = $('<img>', {
    	        'src' : url,
    	        'style' : 'display: none;'
    	    });
    	    $img.load(function() {
    	        // Once the image is loaded, make it visible
    	        var $i = $(this);
    	        var $container = $('#thumbnail-zoom');
    	        $i.css('display', 'block');
    	        
    	        var availHeight = $(window).height() - 20;
    	        var availWidth = $(window).width() - 20;
    	        var offset = $i.offset();
    	        
    	        // If the image's true size is bigger than the browser's height, resize it
    	        if ($i.height() > availHeight) {
    	            $i.css('height', availHeight);
    	        }
    	        
    	        // If the image's true size is bigger than the browser's width including the offset where
    	        // we will be displaying it, resize it
    	        if ($i.width() > availWidth - offset.left - 20) {
    	            $i.css('width', availWidth - offset.left - 20);
    	        }
    	        
    	        // Determine if we need to shift the container up to ensure the image will fit
    	        if (availHeight - 10 - offset.top < $i.height()) {
    	            $container.css('top', availHeight + 10 - $i.height());
    	        }
    	        
    	        // Remove the spinning logo and border
    	        $container.css('padding', '0').css('border', 'none');
    	        $container.find('i').remove();
    	    });
    	    
    	    $container.append($img);
        },
        
        hideImage : function() {
            $('div#thumbnail-zoom').remove();
        }
    }
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
	
	$('body').on('mouseover', 'img.thumbnail', function() {
	    if (!$(this).hasClass('placeholder-image')) {
	        $.doTimeout('hover', 250, BLCAdmin.imagezoom.showImage, this);
	    }
	});
	
	$('body').on('click', 'img.thumbnail', function() {
	    BLCAdmin.imagezoom.hideImage();
	    $.doTimeout('hover');
	});
	
	// When we leave the thumbnail image, remove the zoomed container from view
	$('body').on('mouseout', 'img.thumbnail', function() {
	    BLCAdmin.imagezoom.hideImage();
	    $.doTimeout('hover');
	});
	
})