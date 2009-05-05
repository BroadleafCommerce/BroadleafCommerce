package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.service.OfferService;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OfferDataProvider;
import org.testng.annotations.Test;

public class OfferServiceTest extends BaseTest{

    @Resource
    OfferService offerService;

    @Resource
    OrderService orderService;

    @Resource
    CustomerService customerService;

    @Test(groups = {"applyOffersToOrder"}, dataProvider = "offerDataProvider",  dataProviderClass=OfferDataProvider.class, dependsOnGroups = {"findCurrentCartForCustomer","getItemsForOrder" })
    public void applyOffersToOrder(List<Offer> allOffers){
        //        String userName = "customer1";
        //        Customer customer = customerService.readCustomerByUsername(userName);
        //        Order order = orderService.findCartForCustomer(customer);
        //        order.setOrderItems(orderService.findItemsForOrder(order));
        //		offerService.applyOffersToOrder(allOffers, order);
        //offerService.applyOffersToOrderUsingEngine(allOffers, order);
    }
}
