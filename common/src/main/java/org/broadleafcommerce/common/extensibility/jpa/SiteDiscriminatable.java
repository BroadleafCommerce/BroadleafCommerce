/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.extensibility.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark collections for multi-tenancy when the commercial multi-tenant module is loaded. Please note, multi-tenant
 * collections are NOT eligible for level 2 cache, which results in a query to the database every time the collection is lazy initialized.
 * This can result in production performance degradation depending on how frequently the collection is utilized. It is
 * for this reason that we recommend utilizing a custom service or service extension that explicitly creates a query
 * for the collection entity members based on the parent entity. See the multi-tenant module documentation for more
 * information.
 *
 * @deprecated The system should be automatically able to detect collections that require multitenant behavior. An exception
 * to this is a ManyToMany collection using a join table. In such a situation, you should consider modelling the join table
 * as an entity itself and convert the collection to a OneToMany using that new "join table" entity.
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Deprecated
public @interface SiteDiscriminatable {

    SiteDiscriminatableType type();

}
