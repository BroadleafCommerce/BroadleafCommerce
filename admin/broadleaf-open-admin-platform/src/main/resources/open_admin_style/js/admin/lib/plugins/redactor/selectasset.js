if (!RedactorPlugins) var RedactorPlugins = {};

(function($)
{
    RedactorPlugins.selectasset = function()
    {
        return {
            init: function()
            {
                var button = this.button.addAfter('video', 'selectasset', BLCAdmin.messages.selectUploadAsset);
                this.button.addCallback(button, BLCAdmin.asset.selectButtonClickedRedactor);
                this.button.setAwesome('selectasset', 'icon-picture');
            }
        };
    };
})(jQuery);