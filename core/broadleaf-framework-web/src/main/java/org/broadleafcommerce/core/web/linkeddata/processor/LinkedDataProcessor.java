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
package org.broadleafcommerce.core.web.linkeddata.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCArrayUtils;
import org.broadleafcommerce.common.util.TypedTransformer;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.web.linkeddata.service.LinkedDataDestinationType;
import org.broadleafcommerce.core.web.linkeddata.service.LinkedDataService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This processor replaces linkedData tags with metadata for search engine optimization. The
 * data is formatted to Schema.org and Google standards.
 *
 * @author Jacob Mitash
 */
@Component("blLinkedDataProcessor")
public class LinkedDataProcessor extends AbstractBroadleafTagReplacementProcessor {
    private final Log LOG = LogFactory.getLog(LinkedDataProcessor.class);

    @Autowired
    protected List<LinkedDataService> linkedDataServices;

    @Override
    public BroadleafTemplateModel getReplacementModel(String s, Map<String, String> map, BroadleafTemplateContext context) {
        LinkedDataDestinationType destination = resolveDestination(context.getRequest());

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
    protected LinkedDataDestinationType resolveDestination(HttpServletRequest request) {
        if(request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME) != null) {
            return LinkedDataDestinationType.PRODUCT;
        } else if(request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME) != null) {
            return LinkedDataDestinationType.CATEGORY;
        } else if(request.getRequestURI().equals("/")) {
            return LinkedDataDestinationType.HOME;
        } else {
            return LinkedDataDestinationType.DEFAULT;
        }
    }

    /**
     * Get the metadata for the specific page
     * @param request the user request
     * @param destination the type of page trying to be visited
     * @return the JSON string representation of the linked data
     */
    protected String getDataForDestination(HttpServletRequest request, LinkedDataDestinationType destination) {
        try {
            List<Product> products = new ArrayList<>();
            String requestUrl = request.getRequestURL().toString();

            if(LinkedDataDestinationType.PRODUCT.equals(destination)) {
                Product product = (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
                products.add(product);
            } else if(LinkedDataDestinationType.CATEGORY.equals(destination)) {
                Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
                products = BLCArrayUtils.collect(category.getActiveProductXrefs().toArray(), new TypedTransformer<Product>() {
                    @Override
                    public Product transform(Object input) {
                        return ((CategoryProductXref) input).getProduct();
                    }
                });
            }

            for (LinkedDataService linkedDataService : linkedDataServices) {
                if (linkedDataService.canHandle(destination)) {
                    return linkedDataService.getLinkedData(requestUrl, products);
                }
            }
        } catch (JSONException e) {
            LOG.error("A JSON exception occurred while generating LinkedData", e);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getName() {
        return "linkedData";
    }
}
