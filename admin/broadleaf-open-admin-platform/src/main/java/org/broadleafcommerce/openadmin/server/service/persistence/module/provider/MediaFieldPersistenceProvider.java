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

import org.apache.commons.lang.StringEscapeUtils;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.media.domain.MediaImpl;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Polster
 */
@Component("blMediaFieldPersistenceProvider")
@Scope("prototype")
public class MediaFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        return populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.MEDIA;
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.MEDIA;
    }

    public FieldProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) throws PersistenceException {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        
        try {
            Class<?> valueType = null;
            if (!populateValueRequest.getProperty().getName().contains(FieldManager.MAPFIELDSEPARATOR)) {
                valueType = populateValueRequest.getReturnType();
            } else {
                String valueClassName = populateValueRequest.getMetadata().getMapFieldValueClass();
                if (valueClassName != null) {
                    valueType = Class.forName(valueClassName);
                }
                if (valueType == null) {
                    valueType = populateValueRequest.getReturnType();
                }
            }

            if (valueType == null) {
                throw new IllegalAccessException("Unable to determine the valueType for the rule field (" + populateValueRequest.getProperty().getName() + ")");
            }
        
            if (Media.class.isAssignableFrom(valueType)) {
                Media newMedia = convertJsonToMedia(populateValueRequest.getRequestedValue());
                Media media;
                try {
                    media = (Media) populateValueRequest.getFieldManager().getFieldValue(instance,
                            populateValueRequest.getProperty().getName());
                } catch (FieldNotAvailableException e) {
                    throw new IllegalArgumentException(e);
                }

                if (media == null) {
                    media = (Media) valueType.newInstance();
                }

                updateMediaFields(media, newMedia);
                populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(media);
                populateValueRequest.getFieldManager().setFieldValue(instance,
                        populateValueRequest.getProperty().getName(), media);
            } else {
                throw new UnsupportedOperationException("MediaFields only work with Media types.");
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }

        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return FieldProviderResponse.NOT_HANDLED;
        }

        if (extractValueRequest.getRequestedValue() != null) {
            if (extractValueRequest.getRequestedValue() instanceof Media) {
                Media media = (Media) extractValueRequest.getRequestedValue();
                String jsonString = convertMediaToJson(media);
                property.setValue(jsonString);
                property.setDisplayValue(extractValueRequest.getDisplayVal());
            } else {
                throw new UnsupportedOperationException("MEDIA type is currently only supported on fields of type Media");
            }
        }
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest, Map<String, FieldMetadata> properties) {
        // BP:  Basically copied this from RuleFieldPersistenceProvider
        List<Property> propertyList = new ArrayList<Property>();
        propertyList.addAll(Arrays.asList(addFilterPropertiesRequest.getEntity().getProperties()));
        Iterator<Property> itr = propertyList.iterator();
        List<Property> additionalProperties = new ArrayList<Property>();
        while(itr.hasNext()) {
            Property prop = itr.next();
            if (prop.getName().endsWith("Json")) {
                for (Map.Entry<String, FieldMetadata> entry : properties.entrySet()) {
                    if (prop.getName().startsWith(entry.getKey())) {
                        BasicFieldMetadata originalFM = (BasicFieldMetadata) entry.getValue();
                        if (originalFM.getFieldType() == SupportedFieldType.MEDIA) {
                            Property orginalProp = addFilterPropertiesRequest.getEntity().findProperty(originalFM
                                    .getName());
                            if (orginalProp == null) {
                                orginalProp = new Property();
                                orginalProp.setName(originalFM.getName());
                                additionalProperties.add(orginalProp);
                            }
                            orginalProp.setValue(prop.getValue());
                            itr.remove();
                            break;
                        }
                    }
                }
            }
        }
        propertyList.addAll(additionalProperties);
        addFilterPropertiesRequest.getEntity().setProperties(propertyList.toArray(new Property[propertyList.size()]));
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.MEDIA;
    }

    protected String convertMediaToJson(Media media) {
        String json;
        try {
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(media);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected Media convertJsonToMedia(String jsonProp) {
        try {
            ObjectMapper om = new ObjectMapper();
            jsonProp = StringEscapeUtils.unescapeHtml(jsonProp);
            return om.readValue(jsonProp, MediaImpl.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void updateMediaFields(Media oldMedia, Media newMedia) {
        oldMedia.setAltText(newMedia.getAltText());
        oldMedia.setTags(newMedia.getTags());
        oldMedia.setTitle(newMedia.getTitle());
        oldMedia.setUrl(newMedia.getUrl());
    }
}
