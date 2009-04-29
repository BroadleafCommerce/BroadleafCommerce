package org.broadleafcommerce.offer.test;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.offer.service.OfferService;
import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.Test;

public class OfferTest extends BaseTest {

    @Resource
    private OfferService offerService;

    @Test
    public void testBogoApplied() {
        Date yesterday = new Date(new Date().getTime() - (1000L * 60L * 60L * 24L));
        Date tomorrow = new Date(new Date().getTime() + (1000L * 60L * 60L * 24L));
        OrderImpl order = new OrderImpl();
        CategoryImpl category = new CategoryImpl();
        category.setName("t-shirt");
        OrderItemImpl orderItem = new OrderItemImpl();
        ProductImpl product = new ProductImpl();
        product.setDefaultCategory(category);
        orderItem.setProduct(product);
        orderItem.setRetailPrice(new Money(20D));
        orderItem.setSalePrice(new Money(20D));
        orderItem.setPrice(new Money(20D));
        orderItem.setQuantity(4);
        order.addOrderItem(orderItem);

        OfferImpl offer = new OfferImpl();
        offer.setDiscountType(OfferDiscountType.PERCENT_OFF);
        offer.setValue(new Money(20D)); //Wierd way to say 20% off
        offer.setPriority(1);
        offer.setCombinableWithOtherOffers(true);
        offer.setAppliesToCustomerRules(null);
        offer.setType(OfferType.ORDER_ITEM);
        offer.setStartDate(yesterday);
        offer.setEndDate(tomorrow);
        offer.setAppliesToItemRules("");

        ArrayList<Offer> offers = new ArrayList<Offer>();
        offers.add(offer);

        try {
            offerService.applyOffersToOrder(offers, order);

            for (OrderItem item : order.getOrderItems()) {
                System.out.println("Candidates that were applied: " + item.getCandidateItemOffers().size());
            }
        } catch (PricingException e) {
            e.printStackTrace();
        }
    }
}
