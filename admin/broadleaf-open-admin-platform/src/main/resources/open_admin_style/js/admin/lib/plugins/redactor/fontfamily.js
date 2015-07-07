/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
        		this.inline.format('span', 'syle', 'font-family:' + value + ';');
        	},
        	reset: function()
        	{
        		this.inline.removeStyleRule('font-family');
        	}
        };
    };
})(jQuery);