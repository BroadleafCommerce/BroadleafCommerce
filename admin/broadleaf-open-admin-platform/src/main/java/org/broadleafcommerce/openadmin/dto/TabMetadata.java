/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.openadmin.dto;

import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;

import java.io.Serializable;
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
