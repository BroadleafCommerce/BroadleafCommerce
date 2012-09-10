package org.broadleafcommerce.openadmin.client.datasource;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;

/**
 * @author Jeff Fischer
 */
public class LookupMetadata {

    private String parentDataSourceName;
    private String targetDynamicFormDisplayId;
    private ForeignKey lookupForeignKey;
    private String friendlyName;
    private SupportedFieldType fieldType;

    public ForeignKey getLookupForeignKey() {
        return lookupForeignKey;
    }

    public void setLookupForeignKey(ForeignKey lookupForeignKey) {
        this.lookupForeignKey = lookupForeignKey;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getParentDataSourceName() {
        return parentDataSourceName;
    }

    public void setParentDataSourceName(String parentDataSourceName) {
        this.parentDataSourceName = parentDataSourceName;
    }

    public String getTargetDynamicFormDisplayId() {
        return targetDynamicFormDisplayId;
    }

    public void setTargetDynamicFormDisplayId(String targetDynamicFormDisplayId) {
        this.targetDynamicFormDisplayId = targetDynamicFormDisplayId;
    }

    public SupportedFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(SupportedFieldType fieldType) {
        this.fieldType = fieldType;
    }
}
