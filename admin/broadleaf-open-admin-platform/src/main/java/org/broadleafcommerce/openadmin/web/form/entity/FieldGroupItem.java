/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        LISTGRID
    }


    public FieldGroupItem(Field field) {
        this.type = Type.FIELD.toString();
        this.field = field;
    }

    public FieldGroupItem(ListGrid listGrid) {
        this.type = Type.LISTGRID.toString();
        this.listGrid = listGrid;
    }

    private void setType(String type) {
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

    public Integer getOrder() {
        if (isField()) {
            return field.getOrder();
        } else {
            return listGrid.getOrder();
        }
    }

    public String getFriendlyName() {
        if (isField()) {
            return field.getFriendlyName();
        } else {
            return listGrid.getFriendlyName();
        }
    }

    public String getName() {
        if (isField()) {
            return field.getName();
        } else {
            return listGrid.getSubCollectionFieldName();
        }
    }

    public boolean isVisible() {
        if (isField()) {
            return field.getIsVisible();
        } else {
            return listGrid != null;
        }
    }


}
