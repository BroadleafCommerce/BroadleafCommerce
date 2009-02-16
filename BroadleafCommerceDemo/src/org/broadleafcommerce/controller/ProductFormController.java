package org.broadleafcommerce.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.BroadleafItemAttribute;
import org.broadleafcommerce.catalog.domain.BroadleafProduct;
import org.broadleafcommerce.catalog.domain.ItemAttribute;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ProductFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    protected Object formBackingObject(HttpServletRequest request)
    throws ServletException {
        Product createProduct = new BroadleafProduct();

        if (request.getParameter("productId") != null) {
            createProduct = catalogService.findProductById(Long.valueOf(request.getParameter("productId")));
        }
        Map<String, ItemAttribute> attribs = createProduct.getItemAttributes();
        if (attribs == null) {
            attribs = new HashMap<String, ItemAttribute>();
        }
        attribs.put("foo", new BroadleafItemAttribute());

        return createProduct;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
    throws Exception {
        Product product = (Product) command;

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }

        catalogService.saveProduct(product);
        mav.addObject("saved", true);

        return mav;
    }
}
