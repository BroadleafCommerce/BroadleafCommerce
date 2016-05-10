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
import java.util.Iterator;
import java.util.Map;

/**
 * @author Chris Kittrell
 */
public class TabMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String owningClass;

    protected Map<String, GroupMetadata> groupMetadata;
    protected String tabName;
    protected Integer tabOrder;

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public Integer getTabOrder() {
        return tabOrder;
    }

    public void setTabOrder(Integer tabOrder) {
        this.tabOrder = tabOrder;
    }

    public String getOwningClass() {
        return owningClass;
    }

    public void setOwningClass(String owningClass) {
        this.owningClass = owningClass;
    }

    public Map<String, GroupMetadata> getGroupMetadata() {
        return groupMetadata;
    }

    public void setGroupMetadata(Map<String, GroupMetadata> groupMetadata) {
        this.groupMetadata = groupMetadata;
    }

    public GroupMetadata getFirstGroup() {
        Iterator<GroupMetadata> groupMetadataIterator = groupMetadata.values().iterator();
        GroupMetadata result = groupMetadataIterator.hasNext() ? groupMetadataIterator.next() : null;

        while(groupMetadataIterator.hasNext()) {
            GroupMetadata next = groupMetadataIterator.next();
            if (result.getGroupOrder() == null) {
                result = next;
            } else if (next.getGroupOrder() != null && next.getGroupOrder() < result.getGroupOrder()) {
                result = next;
            }
        }

        return result;
    }

    public TabMetadata cloneFieldMetadata() {
        TabMetadata metadata = new TabMetadata();

        metadata.owningClass = owningClass;
        metadata.setGroupMetadata(groupMetadata);
        metadata.tabName = tabName;
        metadata.tabOrder = tabOrder;

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

        TabMetadata metadata = (TabMetadata) o;

        if (owningClass != null ? !owningClass.equals(metadata.owningClass) : metadata.owningClass != null) {
            return false;
        }
        if (tabName != null ? !tabName.equals(metadata.tabName) : metadata.tabName != null) {
            return false;
        }
        if (tabOrder != null ? !tabOrder.equals(metadata.tabOrder) : metadata.tabOrder != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (owningClass != null ? owningClass.hashCode() : 0);
        result = 31 * result + (tabName != null ? tabName.hashCode() : 0);
        result = 31 * result + (tabOrder != null ? tabOrder.hashCode() : 0);
        return result;
    }
}
