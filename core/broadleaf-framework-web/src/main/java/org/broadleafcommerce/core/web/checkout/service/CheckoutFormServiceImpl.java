package org.broadleafcommerce.core.web.checkout.service;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service("blCheckoutFormService")
public class CheckoutFormServiceImpl implements CheckoutFormService {

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Override
    public OrderInfoForm prePopulateOrderInfoForm(OrderInfoForm orderInfoForm, Order cart) {
        orderInfoForm.setEmailAddress(cart.getEmailAddress());

        return orderInfoForm;
    }

    @Override
    public ShippingInfoForm prePopulateShippingInfoForm(ShippingInfoForm shippingInfoForm, Order cart) {
        FulfillmentGroup firstShippableFulfillmentGroup = fulfillmentGroupService.getFirstShippableFulfillmentGroup(cart);

        if (firstShippableFulfillmentGroup != null) {
            //if the cart has already has fulfillment information
            if (firstShippableFulfillmentGroup.getAddress() != null) {
                shippingInfoForm.setAddress(firstShippableFulfillmentGroup.getAddress());
            } else {
                //check for a default address for the customer
                CustomerAddress defaultAddress = customerAddressService.findDefaultCustomerAddress(CustomerState.getCustomer().getId());
                if (defaultAddress != null) {
                    shippingInfoForm.setAddress(defaultAddress.getAddress());
                    shippingInfoForm.setAddressName(defaultAddress.getAddressName());
                }
            }

            FulfillmentOption fulfillmentOption = firstShippableFulfillmentGroup.getFulfillmentOption();
            if (fulfillmentOption != null) {
                shippingInfoForm.setFulfillmentOption(fulfillmentOption);
                shippingInfoForm.setFulfillmentOptionId(fulfillmentOption.getId());
            }
        }

        return shippingInfoForm;
    }

    @Override
    public BillingInfoForm prePopulateBillingInfoForm(BillingInfoForm billingInfoForm, Order cart) {
        billingInfoForm.setEmailAddress(cart.getEmailAddress());

        List<OrderPayment> orderPayments = cart.getPayments();
        for (OrderPayment payment : CollectionUtils.emptyIfNull(orderPayments)) {
            boolean isCreditCardPaymentType = PaymentType.CREDIT_CARD.equals(payment.getType());
            boolean paymentHasBillingAddress = (payment.getBillingAddress() != null);

            if (isCreditCardPaymentType && paymentHasBillingAddress) {
                billingInfoForm.setAddress(payment.getBillingAddress());
            }
        }

        return billingInfoForm;
    }
}
