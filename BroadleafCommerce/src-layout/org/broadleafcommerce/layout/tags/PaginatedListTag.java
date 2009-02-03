package org.broadleafcommerce.layout.tags;

import java.util.List;

import org.broadleafcommerce.web.PaginationCommandObject;

public class PaginatedListTag extends ListTag{

    private static final long serialVersionUID = 1L;
    private PaginationCommandObject paginationObject;
    
    public PaginationCommandObject getPaginationObject() {
        return paginationObject;
    }
    public void setPaginationObject(PaginationCommandObject paginationObject) {
        this.paginationObject = paginationObject;
    }
    @Override
    public List<?> getObjectList() {
        List<?> paginationObjectList = paginationObject.getDisplayList();
        if (paginationObjectList != null) {
            return paginationObjectList;
        }
        return super.getObjectList();
    }
}
