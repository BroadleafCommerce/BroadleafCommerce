package org.broadleafcommerce.openadmin.web.form.component;


public class DefaultListGridActions {
    
    // Actions for the main list grid toolbar
    public static final ListGridAction ADD = new ListGridAction()
        .withAnchorClass("sub-list-grid-add")
        .withUrlPostfix("/add")
        .withIconClass("icon-plus")
        .withDisplayText("Add");

    // Actions for row-level
    public static final ListGridAction REMOVE = new ListGridAction()
        .withAnchorClass("sub-list-grid-remove")
        .withUrlPostfix("/delete")
        .withIconClass("icon-remove")
        .withDisplayText("Delete");
    
    public static final ListGridAction UPDATE = new ListGridAction()
        .withAnchorClass("sub-list-grid-update")
        .withIconClass("icon-pencil")
        .withDisplayText("Edit");
    
}
