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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FieldGroup {

    protected String title;
    protected Integer order;
    protected Set<Field> alternateOrderedFields = new HashSet<Field>();
    protected Set<Field> fields = new HashSet<Field>();
    protected Boolean isVisible;
    protected Integer column;
    protected Boolean isBorderless;
    protected Boolean collapsed;
    protected String toolTip;
    protected Map<String, Object> groupAttributes = new HashMap<String, Object>();


    Set<ListGrid> listGrids = new TreeSet<ListGrid>(new Comparator<ListGrid>() {
        @Override
        public int compare(ListGrid o1, ListGrid o2) {
            return new CompareToBuilder()
                .append(o1.getOrder(), o2.getOrder())
                .append(o1.getSubCollectionFieldName(), o2.getSubCollectionFieldName())
                .toComparison();
        }
    });

    public void removeListGrid(ListGrid listGrid) {
        listGrids.remove(listGrid);
    }

    public Set<ListGrid> getListGrids() {
        return listGrids;
    }

    public void setListGrids(Set<ListGrid> listGrids) {
        this.listGrids = listGrids;
    }

    public Boolean getIsVisible() {
        if (isVisible != null) {
            return isVisible;
        }
        for (Field f : getFields()) {
            if (f.getIsVisible()) {
                return true;
            }
        }
        if (listGrids.size() > 0) {
            return true;
        }
        return false;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Boolean getIsBorderless() {
        if (isBorderless != null) {
            return isBorderless;
        }
        return false;
    }

    public void setIsBorderless(Boolean isBorderless) {
        this.isBorderless = isBorderless;
    }

    public Boolean getCollapsed() {
        if (collapsed != null) {
            return collapsed;
        }
        return false;
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public Map<String, Object> getGroupAttributes() {
        return groupAttributes;
    }

    public void setGroupAttributes(Map<String, Object> groupAttributes) {
        this.groupAttributes = groupAttributes;
    }


    public FieldGroup withTitle(String title) {
        setTitle(title);
        return this;
    }
    
    public FieldGroup withOrder(Integer order) {
        setOrder(order);
        return this;
    }

    public boolean addField(Field field) {
        if (field.getAlternateOrdering()) {
            return alternateOrderedFields.add(field);
        } else {
            return fields.add(field);
        }
    }

    public boolean removeField(Field field) {
        if (field.getAlternateOrdering()) {
            return alternateOrderedFields.remove(field);
        } else {
            return fields.remove(field);
        }
    }

    public Set<Field> getFields() {
        List<Field> myFields = new ArrayList<Field>();
        myFields.addAll(fields);
        Collections.sort(myFields, new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getFriendlyName(), o2.getFriendlyName())
                    .append(o1.getName(), o2.getName())
                    .toComparison();
            }
        });
        if (!alternateOrderedFields.isEmpty()) {
            List<Field> mapFieldsList = new ArrayList<Field>(alternateOrderedFields);
            Collections.sort(mapFieldsList, new Comparator<Field>() {
                @Override
                public int compare(Field o1, Field o2) {
                    return new CompareToBuilder()
                        .append(o1.getOrder(), o2.getOrder())
                        .append(o1.getFriendlyName(), o2.getFriendlyName())
                        .append(o1.getName(), o2.getName())
                        .toComparison();
                }
            });
            /*
            alternate ordered fields whose order is less or equal to zero appear first and are
            prepended to the response list in order
             */
            List<Field> smallOrderFields = new ArrayList<Field>();
            for (Field mapField : mapFieldsList) {
                if (mapField.getOrder() <= 0) {
                    smallOrderFields.add(mapField);
                }
            }
            myFields.addAll(0, smallOrderFields);
            /*
            Alternate ordered fields (specifically custom fields) have a different ordering rule than regular fields. For example,
            if a user enters 3 for the field order value for a custom field, that custom field should be the third
            on the form. Regular BLC AdminPresentation fields tends to have orders like 1000, 2000, etc..., so this
            distinction is necessary.
             */
            for (Field mapField : mapFieldsList) {
                if (mapField.getOrder() <= 0) {
                    continue;
                }
                if (mapField.getOrder() < myFields.size() + 1) {
                    myFields.add(mapField.getOrder() - 1, mapField);
                    continue;
                }
                myFields.add(mapField);
            }
        }

        //don't allow any modification of the fields
        return Collections.unmodifiableSet(new LinkedHashSet<Field>(myFields));
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public boolean isMasterFieldGroup() {
        if (getTitle() != null && getTitle().toLowerCase().contains("master")) {
            return true;
        }
        return false;
    }

    public boolean containsFieldData() {
        for (Field field : fields) {
            if (field.getValue() != null) {
                return true;
            }
        }
        return false;
    }

}
