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
if (!RedactorPlugins) var RedactorPlugins = {};

(function($)
{
    RedactorPlugins.fontfamily = function()
    {
        return {
        	init: function ()
        	{
        	    /**
                 * Initialize necessary font mappings for Redactor
                 */
        	    var fontMap = {
        	        arial: ["Arial", "Arial, Helvetica, sans-serif"],
        	        arialblack: ["Arial Black", '"Arial Black", Gadget, sans-serif'],
        	        courier: ["Courier New", '"Courier New", Courier, monospace'],
        	        impact: ["Impact", 'Impact, Charcoal, sans-serif'],
        	        lucida: ["Lucida", '"Lucida Sans Unicode", "Lucida Grande", sans-serif'],
        	        lucidaconsole: ["Lucida Console", '"Lucida Console", Monaco, monospace'],
        	        georgia: ["Georgia", "Georgia, serif"],
        	        palatino: ["Palatino Linotype", '"Palatino Linotype", "Book Antiqua", Palatino, serif'],
        	        tahoma: ["Tahoma", "Tahoma, Geneva, sans-serif"],
        	        times: ["Times New Roman", "Times, serif"],
        	        trebuchet: ["Trebuchet", '"Trebuchet MS", Helvetica, sans-serif'],
        	        verdana: ["Verdana", "Verdana, Geneva, sans-serif"] 
        	    };
        	    
        		var that = this;
        		var dropdown = {};
        
        		$.each(fontMap, function(i, element)
        		{
        		    var fontName = element[0];
        		    var fontFace = element[1];
        			dropdown['s' + i] = {
                        title: "<font face='" + fontFace + "'>" + fontName + "</font>",
                        func: function() { that.fontfamily.set(element[1]) }
        		    };
        		});
        
        		dropdown.remove = { title: 'Remove Font Family', func: that.fontfamily.reset };
        		
        		var button = this.button.add('fontfamily', 'Change Font Family');
        		this.button.addDropdown(button, dropdown);
        	},
        	set: function (value)
        	{
        		this.inline.format('span', 'style', 'font-family:' + value + ';');
        	},
        	reset: function()
        	{
        		this.inline.removeStyleRule('font-family');
        	}
        };
    };
})(jQuery);
