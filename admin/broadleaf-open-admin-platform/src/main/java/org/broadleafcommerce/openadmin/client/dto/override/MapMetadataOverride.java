package org.broadleafcommerce.openadmin.client.dto.override;

import org.broadleafcommerce.common.presentation.client.UnspecifiedBooleanType;

/**
 * @author Jeff Fischer
 */
public class MapMetadataOverride extends CollectionMetadataOverride {

    private String configurationKey;
    private String keyClass;
    private String keyPropertyFriendlyName;
    private String valueClass;
    private Boolean deleteEntityUponRemove;
    private String valuePropertyFriendlyName;
    private UnspecifiedBooleanType isSimpleValue;
    private String mediaField;
    private String[][] keys;
    private String mapKeyOptionEntityClass;
    private String mapKeyOptionEntityDisplayField;
    private String mapKeyOptionEntityValueField;

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(String configurationKey) {
        this.configurationKey = configurationKey;
    }

    public Boolean isDeleteEntityUponRemove() {
        return deleteEntityUponRemove;
    }

    public void setDeleteEntityUponRemove(Boolean deleteEntityUponRemove) {
        this.deleteEntityUponRemove = deleteEntityUponRemove;
    }

    public UnspecifiedBooleanType getSimpleValue() {
        return isSimpleValue;
    }

    public void setSimpleValue(UnspecifiedBooleanType simpleValue) {
        isSimpleValue = simpleValue;
    }

    public String getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(String keyClass) {
        this.keyClass = keyClass;
    }

    public String getKeyPropertyFriendlyName() {
        return keyPropertyFriendlyName;
    }

    public void setKeyPropertyFriendlyName(String keyPropertyFriendlyName) {
        this.keyPropertyFriendlyName = keyPropertyFriendlyName;
    }

    public String[][] getKeys() {
        return keys;
    }

    public void setKeys(String[][] keys) {
        this.keys = keys;
    }

    public String getMapKeyOptionEntityClass() {
        return mapKeyOptionEntityClass;
    }

    public void setMapKeyOptionEntityClass(String mapKeyOptionEntityClass) {
        this.mapKeyOptionEntityClass = mapKeyOptionEntityClass;
    }

    public String getMapKeyOptionEntityDisplayField() {
        return mapKeyOptionEntityDisplayField;
    }

    public void setMapKeyOptionEntityDisplayField(String mapKeyOptionEntityDisplayField) {
        this.mapKeyOptionEntityDisplayField = mapKeyOptionEntityDisplayField;
    }

    public String getMapKeyOptionEntityValueField() {
        return mapKeyOptionEntityValueField;
    }

    public void setMapKeyOptionEntityValueField(String mapKeyOptionEntityValueField) {
        this.mapKeyOptionEntityValueField = mapKeyOptionEntityValueField;
    }

    public String getMediaField() {
        return mediaField;
    }

    public void setMediaField(String mediaField) {
        this.mediaField = mediaField;
    }

    public String getValueClass() {
        return valueClass;
    }

    public void setValueClass(String valueClass) {
        this.valueClass = valueClass;
    }

    public String getValuePropertyFriendlyName() {
        return valuePropertyFriendlyName;
    }

    public void setValuePropertyFriendlyName(String valuePropertyFriendlyName) {
        this.valuePropertyFriendlyName = valuePropertyFriendlyName;
    }
}
