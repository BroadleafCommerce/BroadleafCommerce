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

import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.springframework.core.Ordered;

import java.io.Serializable;
import java.util.Map;

/**
 * Classes implementing this interface are capable of handling persistence related events for fields whose values
 * are being requested or set for the admin. This includes any special translations or transformations required to get
 * from the string representation in the admin back to the field on a Hibernate entity - and the reverse. Providers are
 * typically added in response to new admin presentation annotation support that requires special persistence behavior.
 * Note, <tt>PersistenceProviders</tt> are part of BasicPersistenceModule, and therefore relate to variations on persistence of
 * basic fields. For more exotic field types (e.g. Maps), <tt>PersistenceModules</tt> are used (e.g. MapStructurePersistenceModule).
 * Implementers should generally extend <tt>PersistenceProviderAdapter</tt>.
 *
 * @see org.broadleafcommerce.openadmin.server.service.persistence.module.PersistenceModule
 * @author Jeff Fischer
 */
public interface PersistenceProvider extends Ordered {

    //standard ordering constants for BLC providers
    public static final int BASIC = 1000;
    public static final int MAP_FIELD = 2000;
    public static final int RULE = 3000;

    /**
     * Set the property value on the target object. Implementations should translate and set the requestedValue
     * field from the request on the instance parameter.
     *
     * @param populateValueRequest contains the requested value and support classes.
     * @param instance the entity instance on which to set the value harvested from the request
     * @return whether or not the implementation handled the persistence request
     */
    boolean populateValue(PopulateValueRequest populateValueRequest, Serializable instance);

    /**
     * Retrieve the property value from the provided value. Implementations should translate the requestedValue
     * and set on the property parameter.
     *
     * @param extractValueRequest contains the requested value and support classes.
     * @param property the property for the admin that contains the information harvested from the persistence value
     * @return whether or not the implementation handled the persistence request
     */
    boolean extractValue(ExtractValueRequest extractValueRequest, Property property);

    /**
     * Add criteria to the ctoConverter. The CtoConverter is used by the system to refine the fetch criteria
     * used to retrieve lists of records for the admin. The requestedCto contains filters requested from the admin
     * and is generally used to drive the criteria added to CtoConverter.
     *
     * @param addSearchMappingRequest contains the requested cto and support classes.
     * @param ctoConverter search info should be added to converter. It is responsible for generating the final search criteria.
     * @return whether or not the implementation handled the persistence request
     */
    boolean addSearchMapping(AddSearchMappingRequest addSearchMappingRequest, BaseCtoConverter ctoConverter);

    /**
     * Filter the list of properties posted by the admin during and add or update. This is the property list
     * immediately before persistence is attempted. Properties may be altered, removed or added.
     *
     * @param addFilterPropertiesRequest contains the <tt>Entity</tt> instance.
     * @param properties the collection of properties to filter
     * @return whether or not the implementation handled the persistence request
     */
    boolean filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest, Map<String, FieldMetadata> properties);

}
