/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
