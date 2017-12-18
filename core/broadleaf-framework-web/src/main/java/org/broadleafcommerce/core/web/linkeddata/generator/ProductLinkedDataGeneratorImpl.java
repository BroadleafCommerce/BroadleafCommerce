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
package org.broadleafcommerce.core.web.linkeddata.generator;

import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This generator generates structured data specific to product pages.
 * <p>
 * See <a href="http://schema.org/Product" target="_blank">http://schema.org/Product</a>, 
 * <a href="http://schema.org/Offer" target="_blank">http://schema.org/Offer</a>, 
 * and <a href="http://schema.org/AggregateOffer" target="_blank">http://schema.org/AggregateOffer</a>
 *
 * @author Jacob Mitash
 * @author Nathan Moore (nathanmoore).
 */
@Service(value = "blProductLinkedDataGenerator")
public class ProductLinkedDataGeneratorImpl extends AbstractLinkedDataGenerator {
    protected static final String IN_STOCK = "InStock";
    protected static final String OUT_OF_STOCK = "OutOfStock";
    protected final static DateFormat ISO_8601_FORMAT = new SimpleDateFormat("YYYY-MM-DD");

    @Resource(name = "blRatingService")
    protected RatingService ratingService;

    @Override 
    public boolean canHandle(final HttpServletRequest request) {
        return request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME) != null;
    }

    @Override
    protected JSONArray getLinkedDataJsonInternal(final String url, final HttpServletRequest request,
                                                  final JSONArray schemaObjects) throws JSONException {
        final Product product = getProduct(request);

        if (product != null) {
            final JSONObject productData = addProductData(request, product, url);
            addReviewData(request, product, productData);
            
            schemaObjects.put(productData);
        }

        return schemaObjects;
    }
    
    protected Product getProduct(final HttpServletRequest request) {
        return (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
    }
    
    protected JSONObject addProductData(final HttpServletRequest request, final Product product, final String url) 
            throws JSONException {
        final JSONObject productData = new JSONObject();
        productData.put("@context", DEFAULT_STRUCTURED_CONTENT_CONTEXT);
        productData.put("@type", "Product");
        productData.put("name", product.getName());

        addImageUrl(product, productData);
        
        productData.put("description", product.getLongDescription());
        productData.put("brand", product.getManufacturer());
        productData.put("url", url);
        productData.put("sku", product.getDefaultSku().getId());
        productData.put("category", product.getCategory().getName());
        
        addSkus(request, product, productData, url);

        extensionManager.getProxy().addProductData(request, product, productData);

        return productData;
    }
    
    protected void addImageUrl(final Product product, final JSONObject productData) throws JSONException {
        final Map<String, Media> media = product.getMedia();
        
        if (media.size() > 0) {
            final String url;
            final String urlPrefix = getImageUrlPrefix();

            if (media.get("primary") != null) {
                url = media.get("primary").getUrl();
            } else {
                url = media.entrySet().iterator().next().getValue().getUrl();
            }
            
            if (urlPrefix.contains("/cmsstatic/") && url.contains("/cmsstatic/")) {
                productData.put("image", url.replace("/cmsstatic/", urlPrefix));
            } else {
                productData.put("image", urlPrefix + url);
            }
        }
    }

    protected String getImageUrlPrefix() {
        String prefix = getAssetServerUrlPrefix();

        if (prefix == null) {
            baseUrlResolver.getSiteBaseUrl();
        }

        return prefix;
    }

    protected String getAssetServerUrlPrefix() {
        final boolean isSecure = environment.getProperty("site.baseurl.secure", boolean.class, false);
        final String prefix;

        if (isSecure) {
            prefix = environment.getProperty("asset.server.url.prefix.secure");
        } else {
            prefix = environment.getProperty("asset.server.url.prefix");
        }

        return prefix;
    }
    
    protected void addSkus(final HttpServletRequest request, final Product product, final JSONObject productData, final String url) 
            throws JSONException {
        final JSONArray offers = new JSONArray();
        final String currency = product.getRetailPrice().getCurrency().getCurrencyCode();
        BigDecimal highPrice = BigDecimal.ZERO;
        BigDecimal lowPrice = null;

        for (final Sku sku : product.getAllSellableSkus()) {
            final JSONObject offer = new JSONObject();
            offer.put("@type", "Offer");
            offer.put("sku", sku.getId());
            offer.put("priceCurrency", currency);
            offer.put("availability", determineAvailability(sku));
            offer.put("url", url);
            offer.put("category", product.getCategory().getName());

            if (sku.getActiveEndDate() != null) {
                offer.put("priceValidUntil", ISO_8601_FORMAT.format(sku.getActiveEndDate()));
            }

            final Money price = sku.getPriceData().getPrice();
            offer.put("price", price.getAmount());

            if (price.greaterThan(highPrice)) {
                highPrice = price.getAmount();
            }

            if (lowPrice == null || price.lessThan(lowPrice)) {
                lowPrice = price.getAmount();
            }

            extensionManager.getProxy().addSkuData(request, product, offer);

            offers.put(offer);
        }

        // use aggregateOffer to handle multiple sellable SKUs for a single product
        if (offers.length() > 1) {
            final JSONObject aggregateOffer = new JSONObject();
            aggregateOffer.put("@type", "AggregateOffer");
            aggregateOffer.put("highPrice", highPrice.doubleValue());
            aggregateOffer.put("lowPrice", lowPrice.doubleValue());
            aggregateOffer.put("priceCurrency", currency);
            aggregateOffer.put("offerCount", offers.length());
            aggregateOffer.put("offers", offers);

            extensionManager.getProxy().addAggregateSkuData(request, product, aggregateOffer);

            productData.put("offers", aggregateOffer);
        } else {
            productData.put("offers", offers);
        }
    }

    protected String determineAvailability(final Sku sku) {
        boolean purchasable = false;
        
        if (sku.isActive()) {
            if (sku.getInventoryType() != null) {
                if (sku.getInventoryType().equals(InventoryType.ALWAYS_AVAILABLE)) {
                    purchasable = true;
                } else if (sku.getInventoryType().equals(InventoryType.CHECK_QUANTITY)
                           && sku.getQuantityAvailable() != null && sku.getQuantityAvailable() > 0) {
                    purchasable = true;
                }
            } else {
                purchasable = true;
            }
        }
        
        return purchasable ? IN_STOCK : OUT_OF_STOCK;
    }

    protected void addReviewData(final HttpServletRequest request, final Product product, final JSONObject productData) throws JSONException {
        final RatingSummary ratingSummary = ratingService.readRatingSummary(product.getId().toString(), RatingType.PRODUCT);

        if (ratingSummary != null && ratingSummary.getNumberOfRatings() > 0) {
            final JSONObject aggregateRating = new JSONObject();
            aggregateRating.put("ratingCount", ratingSummary.getNumberOfRatings());
            aggregateRating.put("ratingValue", ratingSummary.getAverageRating());

            extensionManager.getProxy().addAggregateReviewData(request, product, aggregateRating);
            
            productData.put("aggregateRating", aggregateRating);

            final JSONArray reviews = new JSONArray();

            for (final ReviewDetail reviewDetail : ratingSummary.getReviews()) {
                final JSONObject review = new JSONObject();
                review.put("reviewBody", reviewDetail.getReviewText());
                review.put("reviewRating", new JSONObject().put("ratingValue", reviewDetail.getRatingDetail().getRating()));
                review.put("author", reviewDetail.getCustomer().getFirstName());
                review.put("datePublished", ISO_8601_FORMAT.format(reviewDetail.getReviewSubmittedDate()));

                extensionManager.getProxy().addReviewData(request, product, review);
                
                reviews.put(review);
            }

            productData.put("review", reviews);
        }
    }
}
