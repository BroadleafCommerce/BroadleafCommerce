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
 * This annotation is used to describe an array of map fields that allow map members
 * to be displayed as regular fields in the admin tool.
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationMapFields {

    /**
     * Members of this map can be displayed as form fields, rather than in a standard grid. When populated,
     * mapDisplayFields informs the form building process to create the fields described here and persist those fields
     * in this map structure.
     *
     * @return the fields to display that represent the members of this map
     */
    AdminPresentationMapField[] mapDisplayFields();

    /**
     * <p>Optional - if the intended map value is actually buried inside of a modelled join entity, specify the
     * the path to that value here. For example, SkuImpl.skuMedia uses SkuMediaXrefImpl, but the intended value
     * is Media, so the toOneTargetProperty annotation param is "media". Note - only declare here if the field
     * does not also have an {@link org.broadleafcommerce.common.presentation.AdminPresentationMap} annotation
     * already, which is the preferred location for declaring this value.</p>
     *
     * @return the path to the intended map value field in the join entity
     */
    String toOneTargetProperty() default "";

    /**
     * <p>Optional - if the intended map value is actually buried inside of a modelled join entity, specify the
     * the path to that parent here. For example, SkuImpl.skuMedia uses SkuMediaXrefImpl, and the parent reference
     * inside SkuMediaXrefImpl is to Sku, so the toOneParentProperty annotation param is "sku". Note - only declare here if the field
     * does not also have an {@link org.broadleafcommerce.common.presentation.AdminPresentationMap} annotation
     * already, which is the preferred location for declaring this value.</p>
     *
     * @return the path to the parent in the join entity
     */
    String toOneParentProperty() default "";
}
