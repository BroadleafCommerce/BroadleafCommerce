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

package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;

import java.lang.reflect.Field;

/**
 * @author Jeff Fischer
 */
public class MetadataProviderAdapter extends AbstractMetadataProvider {

    @Override
    public void addMetadata(AddMetadataRequest addMetadataRequest) {
        //do nothing
    }

    @Override
    public boolean canHandleField(Field field) {
        return false;
    }

    @Override
    public boolean canHandleAnnotationOverride(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean canHandleXmlOverride(String ceilingEntityFullyQualifiedClassname, String configurationKey) {
        return false;
    }

    @Override
    public void overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest) {
        //do nothing
    }

    @Override
    public void overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest) {
        //do nothing
    }

    @Override
    public void addMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest) {
        //do nothing
    }
}
