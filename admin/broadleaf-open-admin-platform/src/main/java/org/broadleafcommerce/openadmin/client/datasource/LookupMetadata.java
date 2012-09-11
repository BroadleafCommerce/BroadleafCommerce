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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LookupMetadata)) return false;

        LookupMetadata that = (LookupMetadata) o;

        if (fieldType != that.fieldType) return false;
        if (friendlyName != null ? !friendlyName.equals(that.friendlyName) : that.friendlyName != null) return false;
        if (lookupForeignKey != null ? !lookupForeignKey.equals(that.lookupForeignKey) : that.lookupForeignKey != null)
            return false;
        if (parentDataSourceName != null ? !parentDataSourceName.equals(that.parentDataSourceName) : that.parentDataSourceName != null)
            return false;
        if (targetDynamicFormDisplayId != null ? !targetDynamicFormDisplayId.equals(that.targetDynamicFormDisplayId) : that.targetDynamicFormDisplayId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parentDataSourceName != null ? parentDataSourceName.hashCode() : 0;
        result = 31 * result + (targetDynamicFormDisplayId != null ? targetDynamicFormDisplayId.hashCode() : 0);
        result = 31 * result + (lookupForeignKey != null ? lookupForeignKey.hashCode() : 0);
        result = 31 * result + (friendlyName != null ? friendlyName.hashCode() : 0);
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        return result;
    }
}
