/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.media.domain.MediaImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.ParentEntityPersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.extension.MediaFieldPersistenceProviderExtensionManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.broadleafcommerce.openadmin.web.service.MediaBuilderService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.OneToMany;

/**
 * @author Brian Polster
 */
@Component("blMediaFieldPersistenceProvider")
@Scope("prototype")
public class MediaFieldPersistenceProvider extends FieldPersistenceProviderAdapter {
    
    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Resource(name = "blMediaFieldPersistenceProviderExtensionManager")
    protected MediaFieldPersistenceProviderExtensionManager extensionManager;

    @Resource(name = "blMediaBuilderService")
    protected MediaBuilderService mediaBuilderService;

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        return populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.MEDIA;
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.MEDIA;
    }

    @Override
    public MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) throws PersistenceException {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        String prop = populateValueRequest.getProperty().getName();
        if (prop.contains(FieldManager.MAPFIELDSEPARATOR)) {
            Field field = populateValueRequest.getFieldManager().getField(instance.getClass(), prop.substring(0, prop.indexOf(FieldManager.MAPFIELDSEPARATOR)));
            if (field.getAnnotation(OneToMany.class) == null) {
                throw new UnsupportedOperationException("MediaFieldPersistenceProvider is currently only compatible with map fields when modelled using @OneToMany");
            }
        }
        MetadataProviderResponse response = MetadataProviderResponse.HANDLED;
        boolean dirty = false;
        try {
            setNonDisplayableValues(populateValueRequest);
            Class<?> valueType = getStartingValueType(populateValueRequest);
        
            if (Media.class.isAssignableFrom(valueType)) {
                Media newMedia = mediaBuilderService.convertJsonToMedia(populateValueRequest
                        .getProperty().getUnHtmlEncodedValue(), valueType);
                boolean persist = false;
                boolean noPrimary = false;
                Media media;
                try {
                    if (extensionManager != null) {
                        ExtensionResultHolder<Media> result = new ExtensionResultHolder<Media>();
                        extensionManager.getProxy().retrieveMedia(instance, populateValueRequest, result);
                        media = result.getResult();
                    } else {
                        media = (Media) populateValueRequest.getFieldManager().getFieldValue(instance,
                                populateValueRequest.getProperty().getName());
                    }
                    if (newMedia == null && media != null) {
                        noPrimary = true;
                        dirty = true;

                        // remove entry in sku to media map
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                            populateValueRequest.getProperty().getName(), null);
                        populateValueRequest.getPersistenceManager().getDynamicEntityDao().remove(media);


                    } else if (media == null) {
                        media = (Media) valueType.newInstance();

                        Object parent = extractParent(populateValueRequest, instance);

                        populateValueRequest.getFieldManager().setFieldValue(media, populateValueRequest.getMetadata().
                                getToOneParentProperty(), parent);
                        populateValueRequest.getFieldManager().setFieldValue(media, populateValueRequest.getMetadata().
                                getMapKeyValueProperty(), prop.substring(prop.indexOf(
                                FieldManager.MAPFIELDSEPARATOR) + FieldManager.MAPFIELDSEPARATOR.length(),
                                prop.length()));
                        persist = true;
                    }
                } catch (FieldNotAvailableException e) {
                    throw new IllegalArgumentException(e);
                }
                populateValueRequest.getProperty().setOriginalValue(convertMediaToJson(media));
                if (!noPrimary) {
                    dirty = establishDirtyState(newMedia, media);
                    updateMedia(populateValueRequest, newMedia, persist, media);
                }
                if (dirty) {
                    updateMedia(populateValueRequest, newMedia, persist, media);
		            response = MetadataProviderResponse.HANDLED_BREAK;
                }
            } else {
                throw new UnsupportedOperationException("MediaFields only work with Media types.");
            }
        } catch (Exception e) {
            throw ExceptionHelper.refineException(PersistenceException.class, PersistenceException.class, e);
        }
        populateValueRequest.getProperty().setIsDirty(dirty);

        return response;
    }

    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }

        if (extractValueRequest.getRequestedValue() != null) {
            Object requestedValue = extractValueRequest.getRequestedValue();
            if (!StringUtils.isEmpty(extractValueRequest.getMetadata().getToOneTargetProperty())) {
                try {
                    requestedValue = extractValueRequest.getFieldManager().getFieldValue(requestedValue, extractValueRequest.getMetadata().getToOneTargetProperty());
                } catch (IllegalAccessException e) {
                    throw ExceptionHelper.refineException(e);
                } catch (FieldNotAvailableException e) {
                    throw ExceptionHelper.refineException(e);
                }
            }
            if (requestedValue instanceof Media) {
                Media media = (Media) requestedValue;
                String jsonString = convertMediaToJson(media);
                if (extensionManager != null) {
                    ExtensionResultHolder<Long> resultHolder = new ExtensionResultHolder<Long>();
                    ExtensionResultStatusType result = extensionManager.getProxy().transformId(media, resultHolder);
                    if (ExtensionResultStatusType.NOT_HANDLED != result && resultHolder.getResult() != null) {
                        Class<?> type;
                        if (media.isUnwrappableAs(Media.class)) {
                            type = media.unwrap(Media.class).getClass();
                        } else {
                            type = media.getClass();
                        }
                        Media converted = mediaBuilderService.convertJsonToMedia(jsonString, type);
                        converted.setId(resultHolder.getResult());
                        jsonString = convertMediaToJson(converted);
                    }
                }
                property.setValue(jsonString);
                property.setUnHtmlEncodedValue(jsonString);
                property.setDisplayValue(extractValueRequest.getDisplayVal());
                return MetadataProviderResponse.HANDLED_BREAK;
            } else {
                throw new UnsupportedOperationException("MEDIA type is currently only supported on fields of type Media");
            }
        }
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest, Map<String, FieldMetadata> properties) {
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
                            Property originalProp = addFilterPropertiesRequest.getEntity().findProperty(originalFM
                                    .getName());
                            if (originalProp == null) {
                                originalProp = new Property();
                                originalProp.setName(originalFM.getName());
                                additionalProperties.add(originalProp);
                            }
                            originalProp.setValue(prop.getValue());
                            originalProp.setRawValue(prop.getRawValue());
                            originalProp.setUnHtmlEncodedValue(prop.getUnHtmlEncodedValue());
                            itr.remove();
                            break;
                        }
                    }
                }
            }
        }
        propertyList.addAll(additionalProperties);
        addFilterPropertiesRequest.getEntity().setProperties(propertyList.toArray(new Property[propertyList.size()]));
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.MEDIA;
    }

    protected void updateMedia(PopulateValueRequest populateValueRequest, Media newMedia, boolean persist,
                               Media media) throws IllegalAccessException, FieldNotAvailableException {
        if (!persist) {
            //pre-merge (can result in a clone for enterprise)
            media = populateValueRequest.getPersistenceManager().getDynamicEntityDao().merge(media);
            if (extensionManager != null) {
                extensionManager.getProxy().postUpdate(media);
            }
        }
        updateMediaFields(media, newMedia);
        if (persist) {
            populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(media);
            if (extensionManager != null) {
                extensionManager.getProxy().postAdd(media);
            }
        }
    }

    protected boolean checkEquality(Object one, Object two) {
        return one == null && two == null || !(one == null || two == null) && one.equals(two);
    }

    protected boolean establishDirtyState(Media newMedia, Media media) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        boolean dirty = (newMedia == null && media != null) || (newMedia != null && media == null);
        if (newMedia == null && media == null) {
            return false;
        }
        if (!dirty) {
            dirty = !checkEquality(newMedia.getAltText(), media.getAltText());
        }
        if (!dirty) {
            dirty = !checkEquality(newMedia.getTags(), media.getTags());
        }
        if (!dirty) {
            dirty = !checkEquality(newMedia.getTitle(), media.getTitle());
        }
        if (!dirty) {
            dirty = !checkEquality(newMedia.getUrl(), media.getUrl());
        }
        if (!dirty && extensionManager != null) {
            ExtensionResultHolder<Boolean> resultHolder = new ExtensionResultHolder<Boolean>();
            extensionManager.getProxy().checkDirtyState(media, newMedia, resultHolder);
            dirty = resultHolder.getResult() != null && resultHolder.getResult();
        }
        return dirty;
    }

    protected Class<?> getStartingValueType(PopulateValueRequest populateValueRequest) throws ClassNotFoundException, IllegalAccessException {
        Class<?> startingValueType = null;
        if (!populateValueRequest.getProperty().getName().contains(FieldManager.MAPFIELDSEPARATOR)) {
            startingValueType = populateValueRequest.getReturnType();
        } else {
            String valueClassName = populateValueRequest.getMetadata().getMapFieldValueClass();
            if (valueClassName != null) {
                startingValueType = Class.forName(valueClassName);
            }
            if (startingValueType == null) {
                startingValueType = populateValueRequest.getReturnType();
            }
        }
        if (startingValueType == null) {
            throw new IllegalAccessException("Unable to determine the valueType for the rule field (" + populateValueRequest.getProperty().getName() + ")");
        } else if (Media.class.equals(startingValueType)) {
            startingValueType = MediaImpl.class;
        }
        return startingValueType;
    }

    protected String convertMediaToJson(Media media) {
        try {
            ObjectMapper om = new ObjectMapper();
            Media unwrapped = media;
            if (media.isUnwrappableAs(Media.class)) {
                unwrapped = media.unwrap(Media.class);
            }
            return om.writeValueAsString(unwrapped);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void updateMediaFields(Media oldMedia, Media newMedia) {
        if (newMedia == null) {
            return;
        }

        oldMedia.setAltText(newMedia.getAltText());
        oldMedia.setTags(newMedia.getTags());
        oldMedia.setTitle(newMedia.getTitle());
        oldMedia.setUrl(newMedia.getUrl());
    }

    protected Object extractParent(PopulateValueRequest populateValueRequest, Serializable instance) throws IllegalAccessException, FieldNotAvailableException {
        Object parent = instance;
        String parentName = populateValueRequest.getProperty().getName();
        if (parentName.contains(".")) {
            parent = populateValueRequest.getFieldManager().getFieldValue(instance,
                    parentName.substring(0, parentName.lastIndexOf(".")));
        }
        if (!populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager().contains(parent)) {
            try {
                populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(parent);
            } catch (Exception e) {
                throw new ParentEntityPersistenceException("Unable to Persist the parent entity during rule builder field population", e);
            }
        }
        return parent;
    }
}
