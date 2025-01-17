/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.broadleafcommerce.openadmin.dto.visitor.PersistencePerspectiveItemVisitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class ParentRecordStructure implements Serializable, PersistencePerspectiveItem {

    @Serial
    private static final long serialVersionUID = 1L;

    private Entity parentRecord;
    private ClassMetadata parentMetadata;

    public ParentRecordStructure() {
        //do nothing
    }

    public ParentRecordStructure(Entity parentRecord, ClassMetadata parentMetadata) {
        this.parentRecord = parentRecord;
        this.parentMetadata = parentMetadata;
    }

    public Entity getParentRecord() {
        return parentRecord;
    }

    public void setParentRecord(Entity parentRecord) {
        this.parentRecord = parentRecord;
    }

    public ClassMetadata getParentMetadata() {
        return parentMetadata;
    }

    public void setParentMetadata(ClassMetadata parentMetadata) {
        this.parentMetadata = parentMetadata;
    }

    @Override
    public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public PersistencePerspectiveItem clonePersistencePerspectiveItem() {
        ParentRecordStructure parentRecordStructure = new ParentRecordStructure();
        parentRecordStructure.parentRecord = parentRecord;
        parentRecordStructure.parentMetadata = parentMetadata;

        return parentRecordStructure;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParentRecordStructure{");
        if (parentRecord != null) {
            sb.append(parentRecord.toString()).append('\'');
        }
        if (parentMetadata != null && parentMetadata.getProperties() != null) {
            sb.append(", parentMetadata=").append(Arrays.toString(parentMetadata.getProperties()));
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParentRecordStructure)) return false;

        ParentRecordStructure that = (ParentRecordStructure) o;

        if (parentRecord != null ? !parentRecord.equals(that.parentRecord) : that.parentRecord != null) return false;
        return parentMetadata != null ? parentMetadata.equals(that.parentMetadata) : that.parentMetadata == null;
    }

    @Override
    public int hashCode() {
        int result = parentRecord != null ? parentRecord.hashCode() : 0;
        result = 31 * result + (parentMetadata != null ? parentMetadata.hashCode() : 0);
        return result;
    }

}
