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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Tab {

    protected String title;
    protected String key;
    protected Integer order;
    protected String tabClass;
    protected Boolean isMultiColumn;
    protected Boolean wantsFullScreen = false;
    protected String customTemplate;
    private boolean isTabsPresent = false;

    TreeSet<FieldGroup> fieldGroups = new TreeSet<FieldGroup>(new Comparator<FieldGroup>() {
        @Override
        public int compare(FieldGroup o1, FieldGroup o2) {
            return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getTitle(), o2.getTitle())
                    .append(o1.getKey(), o2.getKey())
                    .toComparison();
        }
    });

    Set<ListGrid> listGrids = new TreeSet<ListGrid>(new Comparator<ListGrid>() {
        @Override
        public int compare(ListGrid o1, ListGrid o2) {
            return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getSubCollectionFieldName(), o2.getSubCollectionFieldName())
                    .toComparison();
        }
    });

    public Tab withTitle(String title) {
        setTitle(title);
        return this;
    }

    public Tab withKey(String key) {
        setKey(key);
        return this;
    }

    public Tab withOrder(Integer order) {
        setOrder(order);
        return this;
    }

    public Tab withTabClass(String tabClass) {
        setTabClass(tabClass);
        return this;
    }

    public Tab withIsMultiColumn(Boolean isMultiColumn) {
        setIsMultiColumn(isMultiColumn);
        return this;
    }

    public Tab withCustomTemplate(String customTemplate) {
        setCustomTemplate(customTemplate);
        return this;
    }
    
    public Boolean getIsVisible() {
        if (listGrids.size() > 0 || isTabsPresent) {
            return true;
        }

        for (FieldGroup fg : fieldGroups) {
            if (fg.getIsVisible()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasFieldOrListGrid() {
        if (listGrids.size() > 0) {
            return true;
        }

        for (FieldGroup fg : fieldGroups) {
            if (fg.hasFieldOrListGrid()) {
                return true;
            }
        }

        if (customTemplate != null) {
            return true;
        }

        return false;
    }

    public FieldGroup findGroupByKey(String key) {
        for (FieldGroup fg : fieldGroups) {
            if (fg.getKey() != null && fg.getKey().equals(key)) {
                return fg;
            }
        }
        return null;
    }

    public FieldGroup findGroupByTitle(String title) {
        for (FieldGroup fg : fieldGroups) {
            if (fg.getTitle() != null && fg.getTitle().equals(title)) {
                return fg;
            }
        }
        return null;
    }
    
    public List<Field> getFields() {
        List<Field> fields = new ArrayList<Field>();
        for (FieldGroup fg : getFieldGroups()) {
            fields.addAll(fg.getFields());
        }
        return fields;
    }

    public void removeFieldGroup(FieldGroup fieldGroup) {
        fieldGroups.remove(fieldGroup);
    }
    
    public void removeListGrid(ListGrid listGrid) {
        listGrids.remove(listGrid);
    }

    public String getTabClass() {
        return StringUtils.isBlank(tabClass) ? "" : " " + tabClass;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Set<FieldGroup> getFieldGroups() {
        return fieldGroups;
    }

    public void setFieldGroups(Set<FieldGroup> fieldGroups) {
        this.fieldGroups.clear();
        this.fieldGroups.addAll(fieldGroups);
    }

    public Set<ListGrid> getListGrids() {
        return listGrids;
    }

    public void setListGrids(Set<ListGrid> listGrids) {
        this.listGrids = listGrids;
    }

    public void setTabClass(String tabClass) {
        this.tabClass = tabClass;
    }

    public Boolean getIsMultiColumn() {
        return isMultiColumn == null ? false : isMultiColumn;
    }

    public void setIsMultiColumn(Boolean isMultiColumn) {
        this.isMultiColumn = isMultiColumn;
    }

    public Boolean getWantsFullScreen() {
        return wantsFullScreen;
    }

    public void setWantsFullScreen(Boolean wantsFullScreen) {
        this.wantsFullScreen = wantsFullScreen;
    }

    public boolean isTabsPresent() {
        return isTabsPresent;
    }

    public void setTabsPresent(boolean isTabsPresent) {
        this.isTabsPresent = isTabsPresent;
    }

    public String getCustomTemplate() {
        return customTemplate;
    }

    public void setCustomTemplate(String customTemplate) {
        this.customTemplate = customTemplate;
    }
}


