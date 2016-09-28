package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * A request to modify an {@link EntityForm} instance. Contains supporting objects useful during the modification process.
 *
 * @see EntityFormModifier#modify(EntityFormModifierRequest)
 * @author Jeff Fischer
 */
public class EntityFormModifierRequest {

    protected EntityForm entityForm;
    protected EntityFormModifierData<EntityFormModifierDataPoint> configuration;
    protected AdminUser currentUser;
    protected Entity entity;
    protected RowLevelSecurityService rowLevelSecurityService;

    public EntityFormModifierRequest withEntityForm(EntityForm entityForm) {
        setEntityForm(entityForm);
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
}
