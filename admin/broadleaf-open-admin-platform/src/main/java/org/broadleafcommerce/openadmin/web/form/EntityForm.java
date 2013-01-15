package org.broadleafcommerce.openadmin.web.form;

import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityForm {

    protected ClassMetadata metadata;

    protected Entity entity;

    protected Map<String, List<Field>> groupedFields = new HashMap<String, List<Field>>();

    protected Map<String, ListGrid> subordinateListGrids = new HashMap<String, ListGrid>();

    protected List<Property> nonBasicFields = new ArrayList<Property>();

    public ClassMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ClassMetadata metadata) {
        this.metadata = metadata;
    }

    public EntityForm(Entity entity) {
        this.entity = entity;
    }

    public EntityForm() {
        this.entity = new Entity();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Map<String, List<Field>> getGroupedFields() {
        return groupedFields;
    }

    public void setGroupedFields(Map<String, List<Field>> groupedFields) {
        this.groupedFields = groupedFields;
    }

    public List<Property> getNonBasicFields() {
        return nonBasicFields;
    }

    public void setNonBasicFields(List<Property> nonBasicFields) {
        this.nonBasicFields = nonBasicFields;
    }

    public Map<String, ListGrid> getSubordinateListGrids() {
        return subordinateListGrids;
    }

    public void setSubordinateListGrids(Map<String, ListGrid> subordinateListGrids) {
        this.subordinateListGrids = subordinateListGrids;
    }

}
