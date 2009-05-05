package org.broadleafcommerce.pricing.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.pricing.service.PricingService;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.Test;

public class PricingTest extends BaseTest {

    @Resource(name="pricingService")
    private PricingService pricingService;

    @Resource
    private OrderDao orderDao;

    @Test
    public void testPricing() throws Exception {
        Order order = orderDao.create();
        FulfillmentGroup group = new FulfillmentGroupImpl();
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        groups.add(group);
        order.setFulfillmentGroups(groups);
        Money total = new Money(5D);
        group.setPrice(total);

        OrderItem item = new DiscreteOrderItemImpl();
        item.setPrice(new Money(10D));
        item.setQuantity(1);
        List<OrderItem> items = new ArrayList<OrderItem>();
        items.add(item);
        order.setOrderItems(items);

        order.setTotalShipping(new Money(0D));

        order = pricingService.executePricing(order);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
        assert (order.getTotalTax().equals(order.getSubTotal().multiply(0.05D)));
        assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax()).add(order.getTotalShipping())));
    }

    @Test
    public void testShipping() throws Exception {
        Order order = orderDao.create();
        FulfillmentGroup group1 = new FulfillmentGroupImpl();
        FulfillmentGroup group2 = new FulfillmentGroupImpl();

        // setup group1 - standard
        group1.setMethod("standard");
        Address address = new AddressImpl();
        State state = new StateImpl();
        state.setAbbreviation("hi");
        address.setState(state);
        group1.setAddress(address);

        // setup group2 - truck
        group2.setMethod("truck");

        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        groups.add(group1);
        //groups.add(group2);
        order.setFulfillmentGroups(groups);
        Money total = new Money(5D);
        group1.setPrice(total);
        group2.setPrice(total);
        order.setSubTotal(total);
        order.setTotal(total);

        OrderItem item = new DiscreteOrderItemImpl();
        item.setPrice(new Money(10D));
        item.setQuantity(1);
        List<OrderItem> items = new ArrayList<OrderItem>();
        items.add(item);
        order.setOrderItems(items);

        order.setTotalShipping(new Money(0D));

        order = pricingService.executePricing(order);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
        assert (order.getTotalTax().equals(order.getSubTotal().multiply(0.05D)));
        assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax())));
    }
}
