package org.broadleafcommerce.checkout.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.checkout.service.CheckoutService;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.order.domain.PaymentInfoImpl;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfoImpl;
import org.broadleafcommerce.payment.secure.domain.Referenced;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    @Resource(name="checkoutService")
    private CheckoutService checkoutService;

    @Test
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
        payment.setAmount(new Money(15D + (15D * 0.05D)));
        payment.setReferenceNumber("1234");

        CreditCardPaymentInfo cc = new CreditCardPaymentInfoImpl();
        cc.setExpirationMonth(11);
        cc.setExpirationYear(2011);
        cc.setPan(1111111111111111L);
        cc.setReferenceNumber("1234");

        Map<PaymentInfo, Referenced> map = new HashMap<PaymentInfo, Referenced>();
        map.put(payment, cc);

        checkoutService.performCheckout(order, map);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
        assert (order.getTotalTax().equals(order.getSubTotal().multiply(0.05D)));
        assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax())));
    }
}
