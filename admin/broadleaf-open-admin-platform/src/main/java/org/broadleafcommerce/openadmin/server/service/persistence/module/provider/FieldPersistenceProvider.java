/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.core.Ordered;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Classes implementing this interface are capable of handling persistence related events for fields whose values
 * are being requested or set for the admin. This includes any special translations or transformations required to get
 * from the string representation in the admin back to the field on a Hibernate entity - and the reverse. Providers are
 * typically added in response to new admin presentation annotation support that requires special persistence behavior.
 * Note, {@link FieldPersistenceProvider} instances are part of {@link org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule},
 * and therefore relate to variations on persistence of basic fields. Implementers should generally
 * extend {@link FieldPersistenceProviderAdapter}.
 *
 * @see org.broadleafcommerce.openadmin.server.service.persistence.module.PersistenceModule
 * @author Jeff Fischer
 */
public interface FieldPersistenceProvider extends Ordered {

    //standard ordering constants for BLC providers
    public static final int BASIC = Integer.MAX_VALUE;
    /**
     * The {@link MediaFieldPersistenceProvider} MUST come prior to the normal Map field provider since they can both
     * respond to the same type of map fields. However, the Media fields are a special case since it needs to parse out the
     * Media DTO
     */
    public static final int MEDIA = 20000;
    /**
     * The {@link RuleFieldPersistenceProvider} MUST come prior to the normal map field provider. They both deal with map
     * field types but rules are a special case
     */
    public static final int RULE = 30000;
    public static final int MAP_FIELD = 40000;
    public static final int MONEY = 50000;
    public static final int HTML = 60000;

    /**
     * Set the property value on the target object. Implementations should translate the requestedValue
     * field from the request and set on the <tt>instance</tt> parameter. You are basically taking the string value
     * submitted by the admin application and converting it into the format required to set on the target
     * field of <tt>instance</tt> (which should be a JPA managed entity). Used during admin create and update events.
     *
     * @param populateValueRequest contains the requested value and support classes.
     * @param instance the persistence entity instance on which to set the value harvested from the request
     * @return whether or not the implementation handled the persistence request
     */
    MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance);

    /**
     * Retrieve the property value from the requestedValue field from the request. Implementations should translate the requestedValue
     * and set on the property parameter. The requestedValue is the field value taken from the JPA managed entity instance.
     * You are taking this field value and converting it into a string representation appropriate for the <tt>property</tt>
     * instance parameter. Used during admin fetch events.
     *
     * @param extractValueRequest contains the requested value and support classes.
     * @param property the property for the admin that will contain the information harvested from the persistence value
     * @return whether or not the implementation handled the persistence request
     */
    MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property);

    /**
     * Add {@link FilterMapping} instances. The FilterMappings are used by the system to refine the fetch criteria
     * used to retrieve lists of records for the admin. The requestedCto contains filters requested from the admin
     * and is generally used to drive the added FilterMapping instances.
     *
     * @param addSearchMappingRequest contains the requested cto and support classes.
     * @param filterMappings filter criteria should be added here. It is used to generate the final search criteria.
     * @return whether or not the implementation handled the persistence request
     */
    MetadataProviderResponse addSearchMapping(AddSearchMappingRequest addSearchMappingRequest, List<FilterMapping> filterMappings);

    /**
     * Filter the list of properties posted by the admin during and add or update. This is the property list
     * immediately before persistence is attempted. Properties may be altered, removed or added.
     *
     * @param addFilterPropertiesRequest contains the <tt>Entity</tt> instance.
     * @param properties the collection of properties to filter
     * @return whether or not the implementation handled the persistence request
     */
    MetadataProviderResponse filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest, Map<String, FieldMetadata> properties);

    /**
     * If the provider should always run, regardless if a previous provider returned a response of HANDLED_BREAK
     *
     * @return if this provider should always run
     */
    boolean alwaysRun();

    /**
     * If the provider should handle populating null values or if it should delegate to the default persistence provider
     * @return
     */
    boolean canHandlePopulateNull();
}
