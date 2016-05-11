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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.broadleafcommerce.common.util.BLCMessageUtils;
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

public class FieldGroup {

    protected String title;
    protected String key;
    protected Integer order;
    protected Set<FieldGroupItem> alternateOrderedGroupItems = new HashSet<>();
    protected Set<FieldGroupItem> groupItems = new HashSet<>();
    protected Boolean isVisible;
    protected Integer column;
    protected Boolean isUntitled;
    protected Boolean collapsed;
    protected String toolTip;
    protected String customTemplate;
    protected Map<String, Object> groupAttributes = new HashMap<String, Object>();

    public void removeListGrid(ListGrid listGrid) {
        FieldGroupItem groupItem = findFieldGroupItemByListGrid(listGrid);
        groupItems.remove(groupItem);
    }

    public Boolean getIsVisible() {
        if (isVisible != null) {
            return isVisible;
        }
        for (FieldGroupItem groupItem : getGroupItems()) {
            if (groupItem.isVisible()) {
                return true;
            }
        }
        return false;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Boolean getIsUntitled() {
        if (isUntitled != null) {
            return isUntitled;
        }
        return false;
    }

    public void setIsUntitled(Boolean isUntitled) {
        this.isUntitled = isUntitled;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        if (title != null) {
            return title;
        } else if (key != null) {
            return BLCMessageUtils.getMessage(key);
        }

        return null;
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

    public String getCustomTemplate() {
        return customTemplate;
    }

    public void setCustomTemplate(String customTemplate) {
        this.customTemplate = customTemplate;
    }

    public FieldGroup withKey(String key) {
        setKey(key);
        return this;
    }

    public FieldGroup withTitle(String title) {
        setTitle(title);
        return this;
    }
    
    public FieldGroup withOrder(Integer order) {
        setOrder(order);
        return this;
    }

    public FieldGroup withCustomTemplate(String customTemplate) {
        setCustomTemplate(customTemplate);
        return this;
    }

    public boolean addField(Field field) {
        if (field.getAlternateOrdering()) {
            return alternateOrderedGroupItems.add(new FieldGroupItem(field));
        } else {
            return groupItems.add(new FieldGroupItem(field));
        }
    }

    public void addFields(Set<Field> fields) {
        for (Field field : fields) {
            addField(field);
        }
    }

    public void addListGrid(ListGrid listGrid) {
        groupItems.add(new FieldGroupItem(listGrid));
    }

    public boolean removeField(Field field) {
        FieldGroupItem groupItem = findFieldGroupItemByField(field);
        if (field.getAlternateOrdering()) {
            return alternateOrderedGroupItems.remove(groupItem);
        } else {
            return groupItems.remove(groupItem);
        }
    }

    public Set<FieldGroupItem> getGroupItems() {
        List<FieldGroupItem> myGroupItems = new ArrayList<>(groupItems);
        Collections.sort(myGroupItems, new Comparator<FieldGroupItem>() {
            @Override
            public int compare(FieldGroupItem o1, FieldGroupItem o2) {
                return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getFriendlyName(), o2.getFriendlyName())
                    .append(o1.getName(), o2.getName())
                    .toComparison();
            }
        });
        if (!alternateOrderedGroupItems.isEmpty()) {
            List<FieldGroupItem> mapGroupItemsList = new ArrayList<>(alternateOrderedGroupItems);
            Collections.sort(mapGroupItemsList, new Comparator<FieldGroupItem>() {
                @Override
                public int compare(FieldGroupItem o1, FieldGroupItem o2) {
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
            List<FieldGroupItem> smallOrderGroupItems = new ArrayList<>();
            for (FieldGroupItem mapField : mapGroupItemsList) {
                if (mapField.getOrder() <= 0) {
                    smallOrderGroupItems.add(mapField);
                }
            }
            myGroupItems.addAll(0, smallOrderGroupItems);
            /*
            Alternate ordered fields (specifically custom fields) have a different ordering rule than regular fields. For example,
            if a user enters 3 for the field order value for a custom field, that custom field should be the third
            on the form. Regular BLC AdminPresentation fields tends to have orders like 1000, 2000, etc..., so this
            distinction is necessary.
             */
            for (FieldGroupItem mapField : mapGroupItemsList) {
                if (mapField.getOrder() <= 0) {
                    continue;
                }
                if (mapField.getOrder() < myGroupItems.size() + 1) {
                    myGroupItems.add(mapField.getOrder() - 1, mapField);
                    continue;
                }
                myGroupItems.add(mapField);
            }
        }

        //don't allow any modification of the fields
        return Collections.unmodifiableSet(new LinkedHashSet<>(myGroupItems));
    }

    public void setGroupItems(Set<FieldGroupItem> groupItems) {
        this.groupItems = groupItems;
    }

    public FieldGroupItem findFieldGroupItemByField(Field field) {
        for (FieldGroupItem groupItem : groupItems) {
            if (groupItem.isField() && field != null && field.equals(groupItem.getField())) {
                return groupItem;
            }
        }
        for (FieldGroupItem groupItem : alternateOrderedGroupItems) {
            if (groupItem.isField() && field != null && field.equals(groupItem.getField())) {
                return groupItem;
            }
        }
        return null;
    }

    public FieldGroupItem findFieldGroupItemByListGrid(ListGrid listGrid) {
        for (FieldGroupItem groupItem : groupItems) {
            if (groupItem.isListGrid() && listGrid != null && listGrid.equals(groupItem.getListGrid())) {
                return groupItem;
            }
        }
        for (FieldGroupItem groupItem : alternateOrderedGroupItems) {
            if (groupItem.isListGrid() && listGrid != null && listGrid.equals(groupItem.getListGrid())) {
                return groupItem;
            }
        }
        return null;
    }

    public boolean containsFieldData() {
        for (Field field : getFields()) {
            if (field.getValue() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFieldOrListGrid() {
        return groupItems.size() > 0 || alternateOrderedGroupItems.size() > 0;
    }

    public Set<Field> getFields() {
        Set<Field> fields = new HashSet<>();
        for (FieldGroupItem groupItem : getGroupItems()) {
            if (groupItem.isField() || groupItem.isCustomField()) {
                fields.add(groupItem.getField());
            }
        }
        return fields;
    }

    public Set<ListGrid> getListGrids() {
        Set<ListGrid> listGrids = new HashSet<>();
        for (FieldGroupItem groupItem : getGroupItems()) {
            if (groupItem.isListGrid()) {
                listGrids.add(groupItem.getListGrid());
            }
        }
        return listGrids;
    }
}
