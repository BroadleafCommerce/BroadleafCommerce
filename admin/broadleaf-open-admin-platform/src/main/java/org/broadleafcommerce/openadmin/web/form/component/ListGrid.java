
package org.broadleafcommerce.openadmin.web.form.component;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.openadmin.web.form.entity.Field;

import java.util.ArrayList;
import java.util.List;

public class ListGrid {

    protected String className;
    protected String friendlyName = null;
    protected List<Field> headerFields = new ArrayList<Field>();
    protected List<ListGridRecord> records = new ArrayList<ListGridRecord>();
    protected int startIndex = 0;
    
    protected AddMethodType addMethodType;
    protected String listGridType;
    
    // The section url that maps to this particular list grid
    protected String sectionKey;

    // Whether or not individual rows inside this list grid can be updated.
    protected boolean editable = false;

    // If this list grid is a sublistgrid, meaning it is rendered as part of a different entity, these properties
    // help identify the parent entity.
    protected String externalEntitySectionKey;
    protected String containingEntityId;
    protected String subCollectionFieldName;

    public enum Type {
        MAIN,
        INLINE,
        TO_ONE,
        BASIC,
        ADORNED,
        ADORNED_WITH_FORM,
        MAP
    }
    
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        
        if (!getSectionKey().startsWith("/")) {
            sb.append("/");
        }
        
        sb.append(getSectionKey());
        if (getContainingEntityId() != null && StringUtils.isNotBlank(getSubCollectionFieldName())) {
            sb.append("/").append(getContainingEntityId()).append("/").append(getSubCollectionFieldName());
        }
        return sb.toString();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Field> getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(List<Field> headerFields) {
        this.headerFields = headerFields;
    }

    public List<ListGridRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ListGridRecord> records) {
        this.records = records;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public AddMethodType getAddMethodType() {
        return addMethodType;
    }

    public void setAddMethodType(AddMethodType addMethodType) {
        this.addMethodType = addMethodType;
    }

    public String getListGridType() {
        return listGridType;
    }

    public void setListGridType(Type listGridType) {
        this.listGridType = listGridType.toString().toLowerCase();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getContainingEntityId() {
        return containingEntityId;
    }

    public void setContainingEntityId(String containingEntityId) {
        this.containingEntityId = containingEntityId;
    }

    public String getSubCollectionFieldName() {
        return subCollectionFieldName;
    }

    public void setSubCollectionFieldName(String subCollectionFieldName) {
        this.subCollectionFieldName = subCollectionFieldName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getSectionKey() {
        return sectionKey;
    }
    
    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }
    
    public String getExternalEntitySectionKey() {
        return externalEntitySectionKey;
    }

    public void setExternalEntitySectionKey(String externalEntitySectionKey) {
        this.externalEntitySectionKey = externalEntitySectionKey;
    }
    
}

