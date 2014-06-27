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
import geb.navigator.NonEmptyNavigator


/**
 * Represents a top-level entity page like viewing a list of Categories at '/admin/category' or viewing a list of Prodcuts
 * at /admin/product.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class TopLevelEntity extends AdminPage {

    static at = { addButton.displayed }
        
    static content = {
        addButton(to: [EntityTypeSelection, EntityFormPage]) { $("button.add-main-entity") }
        mainGrid { module ListGridModule, $('.listgrid-container') }
    }
    
    def findModule(String containingText) {
        $('#sideMenu .blc-module .title', text: contains(containingText))
    }
    
    def expandModule(String containingText) {
        findModule(containingText).click()
    }
    
    def findSectionLink(String containingText) {
        $('#sideMenu .blc-module .content a', text: contains(containingText))
    }
    
    def findSectionLink(String containingText, NonEmptyNavigator containingModule) {
        containingModule.closest('.blc-module').find('.content a', text: contains(containingText))
    }
    
}
