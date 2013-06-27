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
