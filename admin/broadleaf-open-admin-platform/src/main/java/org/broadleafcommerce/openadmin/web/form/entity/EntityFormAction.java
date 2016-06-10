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
package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;



public class EntityFormAction implements Cloneable {
    
    public static final String ADD = "ADD";
    public static final String SAVE = "SAVE";
    public static final String DELETE = "DELETE";
    public static final String PREVIEW = "PREVIEW";

    protected String buttonType = "button";
    protected String buttonClass = "";
    protected String urlPostfix = "";
    protected String iconClass = "";
    protected String displayText = "";
    protected String id = "";
    protected String urlOverride = null;
    protected Boolean isConfirmEnabled = Boolean.FALSE;
    protected String confirmEnabledText = "";
    
    public EntityFormAction(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            EntityFormAction that = (EntityFormAction) obj;
            return new EqualsBuilder()
                .append(buttonClass, that.buttonClass)
                .append(urlPostfix, that.urlPostfix)
                .append(iconClass, that.iconClass)
                .append(displayText, that.displayText)
                .append(isConfirmEnabled, that.isConfirmEnabled)
                .append(confirmEnabledText, that.confirmEnabledText)
                .build();
        }
        return false;
    }
    
    @Override
    public EntityFormAction clone() {
        EntityFormAction cloned = new EntityFormAction(id);
        cloned.buttonType = buttonType;
        cloned.buttonClass = buttonClass;
        cloned.urlPostfix = urlPostfix;
        cloned.iconClass = iconClass;
        cloned.displayText = displayText;
        cloned.isConfirmEnabled = isConfirmEnabled;
        cloned.confirmEnabledText = confirmEnabledText;
        return cloned;
    }
    
    public EntityFormAction withButtonType(String buttonType) {
        setButtonType(buttonType);
        return this;
    }
    
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
    
    public EntityFormAction withUrlOverride(String urlOverride) {
        setUrlOverride(urlOverride);
        return this;
    }

    public EntityFormAction withIsConfirmEnabled(Boolean confirmEnabled) {
        setIsConfirmEnabled(confirmEnabled);
        return this;
    }

    public EntityFormAction withConfirmEnabledText(String confirmEnabledText) {
        setConfirmEnabledText(confirmEnabledText);
        return this;
    }
    
    public String getId() {
        return id;
    }

    public String getButtonType() {
        return buttonType;
    }
    
    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
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

    public Boolean getIsConfirmEnabled() {
        return isConfirmEnabled == null ? Boolean.FALSE : isConfirmEnabled;
    }

    public void setIsConfirmEnabled(Boolean confirmEnabled) {
        isConfirmEnabled = confirmEnabled;
    }

    public String getConfirmEnabledText() {
        return confirmEnabledText;
    }

    public void setConfirmEnabledText(String confirmEnabledText) {
        this.confirmEnabledText = confirmEnabledText;
    }

    /**
     * Gets the manual override for the data-actionurl attribute on an action.
     * 
     * @return
     */
    public String getUrlOverride() {
        return urlOverride;
    }
    
    /**
     * This is a manual override for the data-actionurl attribute for an listgrid action. The data-actionurl attribute on a
     * button is normally automatically computed by appending the postfix URL to the path of the list grid
     * 
     * @param actionUrlOverride
     */
    public void setUrlOverride(String urlOverride) {
        this.urlOverride = urlOverride;
    }

}
