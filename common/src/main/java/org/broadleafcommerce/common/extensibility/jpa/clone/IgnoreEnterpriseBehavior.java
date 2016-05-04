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
package org.broadleafcommerce.common.extensibility.jpa.clone;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Marker annotation for any field on a sandboxable entity that should not undergo enterprise sandbox config validation
 * (if applicable). This has added benefits when marking a collection field on a sandboxable entity:
 * </p>
 * <ul>
 * <li>When fetched, the collection will behave as a standard Hibernate collection, rather than enforcing enterprise behavior</li>
 * <li>Adds and removals on the collection directly should behave as expected as long as the collection member type is itself
 * not sandboxable</li>
 *</ul>
 * Please note, it is expected when using this annotation on a collection that the collection member type is not sandboxable. Otherwise,
 * persistence of the collection member will itself engage sandbox state and will lead to unexpected results when fetching
 * the collection members.
 * </p>
 * When used according to these guidelines, this annotation can be used to allow an otherwise sandboxable entity to have
 * a non-sandboxable, ToMany relationship to another non-sandboxable entity.
 *
 * @author jfischer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface IgnoreEnterpriseBehavior {

}
