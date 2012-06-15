package org.broadleafcommerce.core.web.catalog;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;

/**
 * This handler mapping works with the Category entity to determine if a category has been configured for
 * the passed in URL.   
 * 
 * If the URL matches a valid Category then this mapping returns the handler configured via the 
 * controllerName property or blCategoryController by default. 
 *
 * @author bpolster
 * @since 2.0
 * @see org.broadleafcommerce.core.catalog.domain.Product
 * @see CatalogService
 */
public class ProductHandlerMapping extends BLCAbstractHandlerMapping {
	
	private String controllerName="blProductController";
	
    @Resource(name = "blCatalogService")
    private CatalogService catalogService;
    
    public static final String CURRENT_PRODUCT_ATTRIBUTE_NAME = "currentProduct";

	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {		
		BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
		Product product = catalogService.findProductByURI(context.getRequestURIWithoutContext());
 
        if (product != null) {
            context.getRequest().setAttribute(CURRENT_PRODUCT_ATTRIBUTE_NAME, product);
        	return controllerName;
        } else {
        	return null;
        }
	}
}