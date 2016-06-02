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

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.BLCFieldUtils;

import java.lang.reflect.Field;
import java.util.Calendar;

import javax.persistence.Embedded;
import javax.persistence.Entity;

public abstract class AbstractAuditableListener {

    public abstract void setAuditCreatedBy(Object entity) throws Exception;

    public abstract void setAuditUpdatedBy(Object entity) throws Exception;

    protected abstract void setAuditValueAgent(Field field, Object entity) throws IllegalArgumentException, IllegalAccessException;

    public void setAuditCreatedBy(Object entity, Class auditableClass) throws Exception {
        if (entity.getClass().isAnnotationPresent(Entity.class)) {
            Field field = BLCFieldUtils.getSingleField(entity.getClass(), getAuditableFieldName());
            field.setAccessible(true);
            if (field.isAnnotationPresent(Embedded.class)) {
                Object auditable = field.get(entity);
                if (auditable == null) {
                    field.set(entity, auditableClass);
                    auditable = field.get(entity);
                }
                Field temporalCreatedField = auditable.getClass().getDeclaredField("dateCreated");
                Field temporalUpdatedField = auditable.getClass().getDeclaredField("dateUpdated");
                Field agentField = auditable.getClass().getDeclaredField("createdBy");
                setAuditValueTemporal(temporalCreatedField, auditable);
                setAuditValueTemporal(temporalUpdatedField, auditable);
                setAuditValueAgent(agentField, auditable);
            }
        }
    }

    public void setAuditUpdatedBy(Object entity, Class auditableClass) throws Exception {
        if (entity.getClass().isAnnotationPresent(Entity.class)) {
            Field field = BLCFieldUtils.getSingleField(entity.getClass(), getAuditableFieldName());
            field.setAccessible(true);
            if (field.isAnnotationPresent(Embedded.class)) {
                Object auditable = field.get(entity);
                if (auditable == null) {
                    field.set(entity, auditableClass);
                    auditable = field.get(entity);
                }
                Field temporalField = auditable.getClass().getDeclaredField("dateUpdated");
                Field agentField = auditable.getClass().getDeclaredField("updatedBy");
                setAuditValueTemporal(temporalField, auditable);
                setAuditValueAgent(agentField, auditable);
            }
        }
    }
    
    protected void setAuditValueTemporal(Field field, Object entity) throws IllegalArgumentException, IllegalAccessException {
        Calendar cal = SystemTime.asCalendar();
        field.setAccessible(true);
        field.set(entity, cal.getTime());
    }

    protected String getAuditableFieldName() {
        return "auditable";
    }
    
}
