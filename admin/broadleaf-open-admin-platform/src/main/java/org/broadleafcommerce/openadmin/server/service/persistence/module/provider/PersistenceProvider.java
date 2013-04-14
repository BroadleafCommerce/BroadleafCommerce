/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;

/**
 * Classes implementing this interface are capable of handling persistence related events for fields whose values
 * are being requested or set for the admin. This includes any special translations or transformations required to get
 * from the string representation in the admin back to the field on a Hibernate entity - and the reverse. Providers are
 * typically added in response to new admin presentation annotation support that requires special persistence behavior.
 * Implementers should generally extend <tt>PersistenceProviderAdapter</tt>.
 *
 * @author Jeff Fischer
 */
public interface PersistenceProvider {

    /**
     * Set the property value on the target object. Implementations should translate and set the requestedValue
     * field from the request on the requestedInstance field from the request.
     *
     * @param populateValueRequest contains the requested value, instance and support classes.
     * @return whether or not the implementation handled the persistence request
     */
    boolean populateValue(PopulateValueRequest populateValueRequest);

    /**
     * Retrieve the property value from the provided value. Implementations should translate the requestedValue
     * and set on the requestedProperty.
     *
     * @param extractValueRequest contains the requested value, property and support classes.
     * @return whether or not the implementation handled the persistence request
     */
    boolean extractValue(ExtractValueRequest extractValueRequest);

    /**
     * Add criteria to the requestCtoConverter. The CtoConverter is used by the system to refine the fetch criteria
     * used to retrieve lists of records for the admin. The requestedCto contains filters requested from the admin
     * and is generally used to drive the criteria added to CtoConverter.
     *
     * @param addSearchMappingRequest contains the requested ctoConverter, cto and support classes.
     * @return whether or not the implementation handled the persistence request
     */
    boolean addSearchMapping(AddSearchMappingRequest addSearchMappingRequest);

    /**
     * Filter the list of properties posted by the admin during and add or update. This is the property list
     * immediately before persistence is attempted. Properties may be altered, removed or added.
     *
     * @param addFilterPropertiesRequest contains the <tt>Entity</tt> instance and unfiltered property list.
     * @return whether or not the implementation handled the persistence request
     */
    boolean filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest);

}
