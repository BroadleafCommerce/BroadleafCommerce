/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * A request to modify an {@link EntityForm}, or {@link ListGrid} instance. Contains supporting objects useful during the modification process.
 *
 * @see EntityFormModifier#modifyEntityForm(EntityFormModifierRequest)
 * @author Jeff Fischer
 */
public class EntityFormModifierRequest {

    protected EntityForm entityForm;
    protected ListGrid listGrid;
    protected EntityFormModifierData<EntityFormModifierDataPoint> configuration;
    protected AdminUser currentUser;
    protected Entity entity;
    protected RowLevelSecurityService rowLevelSecurityService;

    public EntityFormModifierRequest withEntityForm(EntityForm entityForm) {
        setEntityForm(entityForm);
        return this;
    }

    public EntityFormModifierRequest withListGrid(ListGrid listGrid) {
        setListGrid(listGrid);
        return this;
    }

    public EntityFormModifierRequest withConfiguration(EntityFormModifierData<EntityFormModifierDataPoint> configuration) {
        setConfiguration(configuration);
        return this;
    }

    public EntityFormModifierRequest withCurrentUser(AdminUser currentUser) {
        setCurrentUser(currentUser);
        return this;
    }

    public EntityFormModifierRequest withEntity(Entity entity) {
        setEntity(entity);
        return this;
    }

    public EntityFormModifierRequest withRowLevelSecurityService(RowLevelSecurityService rowLevelSecurityService) {
        setRowLevelSecurityService(rowLevelSecurityService);
        return this;
    }


    public EntityForm getEntityForm() {
        return entityForm;
    }

    public void setEntityForm(EntityForm entityForm) {
        this.entityForm = entityForm;
    }

    public EntityFormModifierData<EntityFormModifierDataPoint> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(EntityFormModifierData<EntityFormModifierDataPoint> configuration) {
        this.configuration = configuration;
    }

    public AdminUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(AdminUser currentUser) {
        this.currentUser = currentUser;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public RowLevelSecurityService getRowLevelSecurityService() {
        return rowLevelSecurityService;
    }

    public void setRowLevelSecurityService(RowLevelSecurityService rowLevelSecurityService) {
        this.rowLevelSecurityService = rowLevelSecurityService;
    }

    public ListGrid getListGrid() {
        return listGrid;
    }

    public void setListGrid(ListGrid listGrid) {
        this.listGrid = listGrid;
    }
}
