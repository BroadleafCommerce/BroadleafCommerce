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
        reorderButton(required: false) { actionButtons.filter('.sub-list-grid-remove') }
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
