package org.broadleafcommerce.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CatalogController extends AbstractController{
    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;
    private String defaultView;
    private String defaultProductView;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public String getDefaultView() {
        return defaultView;
    }

    public void setDefaultView(String defaultView) {
        this.defaultView = defaultView;
    }

    public String getDefaultProductView() {
        return defaultProductView;
    }

    public void setDefaultProductView(String defaultProductView) {
        this.defaultProductView = defaultProductView;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CommerceRequestStateImpl requestState = (CommerceRequestStateImpl) CommerceRequestStateImpl.getRequestState(request);

        String productId = request.getParameter("productId");

        if (requestState.getCategory() != null){

            if (productId != null){
                try {
                    Product item = catalogService.findProductById(new Long(productId));
                    if (item != null){
                        List<Sku> skus = item.getActiveSkus();

                        ProductSkus productSkus = new ProductSkus(item, skus);
                        Map<Object, Object> model = new HashMap<Object, Object>();
                        model.put("productSkus", productSkus);

                        return new ModelAndView(getDefaultProductView(), model);

                    }
                } catch (NumberFormatException e) {
                    logger.error("Unable to parse productId: " + e.getMessage());
                }
            } else{
                List<Category> subCategories = catalogService.findAllSubCategories(requestState.getCategory());

                Map<Object, Object> model = new HashMap<Object, Object>();
                model.put("subCategories", subCategories);
                model.put("category", requestState.getCategory());

                return new ModelAndView(getDefaultView(), model);
            }
        }

        return null;
    }
}
