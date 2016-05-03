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
package org.broadleafcommerce.openadmin.dto.override;

/**
 * @author Chris Kittrell
 */
public class GroupMetadataOverride extends MetadataOverride {

    protected Integer column;
    protected Boolean untitled;
    protected String tooltip;
    protected Boolean collapsed;

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Boolean getUntitled() {
        return untitled;
    }

    public void setUntitled(Boolean untitled) {
        this.untitled = untitled;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public Boolean getCollapsed() {
        return collapsed;
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }
    
}
