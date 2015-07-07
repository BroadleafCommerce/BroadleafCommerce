Selectize.define('silent_remove', function(options){
    var self = this;

    // defang the internal search method when remove has been clicked
    this.on('item_remove', function(){
        this.plugin_silent_remove_in_remove = true;
    });

    this.search = (function() {
        var original = self.search;
        return function() {
            if (typeof(this.plugin_silent_remove_in_remove) != "undefined") {
                // re-enable normal searching
                delete this.plugin_silent_remove_in_remove;
                return {
                    items: {},
                    query: [],
                    tokens: []
                };
            }
            else {
                return original.apply(this, arguments);
            }
        };
    })();
});