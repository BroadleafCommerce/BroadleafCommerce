
package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    protected List<RuleBuilder> collectionRuleBuilders = new ArrayList<RuleBuilder>();

    // This is used to data-bind when this entity form is submitted
    protected Map<String, Field> fields = null;

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

        return fields;
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

    public void removeField(String fieldName) {
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

    public List<RuleBuilder> getCollectionRuleBuilders() {
        return collectionRuleBuilders;
    }

    public void setCollectionRuleBuilders(List<RuleBuilder> collectionRuleBuilders) {
        this.collectionRuleBuilders = collectionRuleBuilders;
    }

    public Set<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(Set<Tab> tabs) {
        this.tabs = tabs;
    }

}
