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
package org.broadleafcommerce.common.jmx;

import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.springframework.jmx.export.metadata.ManagedResource;

/**
 * 
 * @author jfischer
 *
 */
public class AnnotationJmxAttributeSource extends org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource {
    
    private final String appName;
    
    public AnnotationJmxAttributeSource(String appName) {
        this.appName = appName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ManagedResource getManagedResource(Class beanClass) throws InvalidMetadataException {
        ManagedResource resource = super.getManagedResource(beanClass);
        if (resource != null && appName != null) {
            String objectName = resource.getObjectName();
            objectName += "." + appName;
            resource.setObjectName(objectName);
        }
        return resource;
    }
    
}
