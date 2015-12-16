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
package org.broadleafcommerce.common.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to describe a member of a Map structure that should be
 * displayed as a regular field in the admin tool.
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationMapField {

    /**
     * <p>Represents the field name for this field.</p>
     *
     * @return the name for this field
     */
    String fieldName();

    /**
     * <p>Represents the metadata for this field. The <tt>AdminPresentation</tt> properties will be used
     * by the system to determine how this field should be treated in the admin tool (e.g. date fields get
     * a date picker in the UI)</p>
     *
     * @return the descriptive metadata for this field
     */
    AdminPresentation fieldPresentation();

    /**
     * <p>Optional - if the Map structure is using generics, then the system can usually infer the concrete
     * type for the Map value. However, if not using generics for the Map, or if the value cannot be clearly
     * inferred, you can explicitly set the Map structure value type here. Map fields can only understand
     * maps whose values are basic types (String, Long, Date, etc...). Complex types require additional
     * support. Support is provided out-of-the-box for complex types <tt>ValueAssignable</tt>,
     * and <tt>SimpleRule</tt>.</p>
     *
     * @return the concrete type for the Map structure value
     */
    Class<?> targetClass() default Void.class;

    /**
     * <p>Optional - if the value is not primitive and contains a bi-directional reference back to the entity containing
     * this map structure, you can declare the field name in the value class for this reference. Note, if the map
     * uses the JPA mappedBy property, the system will try to infer the manyToField value so you don't have to set
     * it here.</p>
     *
     * @return the parent entity referring field name
     */
    String manyToField() default "";

}
