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

package org.broadleafcommerce.openadmin.web.form.entity;

import org.broadleafcommerce.openadmin.web.form.component.ListGrid;

public class FieldGroupItem {

    protected String type;
    protected Field field;
    protected ListGrid listGrid;

    public enum Type {
        FIELD,
        LISTGRID,
        CUSTOM_FIELD
    }

    public FieldGroupItem(Field field) {
        this.type = Type.FIELD.toString();
        this.field = field;
    }

    public FieldGroupItem(ListGrid listGrid) {
        this.type = Type.LISTGRID.toString();
        this.listGrid = listGrid;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        setType(Type.FIELD.toString());
        this.field = field;
    }

    public void setCustomField(Field field) {
        setType(Type.CUSTOM_FIELD.toString());
        this.field = field;
    }

    public ListGrid getListGrid() {
        return listGrid;
    }

    public void setListGrid(ListGrid listGrid) {
        setType(Type.LISTGRID.toString());
        this.listGrid = listGrid;
    }

    public boolean isField() {
        return Type.FIELD.toString().equals(getType());
    }

    public boolean isListGrid() {
        return Type.LISTGRID.toString().equals(getType());
    }

    public boolean isCustomField() {
        return Type.CUSTOM_FIELD.toString().equals(getType());
    }

    public Integer getOrder() {
        if (isField() || isCustomField()) {
            return field.getOrder();
        } else {
            return listGrid.getOrder();
        }
    }

    public String getFriendlyName() {
        if (isField() || isCustomField()) {
            return field.getFriendlyName();
        } else {
            return listGrid.getFriendlyName();
        }
    }

    public String getName() {
        if (isField() || isCustomField()) {
            return field.getName();
        } else {
            return listGrid.getSubCollectionFieldName();
        }
    }

    public boolean isVisible() {
        if (isField() || isCustomField()) {
            return field.getIsVisible();
        } else {
            return listGrid != null;
        }
    }
}
