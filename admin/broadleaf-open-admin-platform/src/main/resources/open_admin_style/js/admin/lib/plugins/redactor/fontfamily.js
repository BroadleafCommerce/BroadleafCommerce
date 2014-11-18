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