/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.audit.AbstractAuditableListener;
import org.broadleafcommerce.common.audit.CrossAppAuditable;
import org.broadleafcommerce.common.util.BLCFieldUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.lang.reflect.Field;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class AdminAuditableListener extends AbstractAuditableListener {

    private static final Log LOG = LogFactory.getLog(AdminAuditableListener.class);

    @PrePersist
    @Override
    public void setAuditCreationAndUpdateData(Object entity) throws Exception {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc.getAdmin() || (entity instanceof CrossAppAuditable)) {
            if (!isAuditCreationDataPresent(entity)) {
                setAuditCreationData(entity, new AdminAuditable());
            }
            setAuditUpdateData(entity, new AdminAuditable());
        }
    }

    /**
     * Determines whether the entity already carries creation audit data (e.g. a clone or copy being persisted),
     * in which case it should not be overwritten. Most entities do not implement {@link AdminAudit} themselves
     * and instead hold it in an embedded auditable field, so that field is checked as well.
     *
     * @param entity the entity being persisted
     * @return true if dateCreated or createdBy is already set
     */
    protected boolean isAuditCreationDataPresent(Object entity) throws IllegalAccessException {
        Object auditable = entity;
        if (!(auditable instanceof AdminAudit)) {
            Field field = BLCFieldUtils.getSingleField(entity.getClass(), getAuditableFieldName());
            if (field == null) {
                return false;
            }
            field.setAccessible(true);
            auditable = field.get(entity);
        }
        return (auditable instanceof AdminAudit audit)
                && (audit.getDateCreated() != null || audit.getCreatedBy() != null);
    }

    @PreUpdate
    @Override
    public void setAuditUpdateData(Object entity) throws Exception {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc.getAdmin() || (entity instanceof CrossAppAuditable)) {
            setAuditUpdateData(entity, new AdminAuditable());
        }
    }

    @Override
    protected void setAuditValueAgent(Field field, Object entity) throws IllegalArgumentException, IllegalAccessException {
        try {
            BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
            if (context != null && context.getAdminUserId() != null) {
                if (context.getAdmin() || (entity instanceof CrossAppAuditable)) {
                    field.setAccessible(true);
                    field.set(entity, context.getAdminUserId());
                }
            }
        } catch (IllegalStateException e) {
            //do nothing
        } catch (Exception e) {
            LOG.error("Error setting admin audit field.", e);
        }
    }

}
