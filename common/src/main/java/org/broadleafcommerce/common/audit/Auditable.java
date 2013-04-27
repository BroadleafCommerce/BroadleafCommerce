/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.audit;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;

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
