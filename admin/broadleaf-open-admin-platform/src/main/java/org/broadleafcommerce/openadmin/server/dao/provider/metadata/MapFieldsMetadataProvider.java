package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationMapField;
import org.broadleafcommerce.common.presentation.AdminPresentationMapFields;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author Jeff Fischer
 */
@Component("blMapFieldsMetadataProvider")
@Scope("prototype")
public class MapFieldsMetadataProvider extends BasicMetadataProvider {

    private static final Log LOG = LogFactory.getLog(MapFieldsMetadataProvider.class);

    @Override
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
    public void addMetadata(AddMetadataRequest addMetadataRequest) {
        AdminPresentationMapFields annot = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationMapFields.class);
        for (AdminPresentationMapField mapField : annot.mapDisplayFields()) {
            if (mapField.fieldPresentation().fieldType() == SupportedFieldType.UNKNOWN) {
                throw new IllegalArgumentException("fieldType property on AdminPresentation must be set for AdminPresentationMapField");
            }
            FieldMetadataOverride override = constructBasicMetadataOverride(mapField.fieldPresentation(), null, null);
            override.setFriendlyName(mapField.fieldName().friendlyKeyName());
            FieldInfo myInfo = new FieldInfo();
            myInfo.setName(addMetadataRequest.getRequestedField().getName() + FieldManager.MAPFIELDSEPARATOR + mapField.fieldName().keyName());
            if (!mapField.targetClass().equals(Void.class)) {
                if (mapField.targetClass().isInterface()) {
                    throw new IllegalArgumentException("targetClass on @AdminPresentationMapField must be a concrete class");
                }
                override.setValueClass(mapField.targetClass().getName());
            }
            buildBasicMetadata(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), addMetadataRequest.getRequestedMetadata(), myInfo, override, addMetadataRequest.getDynamicEntityDao());
            setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), addMetadataRequest.getRequestedMetadata(), myInfo);
        }
    }

    @Override
    public void overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest) {
        //TODO support annotation override
        //do nothing
    }

    @Override
    public void overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest) {
        //TODO support xml override
        //do nothing
    }
}
