package org.broadleafcommerce.controller;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.web.PaginationCommandObject;
import org.broadleafcommerce.web.PaginationController;

public class ListSellableItemFormController extends PaginationController {
    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

    @Override
    protected void populatePaginatedList(Map<Object, Object> model,
            PaginationCommandObject object) {
        object.setFullList(catalogService.readAllSellableItems());
    }
}
