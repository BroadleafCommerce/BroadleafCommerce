/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.openadmin.web.form.component;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListGridRecord {

    protected ListGrid listGrid;
    protected String id;
    protected String displayOrder;
    protected String altId;
    protected List<Field> fields = new ArrayList<Field>();
    protected List<Field> hiddenFields = new ArrayList<Field>();
    protected Boolean isDirty;
    protected Boolean isError;
    protected String status;
    protected String statusCssClass;
    protected String errorKey;
    protected String errorMessage;
    protected ListGridRecordIcon icon;
    protected Boolean isLocked = false;
    protected Boolean isReadOnly = false;
    protected Entity entity;

    /**
     * Convenience map keyed by the field name. Used to guarantee field ordering with header fields within a ListGrid
     */
    protected Map<String, Field> fieldMap;
    
    public String getPath() {
        String path = listGrid.getPath() + "/" + id;
        if (!StringUtils.isEmpty(altId)) {
            path += "/" + altId;
        }
        return path;
    }
    
    public boolean getCanLinkToExternalEntity() {
        return StringUtils.isNotBlank(listGrid.getExternalEntitySectionKey());
    }
    
    public String getExternalEntityPath() {
        return listGrid.getExternalEntitySectionKey() + "/" + id;
    }

    public ListGrid getListGrid() {
        return listGrid;
    }
    
    public void setListGrid(ListGrid listGrid) {
        this.listGrid = listGrid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getIndex() {
        return listGrid.getStartIndex() + listGrid.getRecords().indexOf(this);
    }

    /**
     * Normally you should not be looping through these fields. In order to preserve proper field ordering, instead you
     * should loop through {@link ListGrid#getHeaderFields()} and then invoke the {@link #getField(String)} method
     * with that header field name.
     * 
     * @return
     */
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getHiddenFields() {
        return hiddenFields;
    }

    public void setHiddenFields(List<Field> hiddenFields) {
        this.hiddenFields = hiddenFields;
    }

    /**
     * Returns a {@link Field} in this record for a particular field name. Used when displaying a {@link ListGrid} in order
     * to guarantee proper field ordering
     * 
     * @param fieldName
     * @return
     */
    public Field getField(String fieldName) {
        if (fieldMap == null) {
            fieldMap = new LinkedHashMap<String, Field>();
            for (Field field : fields) {
                fieldMap.put(field.getName(), field);
            }
            for (Field hiddenField : hiddenFields) {
                fieldMap.put(hiddenField.getName(), hiddenField);
            }
        }
        Field field = fieldMap.get(fieldName);
        
        // We'll return a null field is this particular record doesn't have this polymorphic property.
        // This prevents NPEs in list grids
        if (field == null) {
            field = new Field();
        }
        
        return field;
    }
    
    public void clearFieldMap() {
        fieldMap = null;
    }

    public String getHiddenFieldsJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"hiddenFields\":[");
        for (int j=0;j<hiddenFields.size();j++) {
            sb.append("{\"name\":\"");
            sb.append(hiddenFields.get(j).getName());
            sb.append("\",\"val\":");
            sb.append(JSONObject.quote(hiddenFields.get(j).getValue()));
            sb.append("}");
            if (j < hiddenFields.size()-1) {
                sb.append(",");
            }
        }
        sb.append("]}");

        return sb.toString();
    }

    public Boolean getIsDirty() {
        return isDirty == null ? false : isDirty;
    }

    public void setDirty(Boolean isDirty) {
        this.isDirty = isDirty;
    }

    public Boolean getReadOnly() {
        return isReadOnly == null ? false : isReadOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        isReadOnly = readOnly;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Boolean getIsError() {
        return isError == null ? false : isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public String getStatus() {
            return status;
        }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCssClass() {
        return statusCssClass;
    }

    public void setStatusCssClass(String statusCssClass) {
        this.statusCssClass = statusCssClass;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }
    
    /**
     * Actual, localized error message.  If set, this will override the error key.
     * @return
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * If set, this will override the errorKey.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ListGridRecordIcon getIcon() {
        if (icon != null) {
            return icon;
        }
        
        if (getIsError()) {
            String msgToUser = getErrorMessage();
            if (msgToUser == null) {
                msgToUser = BLCMessageUtils.getMessage(getErrorKey());
            }

            return new ListGridRecordIcon()
                .withCssClass("fa fa-exclamation-triangle")
                .withMessage(msgToUser)
                .withHasDetails(true);
        } else if (getStatus() != null) {
            return new ListGridRecordIcon()
                    .withCssClass(getStatusCssClass())
                    .withMessage(getStatus())
                    .withHasDetails(true);
        }

        if (getIsDirty()) {
            return new ListGridRecordIcon()
                .withCssClass("blc-icon-site-updates")
                .withMessage(BLCMessageUtils.getMessage("listgrid.record.edited"))
                .withHasDetails(false);
        }
        
        return null;
    }
    
    public void setIcon(ListGridRecordIcon icon) {
        this.icon = icon;
    }
    
    public Boolean getHasIcon() {
        return icon != null || getIsError() || getIsDirty() || getStatus() != null;
    }
    
    public String getAltId() {
        return altId;
    }

    public void setAltId(String altId) {
        this.altId = altId;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }
}
