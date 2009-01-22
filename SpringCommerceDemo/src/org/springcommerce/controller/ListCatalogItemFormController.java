package org.springcommerce.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.catalog.service.CatalogService;
import org.springcommerce.web.PaginationCommandObject;
import org.springcommerce.web.PaginationController;

public class ListCatalogItemFormController extends PaginationController {

    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}
    
    @Override
    protected void populatePaginatedList(Map<Object, Object> model,
            PaginationCommandObject object) {
        List<?> catalogItemList = catalogService.readCatalogItemsByName("");
        object.setFullList(catalogItemList);
    }
}
