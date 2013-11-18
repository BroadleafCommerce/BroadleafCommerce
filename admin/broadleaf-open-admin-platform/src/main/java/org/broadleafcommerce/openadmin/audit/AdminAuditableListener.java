/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.audit;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.SandBoxContext;
import org.broadleafcommerce.openadmin.security.AdminSandBoxContext;

import java.lang.reflect.Field;
import java.util.Calendar;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;


public class AdminAuditableListener {

    @PrePersist
    public void setAuditCreatedBy(Object entity) throws Exception {
        if (entity.getClass().isAnnotationPresent(Entity.class)) {
            Field field = getSingleField(entity.getClass(), "auditable");
            field.setAccessible(true);
            if (field.isAnnotationPresent(Embedded.class)) {
                Object auditable = field.get(entity);
                if (auditable == null) {
                    field.set(entity, new AdminAuditable());
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

    @PreUpdate
    public void setAuditUpdatedBy(Object entity) throws Exception {
        if (entity.getClass().isAnnotationPresent(Entity.class)) {
            Field field = getSingleField(entity.getClass(), "auditable");
            field.setAccessible(true);
            if (field.isAnnotationPresent(Embedded.class)) {
                Object auditable = field.get(entity);
                if (auditable == null) {
                    field.set(entity, new AdminAuditable());
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

    protected void setAuditValueAgent(Field field, Object entity) throws IllegalArgumentException, IllegalAccessException {
        try {
            SandBoxContext context = SandBoxContext.getSandBoxContext();
            if (context != null && context instanceof AdminSandBoxContext) {
                AdminSandBoxContext adminContext = (AdminSandBoxContext) context;
                field.setAccessible(true);
                field.set(entity, adminContext.getAdminUser().getId());
            }
        } catch (IllegalStateException e) {
            //do nothing
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Field getSingleField(Class<?> clazz, String fieldName) throws IllegalStateException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nsf) {
            // Try superclass
            if (clazz.getSuperclass() != null) {
                return getSingleField(clazz.getSuperclass(), fieldName);
            }

            return null;
        }
    }
}
