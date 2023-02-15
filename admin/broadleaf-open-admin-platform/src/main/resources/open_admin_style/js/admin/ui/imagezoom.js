/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
    	    $container.append($('<i>', { 'class' : 'fa-pulse fa fa-spinner' }));
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
	
	$('body').on('mouseover', 'div.view-asset-selector', function() {
		var $img = $(this).parent().find('img:visible');
	    if (!$img.hasClass('placeholder-image')) {
	        $.doTimeout('hover', 250, BLCAdmin.imagezoom.showImage, $img);
	    }
	});
	
	$('body').on('click', 'img.thumbnail', function() {
	    BLCAdmin.imagezoom.hideImage();
	    $.doTimeout('hover');
	});
	
	// When we leave the thumbnail image, remove the zoomed container from view
	$('body').on('mouseout', 'div.view-asset-selector', function() {
	    BLCAdmin.imagezoom.hideImage();
	    $.doTimeout('hover');
	});

});
