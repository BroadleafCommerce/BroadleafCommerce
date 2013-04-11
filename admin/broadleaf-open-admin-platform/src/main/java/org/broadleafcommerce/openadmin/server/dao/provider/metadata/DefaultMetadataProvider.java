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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blDefaultMetadataProvider")
@Scope("prototype")
public class DefaultMetadataProvider extends MetadataProviderAdapter {

    private static final Log LOG = LogFactory.getLog(DefaultMetadataProvider.class);

    @Override
    public void addMetadata(AddMetadataRequest addMetadataRequest) {
        FieldInfo info = buildFieldInfo(addMetadataRequest.getRequestedField());
        BasicFieldMetadata metadata = new BasicFieldMetadata();
        metadata.setName(addMetadataRequest.getRequestedField().getName());
        metadata.setExcluded(false);
        addMetadataRequest.getRequestedMetadata().put(addMetadataRequest.getRequestedField().getName(), metadata);
        setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), addMetadataRequest.getRequestedMetadata(), info);
    }

    @Override
    public boolean canHandleField(Field field) {
        return true;
    }

    @Override
    public void overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest) {
        //override any and all exclusions derived from xml
        Map<String, FieldMetadataOverride> overrides = getTargetedOverride(overrideViaXmlRequest.getRequestedConfigKey(), overrideViaXmlRequest.getRequestedCeilingEntity());
        if (overrides != null) {
            for (String propertyName : overrides.keySet()) {
                final FieldMetadataOverride localMetadata = overrides.get(propertyName);
                Boolean excluded = localMetadata.getExcluded();
                for (String key : overrideViaXmlRequest.getRequestedMetadata().keySet()) {
                    String testKey = overrideViaXmlRequest.getPrefix() + key;
                    if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded != null && excluded) {
                        FieldMetadata metadata = overrideViaXmlRequest.getRequestedMetadata().get(key);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("setExclusionsBasedOnParents:Excluding " + key + "because an override annotation declared "+ testKey + " to be excluded");
                        }
                        metadata.setExcluded(true);
                        continue;
                    }
                    if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded != null && !excluded) {
                        FieldMetadata metadata = overrideViaXmlRequest.getRequestedMetadata().get(key);
                        if (!overrideViaXmlRequest.getParentExcluded()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("setExclusionsBasedOnParents:Showing " + key + "because an override annotation declared " + testKey + " to not be excluded");
                            }
                            metadata.setExcluded(false);
                        }
                    }
                }
            }
        }
    }

}
