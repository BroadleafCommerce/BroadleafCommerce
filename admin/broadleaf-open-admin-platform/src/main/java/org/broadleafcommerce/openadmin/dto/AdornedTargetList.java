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

import org.broadleafcommerce.openadmin.dto.visitor.PersistencePerspectiveItemVisitor;

/**
 * 
 * @author jfischer
 *
 */
public class AdornedTargetList implements PersistencePerspectiveItem {

    private static final long serialVersionUID = 1L;

    private String collectionFieldName;
    private String linkedObjectPath;
    private String targetObjectPath;
    private String adornedTargetEntityClassname;
    private String adornedTargetEntityPolymorphicType;
    private String sortField;
    private Boolean sortAscending;
    private String linkedIdProperty;
    private String targetIdProperty;
    private Boolean inverse = Boolean.FALSE;
    private String joinEntityClass;
    private Boolean mutable = true;
    
    public AdornedTargetList() {
        //do nothing
    }
    
    public AdornedTargetList(String collectionFieldName, String linkedObjectPath, String linkedIdProperty, String targetObjectPath, String targetIdProperty, String adornedTargetEntityClassname) {
        this(collectionFieldName, linkedObjectPath, linkedIdProperty, targetObjectPath, targetIdProperty, adornedTargetEntityClassname, null, null);
    }
    
    public AdornedTargetList(String collectionFieldName, String linkedObjectPath, String linkedIdProperty, String targetObjectPath, String targetIdProperty, String adornedTargetEntityClassname, String adornedTargetEntityPolymorphicType) {
        this(collectionFieldName, linkedObjectPath, linkedIdProperty, targetObjectPath, targetIdProperty, adornedTargetEntityClassname, adornedTargetEntityPolymorphicType, null, null);
    }

    public AdornedTargetList(String collectionFieldName, String linkedObjectPath, String linkedIdProperty, String targetObjectPath, String targetIdProperty, String adornedTargetEntityClassname, String sortField, Boolean sortAscending) {
        this(collectionFieldName, linkedObjectPath, linkedIdProperty, targetObjectPath, targetIdProperty, adornedTargetEntityClassname, null, sortField, sortAscending);
    }
    
    public AdornedTargetList(String collectionFieldName, String linkedObjectPath, String linkedIdProperty, String targetObjectPath, String targetIdProperty, String adornedTargetEntityClassname, String adornedTargetEntityPolymorphicType, String sortField, Boolean sortAscending) {
        this.collectionFieldName = collectionFieldName;
        this.linkedObjectPath = linkedObjectPath;
        this.targetObjectPath = targetObjectPath;
        this.adornedTargetEntityClassname = adornedTargetEntityClassname;
        this.adornedTargetEntityPolymorphicType = adornedTargetEntityPolymorphicType;
        this.sortField = sortField;
        this.sortAscending = sortAscending;
        this.linkedIdProperty = linkedIdProperty;
        this.targetIdProperty = targetIdProperty;
    }
    
    public String getCollectionFieldName() {
        return collectionFieldName;
    }
    
    public void setCollectionFieldName(String manyToField) {
        this.collectionFieldName = manyToField;
    }

    public String getLinkedObjectPath() {
        return linkedObjectPath;
    }

    public void setLinkedObjectPath(String linkedPropertyPath) {
        this.linkedObjectPath = linkedPropertyPath;
    }

    public String getTargetObjectPath() {
        return targetObjectPath;
    }

    public void setTargetObjectPath(String targetObjectPath) {
        this.targetObjectPath = targetObjectPath;
    }

    public String getAdornedTargetEntityClassname() {
        return adornedTargetEntityClassname;
    }

    public void setAdornedTargetEntityClassname(String adornedTargetEntityClassname) {
        this.adornedTargetEntityClassname = adornedTargetEntityClassname;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Boolean getSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(Boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public String getLinkedIdProperty() {
        return linkedIdProperty;
    }

    public void setLinkedIdProperty(String linkedIdProperty) {
        this.linkedIdProperty = linkedIdProperty;
    }

    public String getTargetIdProperty() {
        return targetIdProperty;
    }

    public void setTargetIdProperty(String targetIdProperty) {
        this.targetIdProperty = targetIdProperty;
    }

    public Boolean getInverse() {
        return inverse;
    }

    public void setInverse(Boolean inverse) {
        this.inverse = inverse;
    }
    
    public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }

    public String getAdornedTargetEntityPolymorphicType() {
        return adornedTargetEntityPolymorphicType;
    }

    public void setAdornedTargetEntityPolymorphicType(String adornedTargetEntityPolymorphicType) {
        this.adornedTargetEntityPolymorphicType = adornedTargetEntityPolymorphicType;
    }

    public String getJoinEntityClass() {
        return joinEntityClass;
    }

    public void setJoinEntityClass(String joinEntityClass) {
        this.joinEntityClass = joinEntityClass;
    }

    public Boolean getMutable() {
        return mutable;
    }

    public void setMutable(Boolean mutable) {
        this.mutable = mutable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AdornedTargetList{");
        sb.append("collectionFieldName='").append(collectionFieldName).append('\'');
        sb.append(", linkedObjectPath='").append(linkedObjectPath).append('\'');
        sb.append(", targetObjectPath='").append(targetObjectPath).append('\'');
        sb.append(", adornedTargetEntityClassname='").append(adornedTargetEntityClassname).append('\'');
        sb.append(", adornedTargetEntityPolymorphicType='").append(adornedTargetEntityPolymorphicType).append('\'');
        sb.append(", sortField='").append(sortField).append('\'');
        sb.append(", sortAscending=").append(sortAscending);
        sb.append(", linkedIdProperty='").append(linkedIdProperty).append('\'');
        sb.append(", targetIdProperty='").append(targetIdProperty).append('\'');
        sb.append(", inverse=").append(inverse);
        sb.append(", joinEntityClass='").append(joinEntityClass).append('\'');
        sb.append(", mutable=").append(mutable);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public PersistencePerspectiveItem clonePersistencePerspectiveItem() {
        AdornedTargetList adornedTargetList = new AdornedTargetList();
        adornedTargetList.collectionFieldName = collectionFieldName;
        adornedTargetList.linkedObjectPath = linkedObjectPath;
        adornedTargetList.targetObjectPath = targetObjectPath;
        adornedTargetList.adornedTargetEntityClassname = adornedTargetEntityClassname;
        adornedTargetList.adornedTargetEntityPolymorphicType = adornedTargetEntityPolymorphicType;
        adornedTargetList.sortField = sortField;
        adornedTargetList.sortAscending = sortAscending;
        adornedTargetList.linkedIdProperty = linkedIdProperty;
        adornedTargetList.targetIdProperty = targetIdProperty;
        adornedTargetList.inverse = inverse;
        adornedTargetList.joinEntityClass = joinEntityClass;
        adornedTargetList.mutable = mutable;

        return adornedTargetList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        AdornedTargetList that = (AdornedTargetList) o;

        if (adornedTargetEntityClassname != null ? !adornedTargetEntityClassname.equals(that.adornedTargetEntityClassname) : that.adornedTargetEntityClassname != null)
            return false;
        if (adornedTargetEntityPolymorphicType != null ? !adornedTargetEntityPolymorphicType.equals(that.adornedTargetEntityPolymorphicType) : that.adornedTargetEntityPolymorphicType != null)
            return false;
        if (collectionFieldName != null ? !collectionFieldName.equals(that.collectionFieldName) : that.collectionFieldName != null)
            return false;
        if (inverse != null ? !inverse.equals(that.inverse) : that.inverse != null) return false;
        if (linkedIdProperty != null ? !linkedIdProperty.equals(that.linkedIdProperty) : that.linkedIdProperty != null)
            return false;
        if (linkedObjectPath != null ? !linkedObjectPath.equals(that.linkedObjectPath) : that.linkedObjectPath != null)
            return false;
        if (sortAscending != null ? !sortAscending.equals(that.sortAscending) : that.sortAscending != null)
            return false;
        if (sortField != null ? !sortField.equals(that.sortField) : that.sortField != null) return false;
        if (targetIdProperty != null ? !targetIdProperty.equals(that.targetIdProperty) : that.targetIdProperty != null)
            return false;
        if (targetObjectPath != null ? !targetObjectPath.equals(that.targetObjectPath) : that.targetObjectPath != null)
            return false;
        if (joinEntityClass != null ? !joinEntityClass.equals(that.joinEntityClass) : that.joinEntityClass != null)
            return false;
        if (mutable != null ? !mutable.equals(that.mutable) : that.mutable != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = collectionFieldName != null ? collectionFieldName.hashCode() : 0;
        result = 31 * result + (linkedObjectPath != null ? linkedObjectPath.hashCode() : 0);
        result = 31 * result + (targetObjectPath != null ? targetObjectPath.hashCode() : 0);
        result = 31 * result + (adornedTargetEntityClassname != null ? adornedTargetEntityClassname.hashCode() : 0);
        result = 31 * result + (adornedTargetEntityPolymorphicType != null ? adornedTargetEntityPolymorphicType.hashCode() : 0);
        result = 31 * result + (sortField != null ? sortField.hashCode() : 0);
        result = 31 * result + (sortAscending != null ? sortAscending.hashCode() : 0);
        result = 31 * result + (linkedIdProperty != null ? linkedIdProperty.hashCode() : 0);
        result = 31 * result + (targetIdProperty != null ? targetIdProperty.hashCode() : 0);
        result = 31 * result + (inverse != null ? inverse.hashCode() : 0);
        result = 31 * result + (joinEntityClass != null ? joinEntityClass.hashCode() : 0);
        result = 31 * result + (mutable != null ? mutable.hashCode() : 0);
        return result;
    }
}
