/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.form.component;


/**
 * Convenience class to deal with the common actions on most list grids. If you are using one of these static variables
 * as a base for a new action, <b>do not modify them directly</b> or your changes will persist for the remainder of the
 * application. Instead, use the clone() method from {@link ListGridAction}. For instance:
 * 
 * <pre>
 *  {@code
 *  ListGridAction newAddAction = DefaultListGridActions.ADD.clone().withDisplayText("New Add Text");
 *  }
 * </pre>
 *
 * @author Andre Azzolini
 * @see {@link ListGridAction#clone()}
 */
public class DefaultListGridActions {
    
    // Actions for the main list grid toolbar
    public static final ListGridAction ADD = new ListGridAction(ListGridAction.ADD)
        .withButtonClass("sub-list-grid-add")
        .withUrlPostfix("/add")
        .withIconClass("icon-plus")
        .withDisplayText("Add");
    
    public static final ListGridAction REORDER = new ListGridAction(ListGridAction.REORDER)
        .withButtonClass("sub-list-grid-reorder")
        .withUrlPostfix("/update")
        .withIconClass("icon-move")
        .withDisplayText("Reorder");

    // Actions for row-level
    public static final ListGridAction REMOVE = new ListGridAction(ListGridAction.REMOVE)
        .withButtonClass("sub-list-grid-remove")
        .withUrlPostfix("/delete")
        .withIconClass("icon-remove")
        .withDisplayText("Delete");
    
    public static final ListGridAction UPDATE = new ListGridAction(ListGridAction.UPDATE)
        .withButtonClass("sub-list-grid-update")
        .withIconClass("icon-pencil")
        .withDisplayText("Edit");

    public static final ListGridAction VIEW = new ListGridAction(ListGridAction.VIEW)
        .withButtonClass("sub-list-grid-view")
        .withIconClass("icon-book")
        .withDisplayText("View")
        .withUrlPostfix("/view")
        .withForListGridReadOnly(true);


    public static final ListGridAction PREVIEW = new ListGridAction("PREVIEW")
            .withButtonClass("workflow-preview")
            .withIconClass("icon-eye-open")
            .withDisplayText("Workflow_button_preview");
    
}
