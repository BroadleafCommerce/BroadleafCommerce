/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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

import java.io.Serializable;
import java.util.Arrays;

public class ParentRecordStructure implements Serializable, PersistencePerspectiveItem {

    private static final long serialVersionUID = 1L;

    private Entity parentRecord;
    private ClassMetadata mainMetadata;

    public ParentRecordStructure() {
        //do nothing
    }

    public ParentRecordStructure(Entity parentRecord, ClassMetadata mainMetadata) {
        this.parentRecord = parentRecord;
        this.mainMetadata = mainMetadata;
    }

    public Entity getParentRecord() {
        return parentRecord;
    }

    public void setParentRecord(Entity parentRecord) {
        this.parentRecord = parentRecord;
    }

    public ClassMetadata getMainMetadata() {
        return mainMetadata;
    }

    public void setMainMetadata(ClassMetadata mainMetadata) {
        this.mainMetadata = mainMetadata;
    }

    @Override
    public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public PersistencePerspectiveItem clonePersistencePerspectiveItem() {
        ParentRecordStructure parentRecordStructure = new ParentRecordStructure();
        parentRecordStructure.parentRecord = parentRecord;
        parentRecordStructure.mainMetadata = mainMetadata;

        return parentRecordStructure;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParentRecordStructure{");
        if (parentRecord != null) {
            sb.append(parentRecord.toString()).append('\'');
        }
        if (mainMetadata != null && mainMetadata.getProperties() != null) {
            sb.append(", mainMetadata=").append(Arrays.toString(mainMetadata.getProperties()));
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
        return mainMetadata != null ? mainMetadata.equals(that.mainMetadata) : that.mainMetadata == null;
    }

    @Override
    public int hashCode() {
        int result = parentRecord != null ? parentRecord.hashCode() : 0;
        result = 31 * result + (mainMetadata != null ? mainMetadata.hashCode() : 0);
        return result;
    }
}
