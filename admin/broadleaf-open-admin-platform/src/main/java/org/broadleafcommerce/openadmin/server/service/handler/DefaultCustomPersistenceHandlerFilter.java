package org.broadleafcommerce.openadmin.server.service.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public class DefaultCustomPersistenceHandlerFilter implements CustomPersistenceHandlerFilter {

    protected List<String> filterCustomPersistenceHandlerClassnames = new ArrayList<String>();

    @Override
    public boolean shouldUseHandler(String handlerClassName) {
        return !filterCustomPersistenceHandlerClassnames.contains(handlerClassName);
    }

    public List<String> getFilterCustomPersistenceHandlerClassnames() {
        return filterCustomPersistenceHandlerClassnames;
    }

    public void setFilterCustomPersistenceHandlerClassnames(List<String> filterCustomPersistenceHandlerClassnames) {
        this.filterCustomPersistenceHandlerClassnames = filterCustomPersistenceHandlerClassnames;
    }
}
