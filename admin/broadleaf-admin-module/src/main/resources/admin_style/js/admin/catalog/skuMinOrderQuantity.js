(function($, BLCAdmin) {

    var skuClassName = 'org.broadleafcommerce.core.catalog.domain.Sku';
    BLCAdmin.addDependentFieldHandler(skuClassName, '#field-hasMinOrderQuantity', '#field-minOrderQuantity', 'true');

    var productClassName = 'org.broadleafcommerce.core.catalog.domain.Product';
    BLCAdmin.addDependentFieldHandler(productClassName, '#field-defaultSku--hasMinOrderQuantity', '#field-defaultSku--minOrderQuantity', 'true');

})(jQuery, BLCAdmin);
