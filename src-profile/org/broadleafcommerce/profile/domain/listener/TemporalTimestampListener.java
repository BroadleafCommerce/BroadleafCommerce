package org.broadleafcommerce.profile.domain.listener;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.annotation.AutoPopulate;

public class TemporalTimestampListener {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

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
                                        cal = new GregorianCalendar();
                                    }
                                    field.set(entity, cal.getTime());
                                } else if (type.isAssignableFrom(Calendar.class)) {
                                    if (cal == null) {
                                        cal = new GregorianCalendar();
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
