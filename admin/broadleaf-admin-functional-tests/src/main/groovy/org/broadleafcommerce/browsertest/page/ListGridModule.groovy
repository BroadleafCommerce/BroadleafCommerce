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

import geb.Browser
import geb.Module


/**
 * A generic module representing a grid on the page. This is mainly used as the representation of the main list grid (like
 * on a top-level entity page). A {@link ActionableListGridModule} is a type of {@link ListGridModule} but with actions.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link ActionableListGridModule}
 */
class ListGridModule extends Module {
    
    static content = {
        id { $().@id }
        toolbar { $('.listgrid-toolbar') }
        
        header { $('.listgrid-header-wrapper') }
        // todo: consider refactoring to use tail() for the rows
        rows { index ->
            moduleList Row, $('.listgrid-body-wrapper table tr:not(.width-control-header)'), index
        }
    }
    
    def boolean isEmpty() {
        return rows.size() == 0 || rows[0].cells[0].hasClass('list-grid-no-results')
    }
    
    def scrollToIndex(int number) {
        if (id == null || id.isEmpty()) {
            js.exec 'BLCAdmin.listGrid.paginate.scrollToIndex($(\'.listgrid-body-wrapper tbody\'), ' + number + ');'
        } else {
            js.exec 'BLCAdmin.listGrid.paginate.scrollToIndex($(\'.' + id + ' .listgrid-body-wrapper tbody\'), ' + number + ');'
        }
    }
    
    def scrollToEntry(String entry) {
        if (this.rows.size() > 25) {
            def number = 25
            while (this.rows.find({it.text().contains(entry)}) == null) {
                scrollToIndex(number)
                number += 25
            }
        }
        scrollToIndex(this.rows.findIndexOf({it.text().contains(entry)}) - 1)
    }
}

/**
 * The main use case of this is for list grids inside of an {@link EntityForm} but there could be other situations that
 * warrant its use in other areas. A {@link ListGridModule} that includes a toolbar.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class ActionableListGridModule extends ListGridModule {
    
    static content = {
        actionButtons { toolbar.find('button') }
        addButton(required: false) { actionButtons.filter('.sub-list-grid-add') }
        editButton(required: false) { actionButtons.filter('.sub-list-grid-update') }
        reorderButton(required: false) { actionButtons.filter('.sub-list-grid-reorder') }
        removeButton(required: false) { actionButtons.filter('.sub-list-grid-remove') }
    }
    
    def reorderDragAndDrop(int row1, int row2) {
        def element1 = this.rows[row1]
        def element2 = this.rows[row2]
        Browser.drive {
            interact {
                clickAndHold(element1)
                moveToElement(element2)
                moveByOffset(0, 1)
                release()
            }
        }
    }
    
}

/**
 * An individual row within a {@link ListGridModule}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class Row extends Module {
    
    static content = {
        cells { $('td') }
        detailLink(required: false, to: EntityFormPage) { cells.first().find('a') }
    }
}
