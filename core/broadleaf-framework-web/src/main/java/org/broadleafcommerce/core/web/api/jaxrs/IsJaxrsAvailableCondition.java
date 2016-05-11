/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.jaxrs;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * <p>
 * Condition class that checks for the presence of a JAXRS class to determine if JAXRS is actually loaded
 *
 * <p>
 * By default, this checks the existence of javax.ws.rs.core.UriInfo
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class IsJaxrsAvailableCondition implements Condition {

    /**
     * Fully-qualified name of a class that is representative of JAXRS being loaded
     */
    public static String JAXRSCLASS = "javax.ws.rs.core.UriInfo";
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            ClassUtils.getClass(JAXRSCLASS);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

}
