
package org.broadleafcommerce.openadmin.web.form.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.openadmin.web.form.entity.Field;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ListGrid {

    protected String className;
    protected String friendlyName = null;
    protected int order;
    
    protected Set<Field> headerFields = new TreeSet<Field>(new Comparator<Field>() {

        @Override
        public int compare(Field o1, Field o2) {
            return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getFriendlyName(), o2.getFriendlyName())
                    .append(o1.getName(), o2.getName())
                    .toComparison();
        }
    });
    protected List<ListGridRecord> records = new ArrayList<ListGridRecord>();
    protected List<ListGridAction> toolbarActions = new ArrayList<ListGridAction>();
    
    /**
     * These actions will start greyed out and unable to be clicked until a specific row has been selected
     */
    protected List<ListGridAction> rowActions = new ArrayList<ListGridAction>();
    protected int startIndex = 0;
    
    protected AddMethodType addMethodType;
    protected String listGridType;
    
    // The section url that maps to this particular list grid
    protected String sectionKey;

    // If this list grid is a sublistgrid, meaning it is rendered as part of a different entity, these properties
    // help identify the parent entity.
    protected String externalEntitySectionKey;
    protected String containingEntityId;
    protected String subCollectionFieldName;

    public enum Type {
        MAIN,
        INLINE,
        TO_ONE,
        BASIC,
        ADORNED,
        ADORNED_WITH_FORM,
        MAP
    }
    
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        
        if (!getSectionKey().startsWith("/")) {
            sb.append("/");
        }
        
        sb.append(getSectionKey());
        if (getContainingEntityId() != null) {
            sb.append("/").append(getContainingEntityId());
        }
        
        if (StringUtils.isNotBlank(getSubCollectionFieldName())) {
            sb.append("/").append(getSubCollectionFieldName());
        }
        
        //to-one grids need a slightly different grid URL; these need to be appended with 'select'
        //TODO: surely there's a better way to do this besides just hardcoding the 'select'?
        if (Type.TO_ONE.toString().toLowerCase().equals(listGridType)) {
            sb.append("/select");
        }
        
        return sb.toString();
    }
    
    public void addRowAction(ListGridAction action) {
        getRowActions().add(action);
    }
    
    public void addToolbarAction(ListGridAction action) {
        getToolbarActions().add(action);
    }
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }

    public Set<Field> getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(Set<Field> headerFields) {
        this.headerFields = headerFields;
    }

    public List<ListGridRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ListGridRecord> records) {
        this.records = records;
    }
    
    public List<ListGridAction> getToolbarActions() {
        return toolbarActions;
    }
    
    public void setToolbarActions(List<ListGridAction> toolbarActions) {
        this.toolbarActions = toolbarActions;
    }
    
    public List<ListGridAction> getRowActions() {
        return rowActions;
    }
    
    public void setRowActions(List<ListGridAction> rowActions) {
        this.rowActions = rowActions;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public AddMethodType getAddMethodType() {
        return addMethodType;
    }

    public void setAddMethodType(AddMethodType addMethodType) {
        this.addMethodType = addMethodType;
    }

    public String getListGridType() {
        return listGridType;
    }

    public void setListGridType(Type listGridType) {
        this.listGridType = listGridType.toString().toLowerCase();
    }

    public String getContainingEntityId() {
        return containingEntityId;
    }

    public void setContainingEntityId(String containingEntityId) {
        this.containingEntityId = containingEntityId;
    }

    public String getSubCollectionFieldName() {
        return subCollectionFieldName;
    }

    public void setSubCollectionFieldName(String subCollectionFieldName) {
        this.subCollectionFieldName = subCollectionFieldName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getSectionKey() {
        return sectionKey;
    }
    
    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }
    
    public String getExternalEntitySectionKey() {
        return externalEntitySectionKey;
    }

    public void setExternalEntitySectionKey(String externalEntitySectionKey) {
        this.externalEntitySectionKey = externalEntitySectionKey;
    }
    
}

