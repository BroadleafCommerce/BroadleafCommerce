package org.broadleafcommerce.openadmin.web.form.entity;



public class DefaultEntityFormActions {
    
    public static final EntityFormAction SAVE = new EntityFormAction()
        .withButtonClass("submit-button")
        .withDisplayText("Save");

    public static final EntityFormAction DELETE = new EntityFormAction()
        .withButtonClass("delete-button alert")
        .withDisplayText("Delete");
    
}
