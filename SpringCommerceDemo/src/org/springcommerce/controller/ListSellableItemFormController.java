package org.springcommerce.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.catalog.domain.CatalogItem;
import org.springcommerce.catalog.domain.SellableItem;
import org.springcommerce.catalog.service.CatalogService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ListSellableItemFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

    protected Object formBackingObject(HttpServletRequest request)throws ServletException {
    	return new CatalogItem();
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<SellableItem> sellableItemList = catalogService.readAllSellableItems();
        Map<Object, Object> model = new HashMap<Object, Object>();
        model.put("sellableItemList", sellableItemList);

        return new ModelAndView("listSellableItem", model);
    }
}
