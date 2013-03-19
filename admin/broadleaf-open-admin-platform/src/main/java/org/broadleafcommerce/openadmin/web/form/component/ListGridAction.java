package org.broadleafcommerce.openadmin.web.form.component;



public class ListGridAction {
    
    protected String anchorClass = "";
    protected String urlPostfix = "";
    protected String iconClass = "";
    protected String displayText = "";
    
    public ListGridAction() {
    }
    
    public ListGridAction withAnchorClass(String anchorClass) {
        this.anchorClass = anchorClass;
        return this;
    }
    
    public ListGridAction withUrlPostfix(String urlPostfix) {
        this.urlPostfix = urlPostfix;
        return this;
    }
    
    public ListGridAction withIconClass(String iconClass) {
        this.iconClass = iconClass;
        return this;
    }
    
    public ListGridAction withDisplayText(String displayText) {
        this.displayText = displayText;
        return this;
    }

    public String getAnchorClass() {
        return anchorClass;
    }
    
    public void setAnchorClass(String anchorClass) {
        this.anchorClass = anchorClass;
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
