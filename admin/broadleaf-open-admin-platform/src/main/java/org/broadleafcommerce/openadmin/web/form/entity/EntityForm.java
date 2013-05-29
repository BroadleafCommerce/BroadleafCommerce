
/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class EntityForm {

    public static final String HIDDEN_GROUP = "hiddenGroup";
    public static final String MAP_KEY_GROUP = "keyGroup";
    public static final String DEFAULT_GROUP_NAME = "Default";
    public static final Integer DEFAULT_GROUP_ORDER = 99999;
    public static final String DEFAULT_TAB_NAME = "General";
    public static final Integer DEFAULT_TAB_ORDER = 100;

    protected String id;
    protected String idProperty = "id";
    protected String ceilingEntityClassname;
    protected String entityType;
    protected String mainEntityName;
    protected String sectionKey;
    protected Set<Tab> tabs = new TreeSet<Tab>(new Comparator<Tab>() {
        @Override
        public int compare(Tab o1, Tab o2) {
            return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getTitle(), o2.getTitle())
                    .toComparison();
        }
    });

    // This is used to data-bind when this entity form is submitted
    protected Map<String, Field> fields = null;
    
    // This is used in cases where there is a sub-form on this page that is dynamically
    // rendered based on other values on this entity form. It is keyed by the name of the
    // property that drives the dynamic form.
    protected Map<String, EntityForm> dynamicForms = new HashMap<String, EntityForm>();
    
    // These values are used when dynamic forms are in play. They are not rendered to the client,
    // but they can be used when performing actions on the submit event
    protected Map<String, DynamicEntityFormInfo> dynamicFormInfos = new HashMap<String, DynamicEntityFormInfo>();
    
    protected List<EntityFormAction> actions = new ArrayList<EntityFormAction>();

    /**
     * @return a flattened, field name keyed representation of all of 
     * the fields in all of the groups for this form
     */
    public Map<String, Field> getFields() {
        if (fields == null) {
            Map<String, Field> map = new LinkedHashMap<String, Field>();
            for (Tab tab : tabs) {
                for (FieldGroup group : tab.getFieldGroups()) {
                    for (Field field : group.getFields()) {
                        map.put(field.getName(), field);
                    }
                }
            }
            fields = map;
        }
        
        for (Entry<String, EntityForm> entry : dynamicForms.entrySet()) {
            Map<String, Field> dynamicFormFields = entry.getValue().getFields();
            fields.putAll(dynamicFormFields);
        }

        return fields;
    }
    
    public void clearFieldsMap() {
        fields = null;
    }
    
    public List<ListGrid> getAllListGrids() {
        List<ListGrid> list = new ArrayList<ListGrid>();
        for (Tab tab : tabs) {
            for (ListGrid lg : tab.getListGrids()) {
                list.add(lg);
            }
        }
        return list;
    }
    
    /**
     * Convenience method for grabbing a grid by its collection field name. This is very similar to {@link #findField(String)}
     * but differs in that this only searches through the sub collections for the current entity
     * 
     * @param collectionFieldName the field name of the collection on the top-level entity
     * @return
     */
    public ListGrid findListGrid(String collectionFieldName) {
        for (ListGrid grid : getAllListGrids()) {
            if (grid.getSubCollectionFieldName().equals(collectionFieldName)) {
                return grid;
            }
        }
        return null;
    }

    public Tab findTab(String tabTitle) {
        for (Tab tab : tabs) {
            if (tab.getTitle() != null && tab.getTitle().equals(tabTitle)) {
                return tab;
            }
        }
        return null;
    }

    public Field findField(String fieldName) {
        for (Tab tab : tabs) {
            for (FieldGroup fieldGroup : tab.getFieldGroups()) {
                for (Field field : fieldGroup.getFields()) {
                    if (field.getName().equals(fieldName)) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    public Field removeField(String fieldName) {
        Field fieldToRemove = null;
        FieldGroup containingGroup = null;

        findField: {
            for (Tab tab : tabs) {
                for (FieldGroup fieldGroup : tab.getFieldGroups()) {
                    for (Field field : fieldGroup.getFields()) {
                        if (field.getName().equals(fieldName)) {
                            fieldToRemove = field;
                            containingGroup = fieldGroup;
                            break findField;
                        }
                    }
                }
            }
        }

        if (fieldToRemove != null) {
            containingGroup.removeField(fieldToRemove);
        }
        
        if (fields != null) {
            fields.remove(fieldName);
        }
        
        return fieldToRemove;
    }
    
    public void removeTab(Tab tab) {
        tabs.remove(tab);
    }
    
    public ListGrid removeListGrid(String subCollectionFieldName) {
        ListGrid lgToRemove = null;
        Tab containingTab = null;

        findLg: {
            for (Tab tab : tabs) {
                for (ListGrid lg : tab.getListGrids()) {
                    if (subCollectionFieldName.equals(lg.getSubCollectionFieldName())) {
                        lgToRemove = lg;
                        containingTab = tab;
                        break findLg;
                    }
                }
            }
        }

        if (lgToRemove != null) {
            containingTab.removeListGrid(lgToRemove);
        }
        
        if (containingTab.getListGrids().size() == 0 && containingTab.getFields().size() == 0) {
            removeTab(containingTab);
        }
        
        return lgToRemove;
    }

    public void addHiddenField(Field field) {
        if (StringUtils.isBlank(field.getFieldType())) {
            field.setFieldType(SupportedFieldType.HIDDEN.toString());
        }
        addField(field, HIDDEN_GROUP, DEFAULT_GROUP_ORDER, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addField(Field field) {
        addField(field, DEFAULT_GROUP_NAME, DEFAULT_GROUP_ORDER, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addMapKeyField(Field field) {
        addField(field, MAP_KEY_GROUP, DEFAULT_GROUP_ORDER, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addField(Field field, String groupName, Integer groupOrder, String tabName, Integer tabOrder) {
        //        System.out.println(String.format("Adding field [%s] to group [%s] to tab [%s]", field.getName(), groupName, tabName));
        groupName = groupName == null ? DEFAULT_GROUP_NAME : groupName;
        groupOrder = groupOrder == null ? DEFAULT_GROUP_ORDER : groupOrder;
        tabName = tabName == null ? DEFAULT_TAB_NAME : tabName;
        tabOrder = tabOrder == null ? DEFAULT_TAB_ORDER : tabOrder;

        Tab tab = findTab(tabName);
        if (tab == null) {
            tab = new Tab();
            tab.setTitle(tabName);
            tab.setOrder(tabOrder);
            tabs.add(tab);
        }

        FieldGroup fieldGroup = tab.findGroup(groupName);
        if (fieldGroup == null) {
            fieldGroup = new FieldGroup();
            fieldGroup.setTitle(groupName);
            fieldGroup.setOrder(groupOrder);
            tab.getFieldGroups().add(fieldGroup);
        }

        fieldGroup.addField(field);
    }

    public void addListGrid(ListGrid listGrid, String tabName, Integer tabOrder) {
        Tab tab = findTab(tabName);
        if (tab == null) {
            tab = new Tab();
            tab.setTitle(tabName);
            tab.setOrder(tabOrder);
            tabs.add(tab);
        }

        tab.getListGrids().add(listGrid);
    }

    public void addAction(EntityFormAction action) {
        actions.add(action);
    }
    
    public void removeAction(EntityFormAction action) {
        actions.remove(action);
    }
    
    public void removeAllActions() {
        actions.clear();
    }
    
    public EntityForm getDynamicForm(String name) {
        return getDynamicForms().get(name);
    }
    
    public void putDynamicForm(String name, EntityForm ef) {
        getDynamicForms().put(name, ef);
    }
    
    public DynamicEntityFormInfo getDynamicFormInfo(String name) {
        return getDynamicFormInfos().get(name);
    }
    
    public void putDynamicFormInfo(String name, DynamicEntityFormInfo info) {
        getDynamicFormInfos().put(name, info);
    }
    
    public void setReadOnly() {
        if (getFields() != null) {
            for (Entry<String, Field> entry : getFields().entrySet()) {
                entry.getValue().setReadOnly(true);
            }
        }
        
        if (getAllListGrids() != null) {
            for (ListGrid lg : getAllListGrids()) {
                lg.setReadOnly(true);
            }
        }
        
        if (getDynamicForms() != null) {
            for (Entry<String, EntityForm> entry : getDynamicForms().entrySet()) {
                entry.getValue().setReadOnly();
            }
        }
        
        actions.clear();
    }

    public List<EntityFormAction> getActions() {
        List<EntityFormAction> clonedActions = new ArrayList<EntityFormAction>(actions);
        Collections.reverse(clonedActions);
        return Collections.unmodifiableList(clonedActions);
    }
    
    /* *********************** */
    /* GENERIC GETTERS/SETTERS */
    /* *********************** */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getIdProperty() {
        return idProperty;
    }
    
    public void setIdProperty(String idProperty) {
        this.idProperty = idProperty;
    }

    public String getCeilingEntityClassname() {
        return ceilingEntityClassname;
    }

    public void setCeilingEntityClassname(String ceilingEntityClassname) {
        this.ceilingEntityClassname = ceilingEntityClassname;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public String getMainEntityName() {
        return StringUtils.isBlank(mainEntityName) ? "" : mainEntityName;
    }
    
    public void setMainEntityName(String mainEntityName) {
        this.mainEntityName = mainEntityName;
    }

    public String getSectionKey() {
        return sectionKey.charAt(0) == '/' ? sectionKey : '/' + sectionKey;
    }
    
    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }
    
    public Set<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(Set<Tab> tabs) {
        this.tabs = tabs;
    }

    public Map<String, EntityForm> getDynamicForms() {
        return dynamicForms;
    }

    public void setDynamicForms(Map<String, EntityForm> dynamicForms) {
        this.dynamicForms = dynamicForms;
    }
    
    public Map<String, DynamicEntityFormInfo> getDynamicFormInfos() {
        return dynamicFormInfos;
    }

    public void setDynamicFormInfos(Map<String, DynamicEntityFormInfo> dynamicFormInfos) {
        this.dynamicFormInfos = dynamicFormInfos;
    }

    public void setActions(List<EntityFormAction> actions) {
        this.actions = actions;
    }
    
}
