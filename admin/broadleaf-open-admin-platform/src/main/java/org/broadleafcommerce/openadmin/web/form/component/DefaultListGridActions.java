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
        .withIconClass("fa fa-plus")
        .withDisplayText("Add");

    public static final ListGridAction ADD_EMPTY = new ListGridAction(ListGridAction.ADD)
        .withButtonClass("sub-list-grid-add-empty")
        .withUrlPostfix("/add")
        .withIconClass("fa fa-plus")
        .withDisplayText("Add");

    public static final ListGridAction TREE_ADD = new ListGridAction(ListGridAction.TREE_ADD)
        .withButtonClass("tree-list-grid-row-add")
        .withUrlPostfix("/new/enterprise-tree-add")
        .withIconClass("fa fa-plus")
        .withDisplayText("Add Child");

    public static final ListGridAction MANUAL_FETCH = new ListGridAction(ListGridAction.MANUAL_FETCH)
        .withForListGridReadOnly(true)
        .withButtonClass("sub-list-grid-fetch")
        .withDisplayText("Fetch");

    public static final ListGridAction REFRESH = new ListGridAction(ListGridAction.REFRESH)
        .withButtonClass("collection-refresh")
        .withIconClass("fa fa-refresh")
        .withDisplayText("Refresh Collection");

    // Actions for row-level
    public static final ListGridAction REMOVE = new ListGridAction(ListGridAction.REMOVE)
        .withButtonClass("sub-list-grid-remove")
        .withUrlPostfix("/delete")
        .withIconClass("blc-icon-close")
        .withDisplayText("Delete");
    
    public static final ListGridAction UPDATE = new ListGridAction(ListGridAction.UPDATE)
        .withButtonClass("sub-list-grid-update")
        .withIconClass("fa fa-pencil")
        .withDisplayText("Edit");

    public static final ListGridAction VIEW = new ListGridAction(ListGridAction.VIEW)
        .withButtonClass("sub-list-grid-view")
        .withIconClass("blc-icon-view")
        .withDisplayText("View")
        .withUrlPostfix("/view")
        .withForListGridReadOnly(true);

    public static final ListGridAction EDIT = new ListGridAction(ListGridAction.EDIT)
        .withButtonClass("sub-list-grid-edit")
        .withIconClass("fa fa-pencil")
        .withDisplayText("Edit");

    public static final ListGridAction SINGLE_SELECT = new ListGridAction(ListGridAction.SINGLE_SELECT)
        .withButtonClass("list-grid-single-select")
        .withDisplayText("Select")
        .withSingleActionOnly(true);

    public static final ListGridAction TREE_LOOKUP_SELECT = new ListGridAction(ListGridAction.SINGLE_SELECT)
        .withButtonClass("tree-list-grid-lookup-select")
        .withDisplayText("Select")
        .withSingleActionOnly(true);

    public static final ListGridAction TREE_JUMP_TO_CONTEXT = new ListGridAction(ListGridAction.TREE_JUMP_TO_CONTEXT)
        .withButtonClass("tree-list-grid-jump-to-context")
        .withUrlPostfix("/tree/jump-to-context")
        .withIconClass("fa fa-columns")
        .withDisplayText("Jump to Context");

    public static final ListGridAction ASSET_GRID_SINGLE_SELECT = new ListGridAction(ListGridAction.SINGLE_SELECT)
        .withButtonClass("asset-grid-single-select")
        .withDisplayText("Select")
        .withSingleActionOnly(true);

    public static final ListGridAction ASSET_ADD = new ListGridAction(ListGridAction.ASSET_ADD)
        .withButtonClass("upload-asset additional-action")
        .withUrlPostfix("/assets")
        .withDisplayText("Upload_Asset");

    public static final ListGridAction MULTI_SELECT = new ListGridAction(ListGridAction.MULTI_SELECT)
        .withButtonClass("list-grid-multi-select")
        .withDisplayText("Select");

    public static final ListGridAction PREVIEW = new ListGridAction("PREVIEW")
        .withButtonClass("workflow-preview")
        .withIconClass("blc-icon-view")
        .withDisplayText("Workflow_button_preview");

}
