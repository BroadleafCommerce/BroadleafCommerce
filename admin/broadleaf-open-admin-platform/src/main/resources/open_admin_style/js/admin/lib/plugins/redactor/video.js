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
(function($)
{
	$.Redactor.prototype.video = function()
	{
		return {
			reUrlYoutube: /https?:\/\/(?:[0-9A-Z-]+\.)?(?:youtu\.be\/|youtube\.com\S*[^\w\-\s])([\w\-]{11})(?=[^\w\-]|$)(?![?=&+%\w.-]*(?:['"][^<>]*>|<\/a>))[?=&+%\w.-]*/ig,
			reUrlVimeo: /https?:\/\/(www\.)?vimeo.com\/(\d+)($|\/)/,
			getTemplate: function()
			{
				return String()
				+ '<section id="redactor-modal-video-insert">'
					+ '<label>' + this.lang.get('video_html_code') + '</label>'
					+ '<textarea id="redactor-insert-video-area" style="height: 160px;"></textarea>'
				+ '</section>';
			},
			init: function()
			{
				var button = this.button.addAfter('image', 'video', this.lang.get('video'));
				this.button.addCallback(button, this.video.show);
			},
			show: function()
			{
				this.modal.addTemplate('video', this.video.getTemplate());

				this.modal.load('video', this.lang.get('video'), 700);
				this.modal.createCancelButton();

				var button = this.modal.createActionButton(this.lang.get('insert'));
				button.on('click', this.video.insert);

				this.selection.save();
				this.modal.show();

				$('#redactor-insert-video-area').focus();

			},
			insert: function()
			{
				var data = $('#redactor-insert-video-area').val();

				if (!data.match(/<iframe|<video/gi))
				{
					data = this.clean.stripTags(data);

					// parse if it is link on youtube & vimeo
					var iframeStart = '<iframe style="width: 500px; height: 281px;" src="',
						iframeEnd = '" frameborder="0" allowfullscreen></iframe>';

					if (data.match(this.video.reUrlYoutube))
					{
						data = data.replace(this.video.reUrlYoutube, iframeStart + '//www.youtube.com/embed/$1' + iframeEnd);
					}
					else if (data.match(this.video.reUrlVimeo))
					{
						data = data.replace(this.video.reUrlVimeo, iframeStart + '//player.vimeo.com/video/$2' + iframeEnd);
					}
				}

				this.selection.restore();
				this.modal.close();

				var current = this.selection.getBlock() || this.selection.getCurrent();

				if (current) $(current).after(data);
				else
				{
					this.insert.html(data);
				}

				this.code.sync();
			}

		};
	};
})(jQuery);
