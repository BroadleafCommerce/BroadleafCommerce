package com.broadleafcommerce.customfield.openadmin.provider.metadata;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.MapFieldsFieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.LateStageAddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.broadleafcommerce.customfield.domain.CustomField;
import com.broadleafcommerce.customfield.service.CustomFieldInfo;
import com.broadleafcommerce.customfield.service.CustomFieldService;
import com.broadleafcommerce.customfield.service.type.CustomFieldType;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component("blCustomFieldMetadataProvider")
@Scope("prototype")
public class CustomFieldFieldMetadataProvider extends MapFieldsFieldMetadataProvider {

    //ordering constant for custom field - make execute before the regular map field provider
    public static final int CUSTOM_FIELD = 45000;

    @Resource(name="blCustomFieldService")
    protected CustomFieldService customFieldService;

    protected boolean detectField(String targetEntityName, String fieldName) {
        //see if this is even a legitimate field
        String attributeField = CustomFieldInfo.CUSTOM_FIELD_FIELD_NAMES.get(targetEntityName);
        return (attributeField != null && fieldName.equals(attributeField));
    }

    protected boolean canHandleLateStageField(LateStageAddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        //detect our field, but let the superclass handle metadata alteration
        String fieldName = addMetadataRequest.getFieldName();
        FieldMetadata fmd = metadata.get(fieldName);
        
        String targetEntityName = fmd.getTargetClass();
        if (fieldName.contains(".")) {
            fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
        }
        
        return detectField(targetEntityName, fieldName);
    }
    
    @Override
    public FieldProviderResponse addMetadata(AddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        return FieldProviderResponse.NOT_HANDLED;
    }

    /**
     * Create the metadata dynamically from the database. This ends up being similar to the usage of
     * <tt>AdminPresentationMapField</tt>.
     *
     * @param addMetadataRequest the request to add metadata for a particular field
     * @return whether or not any metadata changes were applied
     */
    @Override
    public FieldProviderResponse lateStageAddMetadata(LateStageAddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleLateStageField(addMetadataRequest, metadata)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        //We will check in the database to see if there are any configured custom fields
        FieldMetadata fmd = metadata.get(addMetadataRequest.getFieldName());
        String targetEntityName = fmd.getTargetClass();
        
        List<CustomField> customFields = customFieldService.findByTargetEntityName(targetEntityName);
        if (CollectionUtils.isEmpty(customFields)) {
            return FieldProviderResponse.NOT_HANDLED;
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
            override.setName(customField.getAttributeName());
            override.setFriendlyName(customField.getFriendlyName());

            FieldInfo myInfo = new FieldInfo();
            myInfo.setName(addMetadataRequest.getFieldName() + FieldManager.MAPFIELDSEPARATOR + customField.getAttributeName());
            buildBasicMetadata(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, myInfo, override, addMetadataRequest.getDynamicEntityDao());
            setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, myInfo);
            BasicFieldMetadata basicFieldMetadata = (BasicFieldMetadata) metadata.get(myInfo.getName());
            basicFieldMetadata.setSearchable(customField.getSearchable());
            basicFieldMetadata.setInheritedFromType(addMetadataRequest.getTargetClass().getName());
            basicFieldMetadata.getAdditionalMetadata().put(org.broadleafcommerce.openadmin.web.form.entity.Field.ALTERNATE_ORDERING, true);
        }
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        //we never want to use annotaton override for this
        return FieldProviderResponse.NOT_HANDLED;
    }

    @Override
    public FieldProviderResponse overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest, Map<String, FieldMetadata> metadata) {
        //we never want to use xml override for this
        return FieldProviderResponse.NOT_HANDLED;
    }

    @Override
    public FieldProviderResponse addMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest, FieldMetadata metadata) {
        //there is no hibernate mapping for this, as we are describing the field "virtually" from metadata stored in the database
        return FieldProviderResponse.NOT_HANDLED;
    }

    @Override
    public int getOrder() {
        return CUSTOM_FIELD;
    }
}
