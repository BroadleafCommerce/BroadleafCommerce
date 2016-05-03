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
package org.broadleafcommerce.openadmin.audit;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.sandbox.SandBoxNonProductionSkip;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class AdminAuditable implements Serializable, SandBoxNonProductionSkip, AdminAudit {

    private static final long serialVersionUID = 1L;

    @Column(name = "DATE_CREATED", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @AdminPresentation(friendlyName = "AdminAuditable_Date_Created", group = "AdminAuditable_Audit",
            tab="AdminAuditable_Audit", tabOrder = Integer.MAX_VALUE, order = 2,
            readOnly = true)
    protected Date dateCreated;

    @Column(name = "CREATED_BY", updatable = false)
    @AdminPresentation(friendlyName = "AdminAuditable_Created_By", group = "AdminAuditable_Audit",
            tab="AdminAuditable_Audit", tabOrder = Integer.MAX_VALUE, order = 1,
            readOnly = true)
    protected Long createdBy;

    @Column(name = "DATE_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    @AdminPresentation(friendlyName = "AdminAuditable_Date_Updated", group = "AdminAuditable_Audit",
            tab="AdminAuditable_Audit", tabOrder = Integer.MAX_VALUE, order = 4,
            readOnly = true)
    protected Date dateUpdated;

    @Column(name = "UPDATED_BY")
    @AdminPresentation(friendlyName = "AdminAuditable_Updated_By", group = "AdminAuditable_Audit",
            tab="AdminAuditable_Audit", tabOrder = Integer.MAX_VALUE, order = 3,
            readOnly = true)
    protected Long updatedBy;

    @Override
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public Date getDateUpdated() {
        return dateUpdated;
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Long getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        AdminAuditable that = (AdminAuditable) o;

        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (dateCreated != null ? !dateCreated.equals(that.dateCreated) : that.dateCreated != null) return false;
        if (dateUpdated != null ? !dateUpdated.equals(that.dateUpdated) : that.dateUpdated != null) return false;
        if (updatedBy != null ? !updatedBy.equals(that.updatedBy) : that.updatedBy != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dateCreated != null ? dateCreated.hashCode() : 0;
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (dateUpdated != null ? dateUpdated.hashCode() : 0);
        result = 31 * result + (updatedBy != null ? updatedBy.hashCode() : 0);
        return result;
    }
}
