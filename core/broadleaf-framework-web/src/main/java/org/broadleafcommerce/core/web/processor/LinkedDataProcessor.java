/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.linked.data.*;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.codehaus.jettison.json.JSONException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This processor replaces linkedData tags with metadata for search engine optimization. The
 * data is formatted to Schema.org and Google standards.
 *
 * @author Jacob Mitash
 */
@Component("blLinkedDataProcessor")
public class LinkedDataProcessor extends AbstractBroadleafTagReplacementProcessor
{
    private final Log LOG = LogFactory.getLog(LinkedDataProcessor.class);
    protected enum Destination { PRODUCT, CATEGORY, HOME, DEFAULT }

    @Resource(name = "blLinkedDataServiceFactory")
    protected LinkedDataServiceFactory linkedDataServiceFactory;

    @Override
    public BroadleafTemplateModel getReplacementModel(String s, Map<String, String> map, BroadleafTemplateContext context) {
        Destination destination = resolveDestination(context.getRequest());

        String linkedDataText = "<script type=\"application/ld+json\">\n" +
                getDataForDestination(context.getRequest(), destination) +
                "\n</script>";

        BroadleafTemplateModel model = context.createModel();
        BroadleafTemplateElement linkedData = context.createTextElement(linkedDataText);
        model.addElement(linkedData);

        return model;
    }

    /**
     * Find out which page the user has requested
     * @param request the user HttpServletRequest
     * @return the destination page type
     */
    protected Destination resolveDestination(HttpServletRequest request) {
        if(request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME) != null) {
            return Destination.PRODUCT;
        } else if(request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME) != null) {
            return Destination.CATEGORY;
        } else if(request.getRequestURI().equals("/")) {
            return Destination.HOME;
        } else {
            return Destination.DEFAULT;
        }
    }

    /**
     * Get the metadata for the specific page
     * @param request the user request
     * @param destination the type of page trying to be visited
     * @return the JSON string representation of the linked data
     */
    protected String getDataForDestination(HttpServletRequest request, Destination destination) {
        try {

            if(destination == Destination.PRODUCT) {
                Product product = (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
                return linkedDataServiceFactory
                        .productLinkedDataService(request.getRequestURL().toString(), product).getLinkedData();

            } else if(destination == Destination.CATEGORY) {
                Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
                List<CategoryProductXref> productXrefs = category.getActiveProductXrefs();
                List<Product> products = new ArrayList<>(productXrefs.size());
                for(CategoryProductXref productXref : productXrefs) {
                    products.add(productXref.getProduct());
                }
                return linkedDataServiceFactory
                        .categoryLinkedDataService(request.getRequestURL().toString(), products).getLinkedData();
            } else if(destination == Destination.HOME) {
                return linkedDataServiceFactory
                        .homepageLinkedDataService(request.getRequestURL().toString()).getLinkedData();
            } else {
                return linkedDataServiceFactory
                        .defaultLinkedDataService(request.getRequestURL().toString()).getLinkedData();
            }

        } catch (JSONException e) {
            LOG.error("A JSON exception occurred while generating LinkedData", e);
            return "";
        }
    }

    @Override
    public String getName() {
        return "linkedData";
    }
}
