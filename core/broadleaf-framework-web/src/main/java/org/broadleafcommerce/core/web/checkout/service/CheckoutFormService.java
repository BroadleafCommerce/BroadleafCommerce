package org.broadleafcommerce.core.web.checkout.service;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;

public interface CheckoutFormService {

    OrderInfoForm prePopulateOrderInfoForm(OrderInfoForm orderInfoForm, Order cart);

    ShippingInfoForm prePopulateShippingInfoForm(ShippingInfoForm shippingInfoForm, Order cart);

    BillingInfoForm prePopulateBillingInfoForm(BillingInfoForm billingInfoForm, Order cart);

}
