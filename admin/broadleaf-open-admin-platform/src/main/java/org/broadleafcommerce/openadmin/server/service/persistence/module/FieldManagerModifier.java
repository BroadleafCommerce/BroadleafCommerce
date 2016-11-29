/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.springframework.core.Ordered;
import java.lang.reflect.Field;
import javax.persistence.EntityManager;

/**
 * This interface is responsible for modifying the behavior of {@link FieldManager#setFieldValue(Object, String, Object)}
 * and {@link FieldManager#getFieldValue(Object, String)}.
 *
 * @see FieldManager
 * @author Nick Crum ncrum
 */
public interface FieldManagerModifier extends Ordered {

    /**
     * Determines whether this modifier is able handle writing or reading this {@code Field}.
     *
     * @param field the Field that is being handled
     * @param value the value that is being set or read
     * @param em the EntityManager
     * @return whether this field can be handled
     */
    public boolean canHandle(Field field, Object value, EntityManager em);

    /**
     * Returns a modified write value for the field.
     *
     * @see FieldManager#setFieldValue(Object, String, Object)
     * @param field the field being modified
     * @param value the bean or object with the field
     * @param newValue the new value for the field
     * @param em the {@code EntityManager}
     * @return the modified write value
     * @throws IllegalAccessException
     */
    public Object getModifiedWriteValue(Field field, Object value, Object newValue, EntityManager em) throws IllegalAccessException;

    /**
     * Returns a modified read value for the field.
     *
     * @see FieldManager#getFieldValue(Object, String)
     * @param field the field being modified
     * @param value the value of the field
     * @param em the {@code EntityManager}
     * @return
     * @throws IllegalAccessException
     */
    public Object getModifiedReadValue(Field field, Object value, EntityManager em) throws IllegalAccessException;
}
