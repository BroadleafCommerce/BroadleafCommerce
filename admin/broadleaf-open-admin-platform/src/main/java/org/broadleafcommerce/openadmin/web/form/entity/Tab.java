
package org.broadleafcommerce.openadmin.web.form.entity;

import org.broadleafcommerce.openadmin.web.form.component.ListGrid;

import java.util.ArrayList;
import java.util.List;

public class Tab {

    protected String title;
    protected int order;
    List<FieldGroup> fieldGroups = new ArrayList<FieldGroup>();
    List<ListGrid> listGrids = new ArrayList<ListGrid>();

    public Boolean getIsVisible() {
        if (listGrids.size() > 0) {
            return true;
        }

        for (FieldGroup fg : fieldGroups) {
            if (fg.getIsVisible()) {
                return true;
            }
        }

        return false;
    }

    public FieldGroup getGroup(String groupTitle) {
        for (FieldGroup fg : fieldGroups) {
            if (fg.getTitle().equals(groupTitle)) {
                return fg;
            }
        }
        return null;
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

    public List<FieldGroup> getFieldGroups() {
        return fieldGroups;
    }

    public List<ListGrid> getListGrids() {
        return listGrids;
    }

    public void setListGrids(List<ListGrid> listGrids) {
        this.listGrids = listGrids;
    }

    public void setFieldGroups(List<FieldGroup> fieldGroups) {
        this.fieldGroups = fieldGroups;
    }

}
