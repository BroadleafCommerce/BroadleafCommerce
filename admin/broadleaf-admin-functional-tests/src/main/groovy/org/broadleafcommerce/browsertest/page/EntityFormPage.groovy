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

import geb.navigator.Navigator

/**
 * A special type of EntityForm that wraps everything inside of a .modal selector
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class EntityFormModal extends EntityFormPage {
    
    def String getContainerSelector() {
        '.modal'
    }
}

/**
 * <p>
 * Represents an entity form on a details/edit page
 * 
 * <p>
 * If you would like to represent this form in a modal (like when you hit the 'Add' button) then you should bind to
 * {@link EntityFormModal}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class EntityFormPage extends AdminPage {

    @Delegate
    FieldConverterSupport fieldConverter = new FieldConverterSupport()
    
    static at = { waitFor { form.displayed } }
    
    static content = {
        container { getContainerSelector() ? $(getContainerSelector()) : $() }
        tabs { container.find('.tabs-container dd') }
        form { container.find('form.entity-form') }
        submitButton(to: EntityFormPage) { container.find('button.submit-button') }
        grids(required: false) { index ->
            moduleList ActionableListGridModule, container.find('.listgrid-container'), index
        }
    }
    
    /**
     * Subclasses can override this method to wrap the parent element inside of another element
     * @see {@link EntityFormModal}
     */
    def String getContainerSelector() {
        ''
    }
    
    /**
     * Gets a collection field by the given name and optionally clicks the associated tab so that it is visible
     * and can be interacted on (defaults to 'true')
     * @param fieldName
     * @return
     */
    def ActionableListGridModule getGrid(String fieldName, makeVisible = true) {
        def ActionableListGridModule grid = grids.find {
            it.id == fieldName
        }
        
        if (!grid.displayed && makeVisible) {
            clickTab(grid)
        }
        
        return grid
    }
    
    /**
     * Returns the a {@link Navigator} representing the field name passed in. You can also pass in dot-separated strings
     * like 'defaultSku.name'
     * @param fieldName
     */
    def Navigator getField(String fieldName, makeVisible = true) {
        def field = form.find(convertFieldName(fieldName))
        if (!field.displayed && makeVisible) {
            clickTab(field)
        }
        return field
    }
    
    /**
     * Clicks the tab from the contained item
     * @param containedItem
     * @return
     */
    def clickTab(Navigator containedItem) {
        def tabName = containedItem.closest('li').classes().find {
            it.startsWith('tab')
        }
        tabName = tabName.replace('Tab', '')
        tabs.find("a[href=\"#$tabName\"]").click()
    }
    
    /**
     * Sets a hidden form field with the given name to the specified value. This has to be done with Javascript since
     * Selenium cannot see and manipulate elements that are hidden on screen. Mainly used with to one lookup fields to
     * set the ID without actually performing a lookup
     * @param fieldName
     * @param value the value that the hidden field should be set to
     */
    def setHiddenFieldValue(String fieldName, String value) {
        js.exec "jQuery('" + convertFieldName(fieldName) + "').val('" + value + "')"
    }
    
}
