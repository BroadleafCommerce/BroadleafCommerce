/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.browsertest.page

import geb.Page
import geb.navigator.Navigator


/**
 * Represents an adorned target modal when you hit 'add' or 'edit' on an adorned target item
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class AdornedTargetPage extends Page {

    @Delegate
    FieldConverterSupport fieldConverter = new FieldConverterSupport()
    
    static at = { waitFor { grid.displayed } }
    
    static content = {
        modal { $('.modal') }
        
        // Always visibile even when just 1 step
        grid { module ListGridModule, modal.find('.listgrid-container') }
        
        // Not always visible because the adorned target could not have additional fields
        form(required: false) { modal.find('form.entity-form') }
        submitButton(required: false) { modal.find('button.submit-button') }
        
        step1Tab(required: false) { modal.find('a[href="#adornedModalTab1"]') }
        step2Tab(required: false) { modal.find('a[href="#adornedModalTab2"]') }
        step1Content(required: false) { modal.find('.tabs-content .adornedModalTab1Tab') }
        step2Content(required: false) { modal.find('.tabs-content .adornedModalTab2Tab') }
    }
    
    def Navigator getField(String fieldName) {
        form.find(convertFieldName(fieldName))
    }
    
}
