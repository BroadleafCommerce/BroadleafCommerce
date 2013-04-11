package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blDefaultMetadataProvider")
@Scope("prototype")
public class DefaultMetadataProvider extends AbstractMetadataProvider {

    private static final Log LOG = LogFactory.getLog(DefaultMetadataProvider.class);

    @Override
    public void addMetadata(Field field, Class<?> parentClass, Class<?> targetClass, Map<String, FieldMetadata> attributes, DynamicEntityDao dynamicEntityDao, String prefix) {
        FieldInfo info = buildFieldInfo(field);
        BasicFieldMetadata metadata = new BasicFieldMetadata();
        metadata.setName(field.getName());
        metadata.setExcluded(false);
        attributes.put(field.getName(), metadata);
        setClassOwnership(parentClass, targetClass, attributes, info);
    }

    @Override
    public boolean canHandleField(Field field) {
        return true;
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
    public void overrideViaAnnotation(Class<?> entity, Map<String, FieldMetadata> mergedProperties, Boolean isParentExcluded, DynamicEntityDao dynamicEntityDao, String prefix) {
        //do nothing
    }

    @Override
    public void overrideViaXml(String configurationKey, String ceilingEntityFullyQualifiedClassname, String prefix,
                               Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties,
                               DynamicEntityDao dynamicEntityDao) {
        //override any and all exclusions derived from xml
        Map<String, FieldMetadataOverride> overrides = getTargetedOverride(configurationKey, ceilingEntityFullyQualifiedClassname);
        if (overrides != null) {
            for (String propertyName : overrides.keySet()) {
                final FieldMetadataOverride localMetadata = overrides.get(propertyName);
                Boolean excluded = localMetadata.getExcluded();
                for (String key : mergedProperties.keySet()) {
                    String testKey = prefix + key;
                    if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded != null && excluded) {
                        FieldMetadata metadata = mergedProperties.get(key);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("setExclusionsBasedOnParents:Excluding " + key + "because an override annotation declared "+ testKey + " to be excluded");
                        }
                        metadata.setExcluded(true);
                        continue;
                    }
                    if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded != null && !excluded) {
                        FieldMetadata metadata = mergedProperties.get(key);
                        if (!isParentExcluded) {
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

    @Override
    public void addMetadataFromMappingData(FieldMetadata presentationAttribute, List<Property> componentProperties, SupportedFieldType type, SupportedFieldType secondaryType, Type entityType, String propertyName, MergedPropertyType mergedPropertyType, DynamicEntityDao dynamicEntityDao) {
        //do nothing
    }
}
