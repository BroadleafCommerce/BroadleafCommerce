package com.broadleafcommerce.customfield.openadmin.provider.metadata;

import com.broadleafcommerce.customfield.domain.CustomField;
import com.broadleafcommerce.customfield.service.CustomFieldInfo;
import com.broadleafcommerce.customfield.service.CustomFieldService;
import com.broadleafcommerce.customfield.service.type.CustomFieldType;
import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.MapFieldsMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blCustomFieldMetadataProvider")
@Scope("prototype")
public class CustomFieldMetadataProvider extends MapFieldsMetadataProvider {

    //ordering constant for custom field - make execute before the regular map field provider
    public static final int CUSTOM_FIELD = 4500;

    @Resource(name="blCustomFieldService")
    protected CustomFieldService customFieldService;

    @Override
    protected boolean canHandleFieldForConfiguredMetadata(AddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        String targetEntityName = addMetadataRequest.getTargetClass().getName();
        Field field = addMetadataRequest.getRequestedField();
        return detectField(targetEntityName, field);
    }

    protected boolean detectField(String targetEntityName, Field field) {
        //see if this is even a legitimate field
        String attributeField = CustomFieldInfo.CUSTOM_FIELD_FIELD_NAMES.get(targetEntityName);
        return (attributeField != null && field.getName().equals(attributeField));
    }

    @Override
    protected boolean canHandleFieldForTypeMetadata(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata) {
        //detect our field, but let the superclass handle metadata alteration
        String targetEntityName = addMetadataFromFieldTypeRequest.getTargetClass().getName();
        Field field = addMetadataFromFieldTypeRequest.getRequestedField();
        return detectField(targetEntityName, field);
    }

    /**
     * Create the metadata dynamically from the database. This ends up being similar to the usage of
     * <tt>AdminPresentationMapField</tt>.
     *
     * @param addMetadataRequest the request to add metadata for a particular field
     * @return whether or not any metadata changes were applied
     */
    @Override
    public boolean addMetadata(AddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleFieldForConfiguredMetadata(addMetadataRequest, metadata)) {
            return false;
        }
        //We will check in the database to see if there are any configured custom fields
        String targetEntityName = addMetadataRequest.getTargetClass().getName();
        List<CustomField> customFields = customFieldService.findByTargetEntityName(targetEntityName);
        if (CollectionUtils.isEmpty(customFields)) {
            return false;
        }
        for (CustomField customField : customFields) {
            FieldMetadataOverride override = new FieldMetadataOverride();
            SupportedFieldType myType;
            if (CustomFieldType.STRING_LIST.getType().equals(customField.getCustomFieldType())) {
                myType = SupportedFieldType.STRING;
            } else {
                myType = SupportedFieldType.valueOf(customField.getCustomFieldType());
            }
            override.setFieldType(myType);
            override.setExplicitFieldType(myType);
            override.setGroup(customField.getGroupName());
            override.setOrder(customField.getFieldOrder()==null?99999:customField.getFieldOrder());
            override.setName(customField.getLabel());
            override.setFriendlyName(customField.getFriendlyName());

            FieldInfo myInfo = new FieldInfo();
            myInfo.setName(addMetadataRequest.getRequestedField().getName() + FieldManager.MAPFIELDSEPARATOR + customField.getLabel());
            buildBasicMetadata(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, myInfo, override, addMetadataRequest.getDynamicEntityDao());
            setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, myInfo);
            BasicFieldMetadata basicFieldMetadata = (BasicFieldMetadata) metadata.get(myInfo.getName());
            basicFieldMetadata.setSearchable(customField.getSearchable());
            basicFieldMetadata.getAdditionalMetadata().put(org.broadleafcommerce.openadmin.web.form.entity.Field.ALTERNATE_ORDERING, true);
        }
        return true;
    }

    @Override
    public boolean overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        //we never want to use annotaton override for this
        return false;
    }

    @Override
    public boolean overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest, Map<String, FieldMetadata> metadata) {
        //we never want to use xml override for this
        return false;
    }

    @Override
    public boolean addMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest, FieldMetadata metadata) {
        //there is no hibernate mapping for this, as we are describing the field "virtually" from metadata stored in the database
        return false;
    }

    @Override
    public int getOrder() {
        return CUSTOM_FIELD;
    }
}
