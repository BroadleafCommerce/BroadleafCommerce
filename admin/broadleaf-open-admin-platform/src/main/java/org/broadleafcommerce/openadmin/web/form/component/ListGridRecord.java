
package org.broadleafcommerce.openadmin.web.form.component;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.web.form.entity.Field;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListGridRecord {

    protected ListGrid listGrid;
    protected String id;
    protected List<Field> fields = new ArrayList<Field>();
    
    /**
     * Convenience map keyed by the field name. Used to guarantee field ordering with header fields within a ListGrid
     */
    protected Map<String, Field> fieldMap;
    
    public String getPath() {
        return listGrid.getPath() + "/" + id;
    }
    
    public boolean getCanLinkToExternalEntity() {
        return StringUtils.isNotBlank(listGrid.getExternalEntitySectionKey());
    }
    
    public String getExternalEntityPath() {
        return listGrid.getExternalEntitySectionKey() + "/" + id;
    }

    public ListGrid getListGrid() {
        return listGrid;
    }
    
    public void setListGrid(ListGrid listGrid) {
        this.listGrid = listGrid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Normally you should not be looping through these fields. In order to preserve proper field ordering, instead you
     * should loop through {@link ListGrid#getHeaderFields()} and then invoke the {@link #getField(String)} method
     * with that header field name.
     * 
     * @return
     */
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
    
    /**
     * Returns a {@link Field} in this record for a particular field name. Used when displaying a {@link ListGrid} in order
     * to guarantee proper field ordering
     * 
     * @param fieldName
     * @return
     */
    public Field getField(String fieldName) {
        if (fieldMap == null) {
            fieldMap = new LinkedHashMap<String, Field>();
            for (Field field : fields) {
                fieldMap.put(field.getName(), field);
            }
        }
        return fieldMap.get(fieldName);
    }

}
