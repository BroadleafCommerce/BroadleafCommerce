
package org.broadleafcommerce.openadmin.web.form.component;

import org.broadleafcommerce.openadmin.web.form.entity.Field;

import java.util.ArrayList;
import java.util.List;

public class ListGridRecord {

    protected String id;
    protected List<Field> fields = new ArrayList<Field>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

}
