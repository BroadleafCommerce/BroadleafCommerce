package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public interface MetadataProvider {

    boolean canHandleField(Field field);

    boolean canHandleAnnotationOverride(Class<?> clazz);

    boolean canHandleXmlOverride(String ceilingEntityFullyQualifiedClassname, String configurationKey);

    void addMetadata(Field field, Class<?> parentClass, Class<?> targetClass, Map<String,
                    FieldMetadata> attributes, DynamicEntityDao dynamicEntityDao, String prefix);

    void overrideViaAnnotation(Class<?> entity, Map<String, FieldMetadata> mergedProperties, Boolean isParentExcluded,
                               DynamicEntityDao dynamicEntityDao, String prefix);

    void overrideViaXml(String configurationKey, String ceilingEntityFullyQualifiedClassname, String prefix, Boolean isParentExcluded,
                        Map<String, FieldMetadata> mergedProperties, DynamicEntityDao dynamicEntityDao);

    void addMetadataFromMappingData(FieldMetadata presentationAttribute, List<Property> componentProperties, SupportedFieldType type,
                                               SupportedFieldType secondaryType, Type entityType, String propertyName,
                                               MergedPropertyType mergedPropertyType, DynamicEntityDao dynamicEntityDao);
}
