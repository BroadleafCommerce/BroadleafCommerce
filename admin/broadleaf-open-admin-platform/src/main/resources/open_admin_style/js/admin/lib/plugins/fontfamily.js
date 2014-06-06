if (!RedactorPlugins) var RedactorPlugins = {};

RedactorPlugins.fontfamily = {
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
                    callback: function() {
                        that.setFontfamily(element[1]);
                    }
		    };
		});

		dropdown['remove'] = { title: 'Remove font', callback: function() { that.resetFontfamily(); }};

		this.buttonAdd('fontfamily', 'Advanced Font List', false, dropdown);
	},
	setFontfamily: function (value)
	{
		this.inlineSetStyle('font-family', value);
	},
	resetFontfamily: function()
	{
		this.inlineRemoveStyle('font-family');
	}
};