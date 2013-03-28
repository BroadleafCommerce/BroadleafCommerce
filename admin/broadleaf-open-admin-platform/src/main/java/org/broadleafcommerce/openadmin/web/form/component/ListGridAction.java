package org.broadleafcommerce.openadmin.web.form.component;


/**
 * Buttons to display on the frontend for a list grid. These will be displayed at the top of the {@link ListGrid} that they
 * are related to.
 * 
 * @author Andre Azzolini (aazzolini)
 * @see {@link ListGrid#addRowAction(ListGridAction)}
 * @see {@link ListGrid#addToolbarAction(ListGridAction)}
 */
public class ListGridAction {
    
    protected String anchorClass = "";
    protected String urlPostfix = "";
    protected String iconClass = "";
    protected String displayText = "";
    
    public ListGridAction() {
    }
    
    public ListGridAction withAnchorClass(String anchorClass) {
        setAnchorClass(anchorClass);
        return this;
    }
    
    public ListGridAction withUrlPostfix(String urlPostfix) {
        setUrlPostfix(urlPostfix);
        return this;
    }
    
    public ListGridAction withIconClass(String iconClass) {
        setIconClass(iconClass);
        return this;
    }
    
    public ListGridAction withDisplayText(String displayText) {
        setDisplayText(displayText);
        return this;
    }

    public String getAnchorClass() {
        return anchorClass;
    }
    
    /**
     * Main intent is for the anchor class to be used in a JQuery selector for giving this button a click action. You could
     * technically also apply additional styling to this anchor but is not usually recommended.
     * 
     * @param anchorClass
     */
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
    
    /**
     * Icon classes are displayed next to the {@link #getDisplayText()}. These can technically be whatever you like and
     * you can use css selectors to style them accordingly. That said, Broadleaf uses the FontAwesome set of icons out of
     * the box, and it is intended that <b>iconClass</b> is an icon from the FontAwesome framework. To see the icons that
     * are included, check out http://fortawesome.github.com/Font-Awesome/#icons-new
     * 
     * @param iconClass
     */
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
