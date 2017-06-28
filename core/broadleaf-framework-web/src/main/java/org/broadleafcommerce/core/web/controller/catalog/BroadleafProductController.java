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
package org.broadleafcommerce.core.web.controller.catalog;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.template.TemplateOverrideExtensionManager;
import org.broadleafcommerce.common.template.TemplateType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.TemplateTypeAware;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.common.web.deeplink.DeepLinkService;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.ConfigurableOrderItemRequest;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class works in combination with the ProductHandlerMapping which finds a product based upon
 * the passed in URL.
 *
 * @author bpolster
 */
public class BroadleafProductController extends BroadleafAbstractController implements Controller, TemplateTypeAware {

    public static final String PAGE_TYPE_ATTRIBUTE_NAME = "BLC_PAGE_TYPE";
    protected String defaultProductView = "catalog/product";
    protected static String MODEL_ATTRIBUTE_NAME = "product";
    protected static String CONFIGURATION_ATTRIBUTE_NAME = "configRequest";
    protected static String ALL_PRODUCTS_ATTRIBUTE_NAME = "blcAllDisplayedProducts";
    
    @Autowired(required = false)
    @Qualifier("blProductDeepLinkService")
    protected DeepLinkService<Product> deepLinkService;

    @Resource(name="blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blTemplateOverrideExtensionManager")
    protected TemplateOverrideExtensionManager templateOverrideManager;

    @Resource(name = "blRatingService")
    protected RatingService ratingService;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView model = new ModelAndView();
        Product product = (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
        assert (product != null);
        model.addObject(MODEL_ATTRIBUTE_NAME, product);
        model.addObject(PAGE_TYPE_ATTRIBUTE_NAME, "product");

        // Build the add to cart request and add it to the page
        ConfigurableOrderItemRequest itemRequest = orderItemService.createConfigurableOrderItemRequestFromProduct(product);
        orderItemService.modifyOrderItemRequest(itemRequest);
        model.addObject(CONFIGURATION_ATTRIBUTE_NAME, itemRequest);
        model.addObject(ALL_PRODUCTS_ATTRIBUTE_NAME, orderItemService.findAllProductsInRequest(itemRequest));




        DateFormat iso8601Format = new SimpleDateFormat("YYYY-MM-DD");

        JSONObject linkedData = new JSONObject();
        linkedData.put("@context", "http://schema.org");
        linkedData.put("@type", "Product");
        linkedData.put("name", product.getName());
        if(product.getMedia().size() > 0) {
            String imageUrl = product.getMedia().get("primary").getUrl();
            if(imageUrl == null) {
                imageUrl = product.getMedia().entrySet().iterator().next().getValue().getUrl();
            }
            linkedData.put("image", imageUrl);
        }
        linkedData.put("description", product.getLongDescription());
        linkedData.put("brand", product.getManufacturer());
        linkedData.put("url", request.getRequestURL().toString()); //TODO: verify
        linkedData.put("sku", product.getDefaultSku().getId()); //TODO: actual SKU
        linkedData.put("category", product.getCategory().getName());

        JSONArray offers = new JSONArray();
        for (Sku sku : product.getAllSellableSkus()) {
            JSONObject offer = new JSONObject();
            offer.put("sku", sku.getId()); //TODO: actual SKU
            offer.put("price", sku.getPriceData().getPrice().doubleValue());
            offer.put("priceCurrency", sku.getPriceData().getPrice().getCurrency().getCurrencyCode());
            if (sku.getActiveEndDate() != null) { //TODO: correct date?
                offer.put("priceValidUntil", iso8601Format.format(sku.getActiveEndDate()));
            }

            //TODO: verify correct
            boolean purchasable = false;
            if(sku.isActive()) {
                if(sku.getInventoryType() != null) {
                    if(sku.getInventoryType().equals(InventoryType.ALWAYS_AVAILABLE))
                    {
                        purchasable = true;
                    }
                    else if(sku.getInventoryType().equals(InventoryType.CHECK_QUANTITY) && sku.getQuantityAvailable() > 0)
                    {
                        purchasable = true;
                    }
                } else {
                    purchasable = true;
                }
            }
            offer.put("availability", purchasable ? "InStock" : "OutOfStock");

            offer.put("url", request.getRequestURL().toString()); //TODO: verify
            offer.put("category", product.getCategory().getName());


            offers.put(offer);
        }

        linkedData.put("offers", offers);

        RatingSummary ratingSummary = ratingService.readRatingSummary(product.getId().toString(), RatingType.PRODUCT);

        if (ratingSummary != null && ratingSummary.getNumberOfRatings() > 0) {
            JSONObject aggregateRating = new JSONObject();
            aggregateRating.put("ratingCount", ratingSummary.getNumberOfRatings());
            aggregateRating.put("ratingValue", ratingSummary.getAverageRating());

            linkedData.put("aggregateRating", aggregateRating);

            JSONArray reviews = new JSONArray();

            for(ReviewDetail reviewDetail : ratingSummary.getReviews()) {
                JSONObject review = new JSONObject();
                review.put("reviewBody", reviewDetail.getReviewText());
                review.put("reviewRating", new JSONObject().put("ratingValue", reviewDetail.getRatingDetail().getRating()));
                review.put("author", reviewDetail.getCustomer().getFirstName());
                review.put("datePublished", iso8601Format.format(reviewDetail.getReviewSubmittedDate()));
                reviews.put(review);
            }

            linkedData.put("review", reviews);
        }

        model.addObject("linkedData", linkedData.toString(2));


        addDeepLink(model, deepLinkService, product);
        
        String templatePath;
        // Use the products custom template if available
        if (StringUtils.isNotBlank(product.getDisplayTemplate())) {
            templatePath = product.getDisplayTemplate();
        } else {
            // Otherwise, use the controller default.
            templatePath = getDefaultProductView();
        }

        // Allow extension managers to override.
        ExtensionResultHolder<String> erh = new ExtensionResultHolder<String>();
        ExtensionResultStatusType extResult = templateOverrideManager.getProxy().getOverrideTemplate(erh, product);
        if (extResult != ExtensionResultStatusType.NOT_HANDLED) {
            templatePath = erh.getResult();
        }
        
        model.setViewName(templatePath);
        return model;
    }

    public String getDefaultProductView() {
        return defaultProductView;
    }

    public void setDefaultProductView(String defaultProductView) {
        this.defaultProductView = defaultProductView;
    }
    
    @Override
    public String getExpectedTemplateName(HttpServletRequest request) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null) {
            Product product = (Product) context.getRequest().getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
            if (product != null && product.getDisplayTemplate() != null) {
                return product.getDisplayTemplate();
            }
        }
        return getDefaultProductView();
    }

    @Override
    public TemplateType getTemplateType(HttpServletRequest request) {
        return TemplateType.PRODUCT;
    }

}
