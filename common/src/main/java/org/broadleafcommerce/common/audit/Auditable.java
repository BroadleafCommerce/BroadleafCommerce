/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.audit;

import org.broadleafcommerce.common.presentation.AdminPresentation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "DATE_CREATED", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @AdminPresentation(friendlyName = "Auditable_Date_Created", order = 1000,
            tab = Presentation.Tab.Name.Audit, tabOrder = Presentation.Tab.Order.Audit,
            group = "Auditable_Audit", groupOrder = 1000,
            readOnly = true)
    protected Date dateCreated;

    @Column(name = "CREATED_BY", updatable = false)
    @AdminPresentation(friendlyName = "Auditable_Created_By", order = 2000,
            tab = Presentation.Tab.Name.Audit, tabOrder = Presentation.Tab.Order.Audit,
            group = "Auditable_Audit",
            readOnly = true)
    protected Long createdBy;

    @Column(name = "DATE_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    @AdminPresentation(friendlyName = "Auditable_Date_Updated", order = 3000,
            tab = Presentation.Tab.Name.Audit, tabOrder = Presentation.Tab.Order.Audit,
            group = "Auditable_Audit",
            readOnly = true)
    protected Date dateUpdated;

    @Column(name = "UPDATED_BY")
    @AdminPresentation(friendlyName = "Auditable_Updated_By", order = 4000,
            tab = Presentation.Tab.Name.Audit, tabOrder = Presentation.Tab.Order.Audit,
            group = "Auditable_Audit",
            readOnly = true)
    protected Long updatedBy;

    public Date getDateCreated() {
        return dateCreated;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        Auditable auditable = (Auditable) o;

        if (createdBy != null ? !createdBy.equals(auditable.createdBy) : auditable.createdBy != null) return false;
        if (dateCreated != null ? !dateCreated.equals(auditable.dateCreated) : auditable.dateCreated != null)
            return false;
        if (dateUpdated != null ? !dateUpdated.equals(auditable.dateUpdated) : auditable.dateUpdated != null)
            return false;
        if (updatedBy != null ? !updatedBy.equals(auditable.updatedBy) : auditable.updatedBy != null) return false;

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

    public static class Presentation {

        public static class Tab {
            public static class Name {
                public static final String Audit = "Auditable_Tab";
            }
            public static class Order {
                public static final int Audit = 99000;
            }
        }

        public static class Group {
            public static class Name {

                public static final String Audit = "Auditable_Audit";
            }
            public static class Order {

                public static final int Audit = 1000;
            }
        }
    }

}
