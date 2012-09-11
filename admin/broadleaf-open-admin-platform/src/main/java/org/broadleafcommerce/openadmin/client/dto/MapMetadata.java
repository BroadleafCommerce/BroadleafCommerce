package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class MapMetadata extends CollectionMetadata {

    private String valueClassName;
    private String[][] keys;
    private boolean isSimpleValue;
    private String mediaField;
    private String mapKeyOptionEntityClass;
    private String mapKeyOptionEntityDisplayField;
    private String mapKeyOptionEntityValueField;

    public String getValueClassName() {
        return valueClassName;
    }

    public void setValueClassName(String valueClassName) {
        this.valueClassName = valueClassName;
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

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        ((MapMetadata) metadata).valueClassName = valueClassName;
        ((MapMetadata) metadata).isSimpleValue = isSimpleValue;
        ((MapMetadata) metadata).mediaField = mediaField;
        ((MapMetadata) metadata).keys = keys;
        ((MapMetadata) metadata).mapKeyOptionEntityClass = mapKeyOptionEntityClass;
        ((MapMetadata) metadata).mapKeyOptionEntityDisplayField = mapKeyOptionEntityDisplayField;
        ((MapMetadata) metadata).mapKeyOptionEntityValueField = mapKeyOptionEntityValueField;

        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        MapMetadata metadata = new MapMetadata();
        return populate(metadata);
    }
}
