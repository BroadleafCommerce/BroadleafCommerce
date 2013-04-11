package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationMapField;
import org.broadleafcommerce.common.presentation.AdminPresentationMapFields;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blMapFieldsMetadataProvider")
@Scope("prototype")
public class MapFieldsMetadataProvider extends BasicMetadataProvider {

    private static final Log LOG = LogFactory.getLog(MapFieldsMetadataProvider.class);

    public boolean canHandleField(Field field) {
        AdminPresentationMapFields annot = field.getAnnotation(AdminPresentationMapFields.class);
        return annot != null;
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
    public void addMetadata(Field field, Class<?> parentClass, Class<?> targetClass, Map<String, FieldMetadata> attributes, DynamicEntityDao dynamicEntityDao, String prefix) {
        AdminPresentationMapFields annot = field.getAnnotation(AdminPresentationMapFields.class);
        for (AdminPresentationMapField mapField : annot.mapDisplayFields()) {
            if (mapField.fieldPresentation().fieldType() == SupportedFieldType.UNKNOWN) {
                throw new IllegalArgumentException("fieldType property on AdminPresentation must be set for AdminPresentationMapField");
            }
            FieldMetadataOverride override = constructBasicMetadataOverride(mapField.fieldPresentation(), null, null);
            override.setFriendlyName(mapField.fieldName().friendlyKeyName());
            FieldInfo myInfo = new FieldInfo();
            myInfo.setName(field.getName() + FieldManager.MAPFIELDSEPARATOR + mapField.fieldName().keyName());
            if (!mapField.targetClass().equals(Void.class)) {
                if (mapField.targetClass().isInterface()) {
                    throw new IllegalArgumentException("targetClass on @AdminPresentationMapField must be a concrete class");
                }
                override.setValueClass(mapField.targetClass().getName());
            }
            buildBasicMetadata(parentClass, targetClass, attributes, myInfo, override, dynamicEntityDao);
            setClassOwnership(parentClass, targetClass, attributes, myInfo);
        }
    }

    @Override
    public void overrideViaAnnotation(Class<?> entity, Map<String, FieldMetadata> mergedProperties, Boolean
            isParentExcluded, DynamicEntityDao dynamicEntityDao, String prefix) {
        //TODO support annotation override
        //do nothing
    }

    @Override
    public void overrideViaXml(String configurationKey, String ceilingEntityFullyQualifiedClassname, String prefix, Boolean isParentExcluded,
                               Map<String, FieldMetadata> mergedProperties, DynamicEntityDao dynamicEntityDao) {
        //TODO support xml override
        //do nothing
    }
}
