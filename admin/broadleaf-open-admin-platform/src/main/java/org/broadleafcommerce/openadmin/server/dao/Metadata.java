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
package org.broadleafcommerce.openadmin.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.DefaultFieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blMetadata")
@Scope("prototype")
public class Metadata {

    private static final Log LOG = LogFactory.getLog(Metadata.class);

    @Resource(name="blMetadataProviders")
    protected List<FieldMetadataProvider> fieldMetadataProviders = new ArrayList<FieldMetadataProvider>();

    @Resource(name= "blDefaultFieldMetadataProvider")
    protected FieldMetadataProvider defaultFieldMetadataProvider;

    public Map<String, FieldMetadata> getFieldPresentationAttributes(Class<?> parentClass, Class<?> targetClass, DynamicEntityDao dynamicEntityDao, String prefix) {
        Map<String, FieldMetadata> attributes = new HashMap<String, FieldMetadata>();
        Field[] fields = dynamicEntityDao.getAllFields(targetClass);
        for (Field field : fields) {
            boolean foundOneOrMoreHandlers = false;
            for (FieldMetadataProvider fieldMetadataProvider : fieldMetadataProviders) {
                MetadataProviderResponse response = fieldMetadataProvider.addMetadata(new AddFieldMetadataRequest(field, parentClass, targetClass,
                        dynamicEntityDao, prefix), metadata);
                if (MetadataProviderResponse.NOT_HANDLED != response) {
                    foundOneOrMoreHandlers = true;
                }
                if (MetadataProviderResponse.HANDLED_BREAK == response) {
                    break;
                }
            }
            if (!foundOneOrMoreHandlers) {
                defaultFieldMetadataProvider.addMetadata(new AddMetadataRequest(field, parentClass, targetClass,
                        dynamicEntityDao, prefix), attributes);
            }
        }
        return attributes;
    }

    public Map<String, FieldMetadata> overrideMetadata(Class<?>[] entities, PropertyBuilder propertyBuilder, String prefix, Boolean isParentExcluded, String ceilingEntityFullyQualifiedClassname, String configurationKey, DynamicEntityDao dynamicEntityDao) {
        Boolean classAnnotatedPopulateManyToOneFields = null;
        //go in reverse order since I want the lowest subclass override to come last to guarantee that it takes effect
        for (int i = entities.length-1;i >= 0; i--) {
            AdminPresentationClass adminPresentationClass = entities[i].getAnnotation(AdminPresentationClass.class);
            if (adminPresentationClass != null && adminPresentationClass.populateToOneFields() != PopulateToOneFieldsEnum.NOT_SPECIFIED) {
                classAnnotatedPopulateManyToOneFields = adminPresentationClass.populateToOneFields()==PopulateToOneFieldsEnum.TRUE;
                break;
            }
        }

        Map<String, FieldMetadata> mergedProperties = propertyBuilder.execute(classAnnotatedPopulateManyToOneFields);
        for (int i = entities.length-1;i >= 0; i--) {
            boolean handled = false;
            for (FieldMetadataProvider fieldMetadataProvider : fieldMetadataProviders) {
                MetadataProviderResponse response = fieldMetadataProvider.overrideViaAnnotation(new OverrideViaAnnotationRequest(entities[i],
                            isParentExcluded, dynamicEntityDao, prefix), mergedProperties);
                if (MetadataProviderResponse.NOT_HANDLED != response) {
                    handled = true;
                }
                if (MetadataProviderResponse.HANDLED_BREAK == response) {
                    break;
                }
            }
            if (!handled) {
                defaultFieldMetadataProvider.overrideViaAnnotation(new OverrideViaAnnotationRequest(entities[i],
                                         isParentExcluded, dynamicEntityDao, prefix), mergedProperties);
            }
        }
        ((DefaultFieldMetadataProvider) defaultFieldMetadataProvider).overrideExclusionsFromXml(new OverrideViaXmlRequest(configurationKey,
                ceilingEntityFullyQualifiedClassname, prefix, isParentExcluded, dynamicEntityDao), mergedProperties);

        boolean handled = false;
        for (FieldMetadataProvider fieldMetadataProvider : fieldMetadataProviders) {
            MetadataProviderResponse response = fieldMetadataProvider.overrideViaXml(
                    new OverrideViaXmlRequest(configurationKey, ceilingEntityFullyQualifiedClassname, prefix,
                            isParentExcluded, dynamicEntityDao), mergedProperties);
            if (MetadataProviderResponse.NOT_HANDLED != response) {
                handled = true;
            }
            if (MetadataProviderResponse.HANDLED_BREAK == response) {
                break;
            }
        }
        if (!handled) {
            defaultFieldMetadataProvider.overrideViaXml(
                                new OverrideViaXmlRequest(configurationKey, ceilingEntityFullyQualifiedClassname, prefix,
                                        isParentExcluded, dynamicEntityDao), mergedProperties);
        }

        return mergedProperties;
    }

    public FieldMetadata getFieldMetadata(
        String prefix,
        String propertyName,
        List<Property> componentProperties,
        SupportedFieldType type,
        Type entityType,
        Class<?> targetClass,
        FieldMetadata presentationAttribute,
        MergedPropertyType mergedPropertyType,
        DynamicEntityDao dynamicEntityDao
    ) {
        return getFieldMetadata(prefix, propertyName, componentProperties, type, null, entityType, targetClass, presentationAttribute, mergedPropertyType, dynamicEntityDao);
    }

    public FieldMetadata getFieldMetadata(
        String prefix,
        final String propertyName,
        final List<Property> componentProperties,
        final SupportedFieldType type,
        final SupportedFieldType secondaryType,
        final Type entityType,
        Class<?> targetClass,
        final FieldMetadata presentationAttribute,
        final MergedPropertyType mergedPropertyType,
        final DynamicEntityDao dynamicEntityDao
    ) {
        if (presentationAttribute.getTargetClass() == null) {
            presentationAttribute.setTargetClass(targetClass.getName());
            presentationAttribute.setFieldName(propertyName);
        }
        presentationAttribute.setInheritedFromType(targetClass.getName());
        presentationAttribute.setAvailableToTypes(new String[]{targetClass.getName()});
        boolean handled = false;
        for (FieldMetadataProvider fieldMetadataProvider : fieldMetadataProviders) {
            MetadataProviderResponse response = fieldMetadataProvider.addMetadataFromMappingData(new AddMetadataFromMappingDataRequest(
                componentProperties, type, secondaryType, entityType, propertyName, mergedPropertyType, dynamicEntityDao), presentationAttribute);
            if (MetadataProviderResponse.NOT_HANDLED != response) {
                handled = true;
            }
            if (MetadataProviderResponse.HANDLED_BREAK == response) {
                break;
            }
        }
        if (!handled) {
            defaultFieldMetadataProvider.addMetadataFromMappingData(new AddMetadataFromMappingDataRequest(
                    componentProperties, type, secondaryType, entityType, propertyName, mergedPropertyType, dynamicEntityDao), presentationAttribute);
        }

        return presentationAttribute;
    }

    public FieldMetadataProvider getDefaultFieldMetadataProvider() {
        return defaultFieldMetadataProvider;
    }

    public void setDefaultFieldMetadataProvider(FieldMetadataProvider defaultFieldMetadataProvider) {
        this.defaultFieldMetadataProvider = defaultFieldMetadataProvider;
    }

    public List<FieldMetadataProvider> getFieldMetadataProviders() {
        return fieldMetadataProviders;
    }

    public void setFieldMetadataProviders(List<FieldMetadataProvider> fieldMetadataProviders) {
        this.fieldMetadataProviders = fieldMetadataProviders;
    }
}
