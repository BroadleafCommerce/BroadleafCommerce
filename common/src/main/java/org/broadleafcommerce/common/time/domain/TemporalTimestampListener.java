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
package org.broadleafcommerce.common.time.domain;

import org.broadleafcommerce.common.time.SystemTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

public class TemporalTimestampListener {

    @PrePersist
    @PreUpdate
    public void setTimestamps(Object entity) throws Exception {
        if (entity.getClass().isAnnotationPresent(Entity.class)) {
            Field[] fields = entity.getClass().getDeclaredFields();
            setTimestamps(fields, entity);
        }
    }

    private void setTimestamps(Field[] fields, Object entity) throws Exception {
        Calendar cal = null;
        for (Field field : fields) {
            Class<?> type = field.getType();
            Temporal temporalAnnotation = field.getAnnotation(Temporal.class);

            if (temporalAnnotation != null) {
                if (field.isAnnotationPresent(Column.class)) {
                    field.setAccessible(true);
                    try {
                        if (TemporalType.TIMESTAMP.equals(temporalAnnotation.value()) && (field.isAnnotationPresent(AutoPopulate.class))) {
                            if (field.get(entity) == null || field.getAnnotation(AutoPopulate.class).autoUpdateValue()) {
                                if (type.isAssignableFrom(Date.class)) {
                                    if (cal == null) {
                                        cal = SystemTime.asCalendar();
                                    }
                                    field.set(entity, cal.getTime());
                                } else if (type.isAssignableFrom(Calendar.class)) {
                                    if (cal == null) {
                                        cal = SystemTime.asCalendar();
                                    }
                                    field.set(entity, cal);
                                }
                            }
                        }
                    } finally {
                        field.setAccessible(false);
                    }
                }
            } else if (field.isAnnotationPresent(Embedded.class)) {
                field.setAccessible(true);
                try {
                    // Call recursively
                    setTimestamps(field.getType().getDeclaredFields(), field.get(entity));
                } finally {
                    field.setAccessible(false);
                }
            }
        }
    }
}
