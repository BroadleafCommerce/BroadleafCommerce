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
package org.broadleafcommerce.openadmin.dto;

import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;

import java.io.Serializable;

/**
 * @author Chris Kittrell
 */
public class GroupMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String owningClass;

    protected String groupName;
    protected Integer groupOrder;
    protected Integer column;
    protected Boolean untitled;
    protected String tooltip;
    protected Boolean collapsed;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getGroupOrder() {
        return groupOrder;
    }

    public void setGroupOrder(Integer groupOrder) {
        this.groupOrder = groupOrder;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Boolean getUntitled() {
        return untitled == null ? false : untitled;
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
        return collapsed == null ? false : collapsed;
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }

    public String getOwningClass() {
        return owningClass;
    }

    public void setOwningClass(String owningClass) {
        this.owningClass = owningClass;
    }

    public GroupMetadata cloneFieldMetadata() {
        GroupMetadata metadata = new GroupMetadata();

        metadata.owningClass = owningClass;

        metadata.groupName = groupName;
        metadata.groupOrder = groupOrder;
        metadata.column = column;
        metadata.untitled = untitled;
        metadata.tooltip = tooltip;
        metadata.collapsed = collapsed;

        return metadata;
    }

    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        GroupMetadata metadata = (GroupMetadata) o;

        if (owningClass != null ? !owningClass.equals(metadata.owningClass) : metadata.owningClass != null) {
            return false;
        }
        if (groupName != null ? !groupName.equals(metadata.groupName) : metadata.groupName != null) {
            return false;
        }
        if (groupOrder != null ? !groupOrder.equals(metadata.groupOrder) : metadata.groupOrder != null) {
            return false;
        }
        if (column != null ? !column.equals(metadata.column) : metadata.column != null) {
            return false;
        }
        if (untitled != null ? !untitled.equals(metadata.untitled) : metadata.untitled != null) {
            return false;
        }
        if (tooltip != null ? !tooltip.equals(metadata.tooltip) : metadata.tooltip != null) {
            return false;
        }
        if (collapsed != null ? !collapsed.equals(metadata.collapsed) : metadata.collapsed != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (owningClass != null ? owningClass.hashCode() : 0);
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        result = 31 * result + (groupOrder != null ? groupOrder.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (untitled != null ? untitled.hashCode() : 0);
        result = 31 * result + (tooltip != null ? tooltip.hashCode() : 0);
        result = 31 * result + (collapsed != null ? collapsed.hashCode() : 0);
        return result;
    }
}
