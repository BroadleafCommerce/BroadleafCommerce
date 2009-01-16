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
import org.springcommerce.util.CreateSellableItem;

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
        CreateSellableItem createSellableItem = new CreateSellableItem(); 

        if (request.getParameter("catalogItemId") != null) {
            createCatalogItem = catalogService.readCatalogItemById(Long.valueOf(request.getParameter("catalogItemId")));
            sellableItem.setCatalogItem(createCatalogItem);
            createSellableItem.setSellableItem(sellableItem);
        }
        
        if (request.getParameter("sellableItemId") != null){
        	sellableItem = catalogService.readSellableItemById(new Long(request.getParameter("sellableItemId")));        	
        	createSellableItem.setSellableItem(sellableItem);
        	Set<ItemAttribute> attributes = sellableItem.getItemAttributes();
        	for(Iterator<ItemAttribute> it = attributes.iterator(); it.hasNext();){
        		ItemAttribute thisAttribute = (ItemAttribute)it.next();
        		if(thisAttribute.getName().equals("price"))
        			createSellableItem.setPrice(thisAttribute.getValue());
        	}
        }

        return createSellableItem;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
        CreateSellableItem createSellableItem = (CreateSellableItem) command;

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }

        HashSet<ItemAttribute> ias = new HashSet<ItemAttribute>();
        if(createSellableItem.getSellableItem().getId() == null){
        	ItemAttribute itemAttribute = new ItemAttribute();
        	itemAttribute.setName("price");
        	itemAttribute.setValue(createSellableItem.getPrice());
        	
        	itemAttribute.setSellableItem(createSellableItem.getSellableItem());
        	
        	ias.add(itemAttribute);
        	
        	createSellableItem.getSellableItem().setItemAttributes(ias);        	
        	
        }else{
        	Set<ItemAttribute> attributes = createSellableItem.getSellableItem().getItemAttributes();
        	for(Iterator<ItemAttribute> it = attributes.iterator(); it.hasNext();){
        		ItemAttribute thisAttribute = (ItemAttribute)it.next();
        		if(thisAttribute.getName().equals("price")){
        			thisAttribute.setValue(createSellableItem.getPrice());
        			//ias.add(thisAttribute);
        		}
        			
        	}
        }
        
        
        catalogService.saveSellableItem(createSellableItem.getSellableItem());
        mav.addObject("saved", true);

        return mav;
    }
}
