/*
 * #%L
 * broadleaf-enterprise
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.weave;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.audit.AdminAudit;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.EntityListeners;

/**
 * @author by ckittrell
 */
@EntityListeners(value = { AdminAuditableListener.class })
public final class WeaveAdminAuditable implements AdminAudit {

    @Embedded
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected AdminAuditable auditable = new AdminAuditable();

    @Override
    public Long getCreatedBy() {
        return getEmbeddableAdminAuditable(false).getCreatedBy();
    }

    @Override
    public Date getDateCreated() {
        return getEmbeddableAdminAuditable(false).getDateCreated();
    }

    @Override
    public Date getDateUpdated() {
        return getEmbeddableAdminAuditable(false).getDateUpdated();
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        getEmbeddableAdminAuditable(true).setDateCreated(dateCreated);
    }

    @Override
    public void setDateUpdated(Date dateUpdated) {
        getEmbeddableAdminAuditable(true).setDateUpdated(dateUpdated);
    }

    @Override
    public void setCreatedBy(Long createdBy) {
        getEmbeddableAdminAuditable(true).setCreatedBy(createdBy);
    }

    @Override
    public Long getUpdatedBy() {
        return getEmbeddableAdminAuditable(false).getUpdatedBy();
    }

    @Override
    public void setUpdatedBy(Long updatedBy) {
        getEmbeddableAdminAuditable(true).setUpdatedBy(updatedBy);
    }

    public AdminAuditable getEmbeddableAdminAuditable(boolean assign) {
        AdminAuditable temp = auditable;
        if (temp == null) {
            temp = new AdminAuditable();
            if (assign) {
                auditable = temp;
            }
        }
        return temp;
    }
}
