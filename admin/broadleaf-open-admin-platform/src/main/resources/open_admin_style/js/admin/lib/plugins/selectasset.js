if (typeof RedactorPlugins === 'undefined') var RedactorPlugins = {};

RedactorPlugins.selectasset = {
    init: function()
    {
        this.buttonAddBefore('video', 'selectasset', BLCAdmin.messages.selectUploadAsset, BLCAdmin.asset.selectButtonClickedRedactor);
        this.buttonAwesome('selectasset', 'icon-picture');
    }
};