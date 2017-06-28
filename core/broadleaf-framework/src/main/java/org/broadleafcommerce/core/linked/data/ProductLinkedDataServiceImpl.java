package org.broadleafcommerce.core.linked.data;

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

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by jacobmitash on 6/28/17.
 */
@Service("blProductLinkedDataService")
public class ProductLinkedDataServiceImpl implements ProductLinkedDataService {

    @Resource(name = "blRatingService")
    protected RatingService ratingService;

    @Override
    public String getLinkedData(Product product, String url) throws JSONException {
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
        linkedData.put("url", url); //TODO: verify
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

            offer.put("url", url); //TODO: verify
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

        return linkedData.toString();
    }
}
