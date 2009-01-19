package org.springcommerce.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springcommerce.catalog.domain.CatalogItem;
import org.springcommerce.catalog.domain.ItemAttribute;
import org.springcommerce.catalog.domain.SellableItem;
import org.springcommerce.catalog.service.CatalogService;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import antlr.collections.impl.Vector;

public class SellableItemFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    protected Object formBackingObject(HttpServletRequest request)
                                throws ServletException {
        CatalogItem createCatalogItem = new CatalogItem();
        SellableItem sellableItem = new SellableItem();

        if (request.getParameter("catalogItemId") != null) {
            createCatalogItem = catalogService.readCatalogItemById(Long.valueOf(request.getParameter("catalogItemId")));
            sellableItem.setCatalogItem(createCatalogItem);
        }
        
        if (request.getParameter("sellableItemId") != null){
        	sellableItem = catalogService.readSellableItemById(new Long(request.getParameter("sellableItemId")));        	
        	Set<ItemAttribute> attributes = sellableItem.getItemAttributes();
        }

        return sellableItem;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
        SellableItem sellableItem = (SellableItem) command;

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }

        
        
        catalogService.saveSellableItem(sellableItem);
        mav.addObject("saved", true);

        return mav;
    }
}
