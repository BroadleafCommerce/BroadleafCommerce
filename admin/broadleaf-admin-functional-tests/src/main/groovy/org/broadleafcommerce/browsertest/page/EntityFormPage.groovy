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
        deleteButton(to: TopLevelEntity) { container.find('button.delete-button') }
        closeButton(to: EntityFormPage) { container.find('button.close') }
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
            def element = field.getElement(0)
            js.exec (element, 'arguments[0].scrollIntoView();')
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
