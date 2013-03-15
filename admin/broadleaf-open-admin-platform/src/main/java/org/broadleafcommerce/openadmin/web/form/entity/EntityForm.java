
package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    protected String entityType;
    protected Set<Tab> tabs = new TreeSet<Tab>(new Comparator<Tab>() {
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
            containingGroup.getFields().remove(fieldToRemove);
        }
        
        if (fields != null) {
            fields.remove(fieldName);
        }
        
        return fieldToRemove;
    }

    public void addHiddenField(Field field) {
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

        fieldGroup.getFields().add(field);
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

    public void addRuleBuilder(RuleBuilder ruleBuilder, String tabName, Integer tabOrder) {
        tabName = tabName == null ? DEFAULT_TAB_NAME : tabName;
        tabOrder = tabOrder == null ? DEFAULT_TAB_ORDER : tabOrder;

        Tab tab = findTab(tabName);
        if (tab == null) {
            tab = new Tab();
            tab.setTitle(tabName);
            tab.setOrder(tabOrder);
            tabs.add(tab);
        }

        tab.getRuleBuilders().add(ruleBuilder);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
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

}
