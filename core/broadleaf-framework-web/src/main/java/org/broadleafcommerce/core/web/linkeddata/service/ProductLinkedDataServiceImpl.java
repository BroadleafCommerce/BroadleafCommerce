/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.web.linkeddata.service;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

/**
 * This service generates metadata unique to product pages. The metadata includes the product and it's relevant
 * information as well as reviews and ratings of the product.
 *
 * @author Jacob Mitash
 */
@Service(value = "blProductLinkedDataServiceImpl")
public class ProductLinkedDataServiceImpl extends DefaultLinkedDataServiceImpl {

    protected final static DateFormat ISO_8601_FORMAT = new SimpleDateFormat("YYYY-MM-DD");

    @Resource(name = "blRatingService")
    protected RatingService ratingService;

    @Override
    public Boolean canHandle(LinkedDataDestinationType destination) {
        return LinkedDataDestinationType.PRODUCT.equals(destination);
    }

    @Override
    protected JSONArray getLinkedDataJson(String url, List<Product> products) throws JSONException {
        JSONArray schemaObjects = new JSONArray();

        if (CollectionUtils.isNotEmpty(products)) {
            Product product = products.get(0);
            JSONObject productData = getProductLinkedData(product, url);
            addReviewData(product, productData);

            schemaObjects.put(productData);
            schemaObjects.put(getBreadcrumbList());
        }

        return schemaObjects;
    }


    protected JSONObject getProductLinkedData(Product product, String url) throws JSONException {
        JSONObject productData = new JSONObject();
        productData.put("@context", "http://schema.org");
        productData.put("@type", "Product");
        productData.put("name", product.getName());
        if(product.getMedia().size() > 0) {
            String imageUrl = product.getMedia().get("primary").getUrl();
            if(imageUrl == null) {
                imageUrl = product.getMedia().entrySet().iterator().next().getValue().getUrl();
            }
            productData.put("image", imageUrl);
        }
        productData.put("description", product.getLongDescription());
        productData.put("brand", product.getManufacturer());
        productData.put("url", url);
        productData.put("sku", product.getDefaultSku().getId());
        productData.put("category", product.getCategory().getName());

        JSONArray offers = new JSONArray();
        for (Sku sku : product.getAllSellableSkus()) {
            JSONObject offer = new JSONObject();
            offer.put("sku", sku.getId());
            offer.put("price", sku.getPriceData().getPrice().doubleValue());
            offer.put("priceCurrency", sku.getPriceData().getPrice().getCurrency().getCurrencyCode());
            if (sku.getActiveEndDate() != null) {
                offer.put("priceValidUntil", ISO_8601_FORMAT.format(sku.getActiveEndDate()));
            }

            boolean purchasable = false;
            if(sku.isActive()) {
                if(sku.getInventoryType() != null) {
                    if(sku.getInventoryType().equals(InventoryType.ALWAYS_AVAILABLE)) {
                        purchasable = true;
                    }
                    else if(sku.getInventoryType().equals(InventoryType.CHECK_QUANTITY)
                            && sku.getQuantityAvailable() != null
                            && sku.getQuantityAvailable() > 0) {
                        purchasable = true;
                    }
                } else {
                    purchasable = true;
                }
            }
            offer.put("availability", purchasable ? "InStock" : "OutOfStock");

            offer.put("url", url);
            offer.put("category", product.getCategory().getName());


            offers.put(offer);
        }

        productData.put("offers", offers);

        return productData;
    }

    protected void addReviewData(Product product, JSONObject productData) throws JSONException {
        RatingSummary ratingSummary = ratingService.readRatingSummary(product.getId().toString(), RatingType.PRODUCT);

        if (ratingSummary != null && ratingSummary.getNumberOfRatings() > 0) {
            JSONObject aggregateRating = new JSONObject();
            aggregateRating.put("ratingCount", ratingSummary.getNumberOfRatings());
            aggregateRating.put("ratingValue", ratingSummary.getAverageRating());

            productData.put("aggregateRating", aggregateRating);

            JSONArray reviews = new JSONArray();

            for(ReviewDetail reviewDetail : ratingSummary.getReviews()) {
                JSONObject review = new JSONObject();
                review.put("reviewBody", reviewDetail.getReviewText());
                review.put("reviewRating", new JSONObject().put("ratingValue", reviewDetail.getRatingDetail().getRating()));
                review.put("author", reviewDetail.getCustomer().getFirstName());
                review.put("datePublished", ISO_8601_FORMAT.format(reviewDetail.getReviewSubmittedDate()));
                reviews.put(review);
            }

            productData.put("review", reviews);
        }
    }
}
