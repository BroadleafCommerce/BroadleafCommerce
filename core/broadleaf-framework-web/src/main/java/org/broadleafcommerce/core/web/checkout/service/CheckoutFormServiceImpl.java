/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.checkout.service;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.order.service.CartStateService;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blCheckoutFormService")
public class CheckoutFormServiceImpl implements CheckoutFormService {

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blCustomerPaymentService")
    protected CustomerPaymentService customerPaymentService;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Resource(name = "blCartStateService")
    protected CartStateService cartStateService;

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
    public BillingInfoForm prePopulateBillingInfoForm(BillingInfoForm billingInfoForm, ShippingInfoForm shippingInfoForm, Order cart) {
        Address orderPaymentBillingAddress = getAddressFromCCOrderPayment(cart);
        billingInfoForm.setAddress(orderPaymentBillingAddress);

        boolean shippingAddressUsedForBilling = addressesContentsAreEqual(shippingInfoForm.getAddress(), billingInfoForm.getAddress());
        billingInfoForm.setUseShippingAddress(shippingAddressUsedForBilling);

        return billingInfoForm;
    }

    @Override
    public PaymentInfoForm prePopulatePaymentInfoForm(PaymentInfoForm paymentInfoForm, ShippingInfoForm shippingInfoForm, Order cart) {
        Customer customer = CustomerState.getCustomer();
        String emailAddress = getKnownEmailAddress(cart, customer);
        paymentInfoForm.setEmailAddress(emailAddress);

        Address orderPaymentBillingAddress = getAddressFromCCOrderPayment(cart);
        if (orderPaymentBillingAddress != null) {
            paymentInfoForm.setAddress(orderPaymentBillingAddress);
        }

        CustomerPayment customerPaymentUsedForOrder = getCustomerPaymentUsedForOrder();
        Long customerPaymentId = (customerPaymentUsedForOrder == null) ? null : customerPaymentUsedForOrder.getId();
        paymentInfoForm.setCustomerPaymentId(customerPaymentId);


        boolean shouldUseCustomerPaymentDefaultValue = getShouldUseCustomerPaymentDefaultValue(customerPaymentUsedForOrder);
        paymentInfoForm.setShouldUseCustomerPayment(shouldUseCustomerPaymentDefaultValue);

        boolean shouldUseShippingAddressDefaultValue = getShouldUseShippingAddressDefaultValue(customerPaymentUsedForOrder, paymentInfoForm, shippingInfoForm);
        paymentInfoForm.setShouldUseShippingAddress(shouldUseShippingAddressDefaultValue);

        boolean shouldSaveNewPaymentDefaultValue = getShouldSaveNewPaymentDefaultValue();
        paymentInfoForm.setShouldSaveNewPayment(shouldSaveNewPaymentDefaultValue);

        return paymentInfoForm;
    }

    protected String getKnownEmailAddress(Order cart, Customer customer) {
        String emailAddress = null;

        if (cart.getEmailAddress() != null) {
            emailAddress = cart.getEmailAddress();
        } else if (customer != null && customer.getEmailAddress() != null) {
            emailAddress = customer.getEmailAddress();
        }

        return emailAddress;
    }

    protected Address getAddressFromCCOrderPayment(Order cart) {
        List<OrderPayment> orderPayments = orderPaymentService.readPaymentsForOrder(cart);

        for (OrderPayment payment : CollectionUtils.emptyIfNull(orderPayments)) {
            boolean isCreditCardPaymentType = PaymentType.CREDIT_CARD.equals(payment.getType());
            boolean paymentHasBillingAddress = (payment.getBillingAddress() != null);

            if (payment.isActive() && isCreditCardPaymentType && paymentHasBillingAddress) {
                return payment.getBillingAddress();
            }
        }

        return null;
    }

    protected CustomerPayment getCustomerPaymentUsedForOrder() {
        Customer customer = CustomerState.getCustomer();

        List<CustomerPayment> customerPayments = customerPaymentService.readCustomerPaymentsByCustomerId(customer.getId());
        for (CustomerPayment customerPayment : customerPayments) {
            if (cartStateService.cartHasCreditCardPaymentWithSameToken(customerPayment.getPaymentToken())) {
                return customerPayment;
            }
        }
        return null;
    }

    /**
     * A temporary credit card {@link OrderPayment} will only be added to the cart if the customer has opted out
     *  of saving their credit card for future payments.
     * @param customerPaymentUsedForOrder
     */
    protected boolean getShouldUseCustomerPaymentDefaultValue(CustomerPayment customerPaymentUsedForOrder) {
        boolean orderUsingCustomerPayment = (customerPaymentUsedForOrder != null);
        boolean cartHasTemporaryCreditCard = cartStateService.cartHasTemporaryCreditCard();

        return orderUsingCustomerPayment && !cartHasTemporaryCreditCard;
    }

    /**
     * A temporary credit card {@link OrderPayment} will only be added to the cart if the customer has opted out
     *  of saving their credit card for future payments.
     */
    protected boolean getShouldSaveNewPaymentDefaultValue() {
        boolean customerOptedOutOfSavingCard = !cartStateService.cartHasTemporaryCreditCard();

        return customerOptedOutOfSavingCard;
    }

    /**
     * A temporary credit card {@link OrderPayment} will only be added to the cart if the customer has opted out
     *  of saving their credit card for future payments.
     * @param customerPaymentUsedForOrder
     * @param paymentInfoForm
     * @param shippingInfoForm
     */
    protected boolean getShouldUseShippingAddressDefaultValue(CustomerPayment customerPaymentUsedForOrder, PaymentInfoForm paymentInfoForm,
            ShippingInfoForm shippingInfoForm) {
        boolean orderIsNotUsingCustomerPayment = (customerPaymentUsedForOrder == null);
        boolean shippingAddressEqualToBillingAddress = addressesContentsAreEqual(paymentInfoForm.getAddress(), shippingInfoForm.getAddress());

        return orderIsNotUsingCustomerPayment && shippingAddressEqualToBillingAddress;
    }

    @Override
    public void prePopulateInfoForms(ShippingInfoForm shippingInfoForm, PaymentInfoForm paymentInfoForm) {
        Order cart = CartState.getCart();
        prePopulateShippingInfoForm(shippingInfoForm, cart);
        prePopulatePaymentInfoForm(paymentInfoForm, shippingInfoForm, cart);
    }

    @Override
    public void determineIfSavedAddressIsSelected(Model model, ShippingInfoForm shippingInfoForm, PaymentInfoForm paymentInfoForm) {
        Customer customer = CustomerState.getCustomer();
        boolean isSavedShippingAddress = false;
        boolean isSavedBillingAddress = false;

        for (CustomerAddress customerAddress : customer.getCustomerAddresses()) {
            if (addressesContentsAreEqual(shippingInfoForm.getAddress(), customerAddress.getAddress())) {
                isSavedShippingAddress = true;
                break;
            }
        }

        for (CustomerAddress customerAddress : customer.getCustomerAddresses()) {
            if (addressesContentsAreEqual(paymentInfoForm.getAddress(), customerAddress.getAddress())) {
                isSavedBillingAddress = true;
                break;
            }
        }

        model.addAttribute("isSavedShippingAddress", isSavedShippingAddress);
        model.addAttribute("isSavedBillingAddress", isSavedBillingAddress);
    }

    protected boolean addressesContentsAreEqual(Address address1, Address address2) {
        return Objects.equals(address2.getAddressLine1(), address1.getAddressLine1()) &&
                Objects.equals(address2.getAddressLine2(), address1.getAddressLine2()) &&
                Objects.equals(address2.getCity(), address1.getCity()) &&
                Objects.equals(address2.getStateProvinceRegion(), address1.getStateProvinceRegion()) &&
                Objects.equals(address2.getPostalCode(), address1.getPostalCode()) &&
                Objects.equals(address2.getIsoCountryAlpha2(), address1.getIsoCountryAlpha2()) &&
                Objects.equals(address2.getIsoCountrySubdivision(), address1.getIsoCountrySubdivision());
    }
}
