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
        .withIconClass("fa fa-plus-square-o fa-lg")
        .withDisplayText("Add");
    
    public static final ListGridAction REORDER = new ListGridAction(ListGridAction.REORDER)
        .withButtonClass("sub-list-grid-reorder")
        .withUrlPostfix("/update")
        .withIconClass("fa fa-arrows fa-lg")
        .withDisplayText("Reorder");

    // Actions for row-level
    public static final ListGridAction REMOVE = new ListGridAction(ListGridAction.REMOVE)
        .withButtonClass("sub-list-grid-remove")
        .withUrlPostfix("/delete")
        .withIconClass("fa fa-times fa-lg")
        .withDisplayText("Delete");
    
    public static final ListGridAction UPDATE = new ListGridAction(ListGridAction.UPDATE)
        .withButtonClass("sub-list-grid-update")
        .withIconClass("fa fa-edit fa-lg")
        .withDisplayText("Edit");

    public static final ListGridAction VIEW = new ListGridAction(ListGridAction.VIEW)
        .withButtonClass("sub-list-grid-view")
        .withIconClass("fa fa-eye fa-lg")
        .withDisplayText("View")
        .withUrlPostfix("/view")
        .withForListGridReadOnly(true);

    public static final ListGridAction EDIT = new ListGridAction(ListGridAction.EDIT)
        .withButtonClass("sub-list-grid-edit")
        .withIconClass("fa fa-edit fa-lg")
        .withDisplayText("Edit");


    public static final ListGridAction SINGLE_SELECT = new ListGridAction(ListGridAction.SINGLE_SELECT)
        .withButtonClass("list-grid-single-select")
        .withDisplayText("Select")
        .withSingleActionOnly(true);

    public static final ListGridAction MULTI_SELECT = new ListGridAction(ListGridAction.MULTI_SELECT)
        .withButtonClass("list-grid-multi-select")
        .withDisplayText("Select");


    public static final ListGridAction PREVIEW = new ListGridAction("PREVIEW")
        .withButtonClass("workflow-preview")
        .withIconClass("fa fa-eye fa-fw")
            .withDisplayText("Workflow_button_preview");
    
}
