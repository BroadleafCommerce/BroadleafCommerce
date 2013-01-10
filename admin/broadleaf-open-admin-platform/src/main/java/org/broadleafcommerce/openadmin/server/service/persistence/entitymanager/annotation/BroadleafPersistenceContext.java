/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager.annotation;

import javax.persistence.PersistenceProperty;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface BroadleafPersistenceContext {

    /**
     * (Optional) The name by which the entity manager is to be accessed in the
     * environment referencing context; not needed when dependency
     * injection is used.
     */
    String name() default "";

    /**
     * (Optional) The name of the persistence unit as defined in the
     * <code>persistence.xml</code> file. If the <code>unitName</code> element is
     * specified, the persistence unit for the entity manager that is
     * accessible in JNDI must have the same name.
     */
    String unitName() default "";
    
    /**
     * (Optional) The name of the sand box persistence unit as defined in the
     * <code>persistence.xml</code> file. If the <code>sandBoxUnitName</code> element is
     * specified, the persistence unit for the entity manager that is
     * accessible in JNDI must have the same name. Sand Boxes are a unique concept
     * to Broadleaf Commerce and represent a repository for change sets generated
     * through the Broadleaf administrative application. Sand box persistence units
     * are defined in <code>persistence.xml</code> like any other persistence unit.
     */
    String sandBoxUnitName() default "";

    /**
     * (Optional) Properties for the container or persistence
     * provider.  Vendor specific properties may be included in this
     * set of properties.  Properties that are not recognized by
     * a vendor are ignored.
     */
    PersistenceProperty[] properties() default {};
}
