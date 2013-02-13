
package org.broadleafcommerce.openadmin.web.form.entity;

import java.util.ArrayList;
import java.util.List;

public class FieldGroup {

    protected String title;
    protected int order;
    List<Field> fields = new ArrayList<Field>();

    public Boolean getIsVisible() {
        for (Field f : fields) {
            if (f.getIsVisible()) {
                return true;
            }
        }
        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<Field> getFields() {
        return fields;
    }

}
