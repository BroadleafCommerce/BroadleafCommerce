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

package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.broadleafcommerce.openadmin.server.dao.provider.property.request.PropertyRequest;

import java.lang.reflect.Field;

/**
 * Classes implementing this interface are capable of manipulating properties resulting from the inspection
 * phase for the admin. Providers are typically added in response to new admin presentation annotation support.
 * Implementers should generally extend <tt>PropertyProviderAdapter</tt>.
 *
 * @author Jeff Fischer
 */
public interface PropertyProvider {

    /**
     * Whether or not this provider is qualified add properties for the specified field.
     *
     * @param field the <tt>Field</tt> instance to test
     * @return whether or not this provider is qualified
     */
    boolean canHandleField(Field field);

    /**
     * Contribute to property inspection for the <tt>Field</tt> instance in the request. Implementations should
     * add values to the requestedProperties field of the request object.
     *
     * @param propertyRequest contains the requested field, properties, property name and support classes.
     */
    void buildProperty(PropertyRequest propertyRequest);
}
