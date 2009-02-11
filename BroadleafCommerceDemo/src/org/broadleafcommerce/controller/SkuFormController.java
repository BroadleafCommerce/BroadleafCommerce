package org.broadleafcommerce.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.ItemAttribute;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class SkuFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
	protected Object formBackingObject(HttpServletRequest request)
                                throws ServletException {
        Product createProduct = new Product();
        Sku sku = new Sku();

        if (request.getParameter("productId") != null) {
            createProduct = catalogService.readProductById(Long.valueOf(request.getParameter("productId")));
            sku.setProduct(createProduct);
        }
        
        if (request.getParameter("skuId") != null){
        	sku = catalogService.readSkuById(new Long(request.getParameter("skuId")));        	
            Map<String, ItemAttribute> attribs = sku.getItemAttributes();
            if (attribs == null) {
                attribs = new HashMap<String, ItemAttribute>();
            }
            attribs.put("", new ItemAttribute());
        }

        return sku;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
        Sku sku = (Sku) command;

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }
        
        
        catalogService.saveSku(sku);
        mav.addObject("saved", true);

        return mav;
    }
}
