
package org.broadleafcommerce.openadmin.web.form.entity;

import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EntityForm {

    protected String id;
    protected String entityType;
    protected Map<String, FieldGroup> groups = new HashMap<String, FieldGroup>();
    protected List<ListGrid> collectionListGrids = new ArrayList<ListGrid>();
    protected List<RuleBuilder> collectionRuleBuilders = new ArrayList<RuleBuilder>();

    protected Map<String, Field> fields = null;

    /**
     * @return a flattened, field name keyed representation of all of 
     * the fields in all of the groups for this form
     */
    public Map<String, Field> getFields() {
        if (fields == null) {
            Map<String, Field> map = new HashMap<String, Field>();
            for (Entry<String, FieldGroup> entry : groups.entrySet()) {
                for (Field field : entry.getValue().getFields()) {
                    map.put(field.getName(), field);
                }
            }
            fields = map;
        }

        return fields;
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

    public Map<String, FieldGroup> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, FieldGroup> groups) {
        this.groups = groups;
    }

    public List<ListGrid> getCollectionListGrids() {
        return collectionListGrids;
    }

    public void setCollectionListGrids(List<ListGrid> collectionListGrids) {
        this.collectionListGrids = collectionListGrids;
    }

    public List<RuleBuilder> getCollectionRuleBuilders() {
        return collectionRuleBuilders;
    }

    public void setCollectionRuleBuilders(List<RuleBuilder> collectionRuleBuilders) {
        this.collectionRuleBuilders = collectionRuleBuilders;
    }
}
