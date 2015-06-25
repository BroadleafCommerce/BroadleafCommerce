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

/**
 * @author Chris Kittrell
 */
public class GroupMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String ceilingEntity;

    protected String groupName;
    protected Integer groupOrder;
    protected Integer column;
    protected Boolean borderless;
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

    public Boolean getBorderless() {
        return borderless == null ? false : borderless;
    }

    public void setBorderless(Boolean borderless) {
        this.borderless = borderless;
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

    public String getCeilingEntity() {
        return ceilingEntity;
    }

    public void setCeilingEntity(String ceilingEntity) {
        this.ceilingEntity = ceilingEntity;
    }

    public GroupMetadata cloneFieldMetadata() {
        GroupMetadata metadata = new GroupMetadata();

        metadata.ceilingEntity = ceilingEntity;

        metadata.groupName = groupName;
        metadata.groupOrder = groupOrder;
        metadata.column = column;
        metadata.borderless = borderless;
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

        if (ceilingEntity != null ? !ceilingEntity.equals(metadata.ceilingEntity) : metadata.ceilingEntity != null) {
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
        if (borderless != null ? !borderless.equals(metadata.borderless) : metadata.borderless != null) {
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
        result = 31 * result + (ceilingEntity != null ? ceilingEntity.hashCode() : 0);
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        result = 31 * result + (groupOrder != null ? groupOrder.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (borderless != null ? borderless.hashCode() : 0);
        result = 31 * result + (tooltip != null ? tooltip.hashCode() : 0);
        result = 31 * result + (collapsed != null ? collapsed.hashCode() : 0);
        return result;
    }
}
