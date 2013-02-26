
package org.broadleafcommerce.openadmin.web.form.entity;

import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EntityForm {

    public static final String HIDDEN_GROUP = "hiddenGroup";
    public static final String MAP_KEY_GROUP = "keyGroup";
    public static final String DEFAULT_GROUP_NAME = "Default";
    public static final String DEFAULT_TAB_NAME = "General";

    protected String id;
    protected String entityType;
    protected Map<String, Tab> tabs = new LinkedHashMap<String, Tab>();

    //protected Map<String, FieldGroup> groups = new LinkedHashMap<String, FieldGroup>();
    //protected List<ListGrid> collectionListGrids = new ArrayList<ListGrid>();

    protected List<RuleBuilder> collectionRuleBuilders = new ArrayList<RuleBuilder>();

    protected Map<String, Field> fields = null;
    protected Map<String, FieldGroup> fieldGroups = null;

    /**
     * @return a flattened, field name keyed representation of all of 
     * the fields in all of the groups for this form
     */
    public Map<String, Field> getFields() {
        if (fields == null) {
            Map<String, Field> map = new LinkedHashMap<String, Field>();
            for (Entry<String, Tab> entry : tabs.entrySet()) {
                for (FieldGroup group : entry.getValue().getFieldGroups()) {
                    for (Field field : group.getFields()) {
                        map.put(field.getName(), field);
                    }
                }
            }
            fields = map;
        }

        return fields;
    }

    /**
     * @return a flattened, gropu title keyed representation of all of
     * the groups in all of the tabs for this form
     */
    public Map<String, FieldGroup> getGroups() {
        if (fieldGroups == null) {
            Map<String, FieldGroup> map = new LinkedHashMap<String, FieldGroup>();
            for (Entry<String, Tab> entry : tabs.entrySet()) {
                for (FieldGroup fieldGroup : entry.getValue().getFieldGroups()) {
                    map.put(fieldGroup.getTitle(), fieldGroup);
                }
            }
            fieldGroups = map;
        }

        return fieldGroups;
    }

    public Field findField(String fieldName) {
        for (Entry<String, FieldGroup> entry : getGroups().entrySet()) {
            for (Field field : entry.getValue().getFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    public void removeField(String fieldName) {
        for (Entry<String, FieldGroup> entry : getGroups().entrySet()) {
            Iterator<Field> it = entry.getValue().getFields().listIterator();
            while (it.hasNext()) {
                Field field = it.next();
                if (field.getName().equals(fieldName)) {
                    it.remove();
                }
            }
        }
    }

    public void addHiddenField(Field field) {
        addField(field, HIDDEN_GROUP, DEFAULT_TAB_NAME);
    }

    public void addField(Field field, String groupName, String tabName) {
        groupName = groupName == null ? "Default" : groupName;

        Tab tab = getTabs().get(tabName);
        if (tab == null) {
            tab = new Tab();
            tab.setTitle(tabName);
            getTabs().put(tabName, tab);
        }

        FieldGroup fieldGroup = tab.getGroup(groupName);
        if (fieldGroup == null) {
            fieldGroup = new FieldGroup();
            fieldGroup.setTitle(groupName);
            tab.getFieldGroups().add(fieldGroup);
        }

        fieldGroup.getFields().add(field);
    }

    public void addListGrid(ListGrid listGrid, String tabName) {
        Tab tab = getTabs().get(tabName);
        if (tab == null) {
            tab = new Tab();
            tab.setTitle(tabName);
            getTabs().put(tabName, tab);
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

    public Map<String, Tab> getTabs() {
        return tabs;
    }

    public void setTabs(Map<String, Tab> tabs) {
        this.tabs = tabs;
    }

//    public Map<String, FieldGroup> getGroups() {
//        return groups;
//    }
//
//    public void setGroups(Map<String, FieldGroup> groups) {
//        this.groups = groups;
//    }
//
//    public List<ListGrid> getCollectionListGrids() {
//        return collectionListGrids;
//    }
//
//    public void setCollectionListGrids(List<ListGrid> collectionListGrids) {
//        this.collectionListGrids = collectionListGrids;
//    }

    public List<RuleBuilder> getCollectionRuleBuilders() {
        return collectionRuleBuilders;
    }

    public void setCollectionRuleBuilders(List<RuleBuilder> collectionRuleBuilders) {
        this.collectionRuleBuilders = collectionRuleBuilders;
    }
}
