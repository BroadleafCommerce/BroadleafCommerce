package org.broadleafcommerce.openadmin.client.dto.override;

import org.broadleafcommerce.common.presentation.client.AddMethodType;

/**
 * @author Jeff Fischer
 */
public class BasicCollectionMetadataOverride extends CollectionMetadataOverride {

    private String configurationKey;
    private AddMethodType addMethodType;
    private String manyToField;

    public AddMethodType getAddMethodType() {
        return addMethodType;
    }

    public void setAddMethodType(AddMethodType addMethodType) {
        this.addMethodType = addMethodType;
    }

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(String configurationKey) {
        this.configurationKey = configurationKey;
    }

    public String getManyToField() {
        return manyToField;
    }

    public void setManyToField(String manyToField) {
        this.manyToField = manyToField;
    }
}
