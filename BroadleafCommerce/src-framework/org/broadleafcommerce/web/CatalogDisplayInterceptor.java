package org.broadleafcommerce.web;

import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

public class CatalogDisplayInterceptor extends HandlerInterceptorAdapter {
    protected final Log logger = LogFactory.getLog(getClass());
    private final UrlPathHelper pathHelper = new UrlPathHelper();
    private CatalogService catalogService;
    private String catalogPrefix;

    public void setCatalogPrefix(String catalogPrefix) {
        this.catalogPrefix = catalogPrefix;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        String path = pathHelper.getRequestUri(request).substring(pathHelper.getContextPath(request).length());

        CommerceRequestStateImpl requestState = CommerceRequestStateImpl.getRequestState(request);
        requestState.setCatalogPrefix(catalogPrefix);
        Category category = retrieveCategory(path);
        String productId = request.getParameter("productId");

        if (category != null){
            requestState.setCategory(category);
            if (productId != null){
                if (!isValidProduct(productId, category)){
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return false;
                }
            }
            if (StringUtils.isNotBlank(category.getUrl())){
                response.sendRedirect(category.getUrl());
                return false;
            }
        } else if(path.startsWith("/" + catalogPrefix)){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        return true;
    }

    private Category retrieveCategory(String path){
        String[] tokens = StringUtils.split(path, "/");
        Stack<String> catTokens = new Stack<String>();

        for (int i=0; i<tokens.length; i++){
            catTokens.push(tokens[i]);
        }

        Category category = catalogService.findCategoryByUrlKey(catTokens.peek());
        if (isValidCategory(catTokens, category, catalogPrefix)){
            return category;
        }
        return null;

    }

    private boolean isValidCategory(Stack<String> tokens, Category category, String ignoreToken){
        if (tokens.isEmpty()){
            return true;
        }

        String token = tokens.pop();
        boolean check;
        if (category != null && token.equals(category.getUrlKey())){
            check = isValidCategory(tokens, category.getDefaultParentCategory(), ignoreToken);
        } else {
            if (category == null && token.equals(ignoreToken)){
                return true;
            } else {
                return false;
            }
        }

        return check;
    }

    private boolean isValidProduct(String productId, Category category){
        try {
            Long id = new Long(productId);
            Product item = catalogService.findProductById(id);
            if (item == null){
                return false;
            }

            // TODO fix with new changes bradley
            //		   List<Product> products = category.getProducts();
            //		   if (products != null){
            //			   for (Iterator<Product> itr = products.iterator();itr.hasNext();){
            //				   if (id.equals(itr.next().getId())){
            //					   return true;
            //				   }
            //			   }

            return false;
            //		   }
        } catch(NumberFormatException e){
            logger.error("Unable to parse productId: " + e.getMessage());
        }

        return true;
    }
}
