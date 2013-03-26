package org.broadleafcommerce.openadmin.web.form.entity;



public class EntityFormAction {
    
    protected String buttonClass = "";
    protected String urlPostfix = "";
    protected String iconClass = "";
    protected String displayText = "";
    
    public EntityFormAction withButtonClass(String buttonClass) {
        setButtonClass(buttonClass);
        return this;
    }
    
    public EntityFormAction withUrlPostfix(String urlPostfix) {
        setUrlPostfix(urlPostfix);
        return this;
    }
    
    public EntityFormAction withIconClass(String iconClass) {
        setIconClass(iconClass);
        return this;
    }
    
    public EntityFormAction withDisplayText(String displayText) {
        setDisplayText(displayText);
        return this;
    }

    public String getButtonClass() {
        return buttonClass;
    }
    
    public void setButtonClass(String buttonClass) {
        this.buttonClass = buttonClass;
    }

    public String getUrlPostfix() {
        return urlPostfix;
    }
    
    public void setUrlPostfix(String urlPostfix) {
        this.urlPostfix = urlPostfix;
    }
    
    public String getIconClass() {
        return iconClass;
    }
    
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
    
    public String getDisplayText() {
        return displayText;
    }
    
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

}
