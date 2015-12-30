/*
 * #%L
 * BroadleafCommerce Custom Field
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
(function($, BLCAdmin) {
    if (location.pathname.match("^/admin/product-options/")) {

        var className = 'org.broadleafcommerce.core.catalog.domain.ProductOption';

        BLCAdmin.addDependentFieldHandler(
            className,
            '#field-productOptionValidationStrategyType',
            '#field-validationString, #field-errorCode ,#field-errorMessage',
            function(value, $container) {
                return value !== "NONE";
            }
        );
    }
})(jQuery, BLCAdmin);