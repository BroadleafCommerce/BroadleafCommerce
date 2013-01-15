package org.broadleafcommerce.openadmin.web.form.component;

import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.web.form.HeaderColumn;

import java.util.List;


public class ListGrid {

    protected List<HeaderColumn> headerColumns;

    protected List<Entity> entities;

    protected int startIndex = 0;

    protected ClassMetadata metadata;

    public List<HeaderColumn> getHeaderColumns() {
        return headerColumns;
    }

    public void setHeaderColumns(List<HeaderColumn> headerColumns) {
        this.headerColumns = headerColumns;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public ClassMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ClassMetadata metadata) {
        this.metadata = metadata;
    }

}
