package org.broadleafcommerce.openadmin.client.dto.override;

import org.broadleafcommerce.common.presentation.client.OperationType;

/**
 * @author Jeff Fischer
 */
public class CollectionMetadataOverride extends FieldMetadataOverride {

    private String targetElementId;
    private String dataSourceName;
    private Boolean mutable;
    private String[] customCriteria;
    private OperationType addType;
    private OperationType removeType;
    private OperationType updateType;
    private OperationType fetchType;
    private OperationType inspectType;

    public String getTargetElementId() {
        return targetElementId;
    }

    public void setTargetElementId(String targetElementId) {
        this.targetElementId = targetElementId;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Boolean isMutable() {
        return mutable;
    }

    public void setMutable(Boolean mutable) {
        this.mutable = mutable;
    }

    public String[] getCustomCriteria() {
        return customCriteria;
    }

    public void setCustomCriteria(String[] customCriteria) {
        this.customCriteria = customCriteria;
    }

    public OperationType getAddType() {
        return addType;
    }

    public void setAddType(OperationType addType) {
        this.addType = addType;
    }

    public OperationType getFetchType() {
        return fetchType;
    }

    public void setFetchType(OperationType fetchType) {
        this.fetchType = fetchType;
    }

    public OperationType getInspectType() {
        return inspectType;
    }

    public void setInspectType(OperationType inspectType) {
        this.inspectType = inspectType;
    }

    public OperationType getRemoveType() {
        return removeType;
    }

    public void setRemoveType(OperationType removeType) {
        this.removeType = removeType;
    }

    public OperationType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(OperationType updateType) {
        this.updateType = updateType;
    }
}
