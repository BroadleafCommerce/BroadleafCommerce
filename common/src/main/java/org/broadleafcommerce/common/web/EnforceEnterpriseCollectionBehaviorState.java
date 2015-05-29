/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.web;

/**
 * Defines the state in which sandboxable collections in the Enterprise module should adhere to Broadleaf defined behavior.
 * When FALSE, {@link org.hibernate.collection.spi.PersistentCollection} extensions in the Enterprise module will delegate
 * to the standard Hibernate behavior. This is useful when the desire is to build and persist entity object structures (that
 * the Enterprise module would otherwise interpret as sandboxable) without interference from the Enterprise module
 * on the collection persistence behavior. When the Enterprise module is loaded, the behavior is enforced by default.
 *
 * @author Jeff Fischer
 */
public enum EnforceEnterpriseCollectionBehaviorState {
    TRUE,FALSE,UNDEFINED
}
