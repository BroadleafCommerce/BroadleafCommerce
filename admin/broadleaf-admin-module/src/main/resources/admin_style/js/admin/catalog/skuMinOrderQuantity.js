(function($, BLCAdmin) {

    var className = 'org.broadleafcommerce.core.catalog.domain.Sku';
    BLCAdmin.addDependentFieldHandler(className, '#field-hasMinOrderQuantity', '#field-minOrderQuantity', 'true');

})(jQuery, BLCAdmin);
