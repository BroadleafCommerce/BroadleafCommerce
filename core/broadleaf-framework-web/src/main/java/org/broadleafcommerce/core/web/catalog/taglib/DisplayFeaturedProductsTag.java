/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.catalog.taglib;

import org.broadleafcommerce.core.catalog.domain.Product;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DisplayFeaturedProductsTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    private List<Product> products;
    private String maxFeatures;
    private String var;
     
    @Override
    public int doStartTag() throws JspException {
        List<Product> featuredProducts = getFeaturedProducts(products);
        if (maxFeatures != null && !"".equals(maxFeatures)) {
            featuredProducts = featuredProducts.subList(0, (getMaxFeatureCount(maxFeatures, featuredProducts.size())));
        }
        super.pageContext.setAttribute(var, featuredProducts);
        
        return EVAL_BODY_INCLUDE;
    }
    
    private List<Product> getFeaturedProducts(List<Product> products) {
        List<Product> featuredProducts = new ArrayList<Product>();
        if (products != null) {
            Iterator<Product> i = products.iterator();
            
            while (i.hasNext()) {
                Product p = i.next();
                if (p.isFeaturedProduct()) {
                    featuredProducts.add(p);
                }
            }
        }       
        return featuredProducts;
    }
    
    private int getMaxFeatureCount(String count, int max) {
        if ((count != null) || (!"".equals(count))) {
            try {
                if (Integer.parseInt(count) < max) {
                    return Integer.parseInt(count);
                }
            }
            catch(Exception e) {}
        }
        
        return max;
    }
    
    public String getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(String maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }    
}
