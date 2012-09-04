package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class MapMetadata extends CollectionMetadata {

    private String keyClassName;
    private String keyPropertyFriendlyName;
    private String valueClassName;
    private boolean deleteEntityUponRemove;
    private String valuePropertyFriendlyName;
    private String[][] keys;

    private String keyPropertyName;
    private String valuePropertyName;
    private String parentObjectClass;
    private String parentObjectIdField;
    private boolean isSimpleValue;
    private String mediaField;

    public boolean isDeleteEntityUponRemove() {
        return deleteEntityUponRemove;
    }

    public void setDeleteEntityUponRemove(boolean deleteEntityUponRemove) {
        this.deleteEntityUponRemove = deleteEntityUponRemove;
    }

    public String getKeyClassName() {
        return keyClassName;
    }

    public void setKeyClassName(String keyClassName) {
        this.keyClassName = keyClassName;
    }

    public String getKeyPropertyFriendlyName() {
        return keyPropertyFriendlyName;
    }

    public void setKeyPropertyFriendlyName(String keyPropertyFriendlyName) {
        this.keyPropertyFriendlyName = keyPropertyFriendlyName;
    }

    public String getValueClassName() {
        return valueClassName;
    }

    public void setValueClassName(String valueClassName) {
        this.valueClassName = valueClassName;
    }

    public String getValuePropertyFriendlyName() {
        return valuePropertyFriendlyName;
    }

    public void setValuePropertyFriendlyName(String valuePropertyFriendlyName) {
        this.valuePropertyFriendlyName = valuePropertyFriendlyName;
    }

    public String getKeyPropertyName() {
        return keyPropertyName;
    }

    public void setKeyPropertyName(String keyPropertyName) {
        this.keyPropertyName = keyPropertyName;
    }

    public String getParentObjectClass() {
        return parentObjectClass;
    }

    public void setParentObjectClass(String parentObjectClass) {
        this.parentObjectClass = parentObjectClass;
    }

    public String getParentObjectIdField() {
        return parentObjectIdField;
    }

    public void setParentObjectIdField(String parentObjectIdField) {
        this.parentObjectIdField = parentObjectIdField;
    }

    public String getValuePropertyName() {
        return valuePropertyName;
    }

    public void setValuePropertyName(String valuePropertyName) {
        this.valuePropertyName = valuePropertyName;
    }

    public boolean isSimpleValue() {
        return isSimpleValue;
    }

    public void setSimpleValue(boolean simpleValue) {
        isSimpleValue = simpleValue;
    }

    public String getMediaField() {
        return mediaField;
    }

    public void setMediaField(String mediaField) {
        this.mediaField = mediaField;
    }

    public String[][] getKeys() {
        return keys;
    }

    public void setKeys(String[][] keys) {
        this.keys = keys;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        ((MapMetadata) metadata).keyClassName = keyClassName;
        ((MapMetadata) metadata).keyPropertyFriendlyName = keyPropertyFriendlyName;
        ((MapMetadata) metadata).valueClassName = valueClassName;
        ((MapMetadata) metadata).deleteEntityUponRemove = deleteEntityUponRemove;
        ((MapMetadata) metadata).valuePropertyFriendlyName = valuePropertyFriendlyName;
        ((MapMetadata) metadata).keyPropertyName = keyPropertyName;
        ((MapMetadata) metadata).valuePropertyName = valuePropertyName;
        ((MapMetadata) metadata).parentObjectClass = parentObjectClass;
        ((MapMetadata) metadata).parentObjectIdField = parentObjectIdField;
        ((MapMetadata) metadata).isSimpleValue = isSimpleValue;
        ((MapMetadata) metadata).mediaField = mediaField;
        ((MapMetadata) metadata).keys = keys;

        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        MapMetadata metadata = new MapMetadata();
        return populate(metadata);
    }
}
