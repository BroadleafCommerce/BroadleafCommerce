package org.broadleafcommerce.checkout.test;

import javax.annotation.Resource;

import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.workflow.Processor;

public class CheckoutTest extends BaseTest {

    @Resource(name="checkoutWorkflow")
    private Processor checkoutWorkflow;

    /*@Test
    public void testCheckout() throws Exception {
        Order order = new OrderImpl();
        FulfillmentGroup group = new FulfillmentGroupImpl();
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        groups.add(group);
        order.setFulfillmentGroups(groups);
        Money total = new Money(5D);
        group.setPrice(total);

        OrderItem item = new OrderItemImpl();
        item.setPrice(new Money(10D));
        item.setQuantity(1);
        List<OrderItem> items = new ArrayList<OrderItem>();
        items.add(item);
        order.setOrderItems(items);

        order.setTotalShipping(new Money(0D));

        PaymentInfo payment = new PaymentInfoImpl();
        Address address = new AddressImpl();
        address.setAddressLine1("123 Test Rd");
        address.setCity("Dallas");
        address.setFirstName("Jeff");
        address.setLastName("Fischer");
        address.setPostalCode("75240");
        address.setPrimaryPhone("972-978-9067");
        State state = new StateImpl();
        state.setAbbreviation("TX");
        address.setState(state);
        payment.setAddress(address);
        payment.setAmount

        CheckoutSeed seed = new CheckoutSeed(order, null);

        checkoutWorkflow.doActivities(seed);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
        assert (order.getTotalTax().equals(order.getSubTotal().multiply(0.05D)));
        assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax())));
    }*/
}
