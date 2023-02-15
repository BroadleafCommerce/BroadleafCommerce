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
package org.broadleafcommerce.openadmin.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.TabMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.BasicEntityMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.DefaultFieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component("blMetadata")
@Scope("prototype")
public class Metadata {

    private static final Log LOG = LogFactory.getLog(Metadata.class);

    @Resource(name="blFieldMetadataProviders")
    protected List<FieldMetadataProvider> fieldMetadataProviders = new ArrayList<>();

    @Resource(name= "blDefaultFieldMetadataProvider")
    protected FieldMetadataProvider defaultFieldMetadataProvider;

    @Resource(name= "blBasicEntityMetadataProvider")
    protected BasicEntityMetadataProvider basicEntityMetadataProvider;

    public Map<String, FieldMetadata> getFieldMetadataForTargetClass(Class<?> parentClass, Class<?> targetClass, DynamicEntityDao dynamicEntityDao, String prefix) {
        Map<String, FieldMetadata> metadata = new HashMap<>();
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
                defaultFieldMetadataProvider.addMetadata(new AddFieldMetadataRequest(field, parentClass, targetClass,
                        dynamicEntityDao, prefix), metadata);
            }
        }
        return metadata;
    }

    public Map<String, TabMetadata> getBaseTabAndGroupMetadata(Class<?>[] entities) {
        Map<String, TabMetadata> baseTabAndGroupMetadata = new HashMap<>();

        // Go in reverse order since we want the lowest subclass to come last to guarantee that it takes effect
        for (int i = entities.length-1;i >= 0; i--) {
            basicEntityMetadataProvider.addTabAndGroupMetadata(new AddMetadataRequest(null, entities[i], null, ""),
                baseTabAndGroupMetadata);
        }

        return baseTabAndGroupMetadata;
    }

    public void applyTabAndGroupMetadataOverrides(Class<?>[] entities, Map<String, TabMetadata> mergedTabAndGroupMetadata) {
        // Go in reverse order since we want the lowest subclass to come last to guarantee that it takes effect
        for (int i = entities.length-1;i >= 0; i--) {
            basicEntityMetadataProvider.overrideMetadataViaAnnotation(new OverrideViaAnnotationRequest(entities[i], true, null, ""),
                    mergedTabAndGroupMetadata);
            basicEntityMetadataProvider.overrideMetadataViaXml(new OverrideViaXmlRequest("", entities[i].getCanonicalName(), "", true, null),
                    mergedTabAndGroupMetadata);
        }
    }

    public void buildAdditionalTabAndGroupMetadataFromCmdProperties(ClassMetadata cmd, Map<String, TabMetadata> metadata) {
        basicEntityMetadataProvider.addTabAndGroupMetadataFromCmdProperties(cmd, metadata);
    }

    public Map<String, FieldMetadata> overrideMetadata(Class<?>[] entities, PropertyBuilder propertyBuilder, String prefix, Boolean isParentExcluded, String ceilingEntityFullyQualifiedClassname, String configurationKey, DynamicEntityDao dynamicEntityDao) {
        Boolean classAnnotatedPopulateManyToOneFields = null;
        //go in reverse order since I want the lowest subclass override to come last to guarantee that it takes effect
        for (int i = entities.length-1;i >= 0; i--) {
            AdminPresentationClass adminPresentationClass = AnnotationUtils.findAnnotation(entities[i], AdminPresentationClass.class);
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
