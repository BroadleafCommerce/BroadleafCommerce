/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
