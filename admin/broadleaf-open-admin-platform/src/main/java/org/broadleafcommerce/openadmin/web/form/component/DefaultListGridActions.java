package org.broadleafcommerce.openadmin.web.form.component;


public class DefaultListGridActions {
    
    // Actions for the main list grid toolbar
    public static final ListGridAction ADD = new ListGridAction()
        .withAnchorClass("sub-list-grid-add")
        .withUrlPostfix("/add")
        .withIconClass("foundicon-add-doc")
        .withDisplayText("Add");

    // Actions for row-level
    public static final ListGridAction REMOVE = new ListGridAction()
        .withAnchorClass("sub-list-grid-remove")
        .withUrlPostfix("/delete")
        .withIconClass("foundicon-trash");
    
    public static final ListGridAction UPDATE = new ListGridAction()
        .withAnchorClass("sub-list-grid-update")
        .withIconClass("foundicon-edit");
    
}
