/*
 * #%L
 * BroadleafCommerce Custom Field
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
(function($, BLCAdmin) {
    if (location.pathname.match("^" + BLC.servletContext + "/product-options/")) {

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