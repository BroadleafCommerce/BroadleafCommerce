/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.controller.checkout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;
import org.broadleafcommerce.core.checkout.service.gateway.PassthroughPaymentConstants;
import org.broadleafcommerce.core.offer.service.exception.OfferExpiredException;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.exception.IllegalCartOperationException;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.pricing.service.workflow.OfferActivity;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * In charge of performing the various checkout operations
 * 
 * @author Andre Azzolini (apazzolini)
 * @author Elbert Bautista (elbertbautista)
 * @author Joshua Skorton (jskorton)
 */
public class BroadleafCheckoutController extends AbstractCheckoutController {

    private static final Log LOG = LogFactory.getLog(BroadleafCheckoutController.class);
    protected static String baseConfirmationRedirect = "redirect:/confirmation";

    /**
     * Renders the default checkout page and allows modules to add variables to the model.
     *
     * @param request
     * @param response
     * @param model
     * @param model
     * @return the checkout view path
     */
    public String checkout(HttpServletRequest request, HttpServletResponse response, Model model,
            RedirectAttributes redirectAttributes) {
        if (CustomerState.getCustomer().getId() != null) {
            preValidateCartOperation(model);
            populateModelWithReferenceData(request, model);
        }
        return getCheckoutView();
    }

    protected void preValidateCartOperation(Model model) {
        try {
            Order cart = CartState.getCart();
            orderService.preValidateCartOperation(cart);
        } catch (IllegalCartOperationException ex) {
            model.addAttribute("cartRequiresLock", true);
        }
    }

    /**
     * Renders checkout stages partial at the requested stage
     *
     * @param request
     * @param response
     * @param model
     * @param stage
     * @return the checkout stages partial path
     */
    public String getCheckoutStagePartial(HttpServletRequest request, HttpServletResponse response, Model model,
            String stage, RedirectAttributes redirectAttributes) {
        model.addAttribute(ACTIVE_STAGE, stage);
        return getCheckoutStagesPartial();
    }

    /**
     * Attempts to attach the user's email to the order so that they may proceed anonymously
     * @param request
     * @param model
     * @param orderInfoForm
     * @param result
     * @return
     * @throws ServiceException
     */
    public String saveGlobalOrderDetails(HttpServletRequest request, Model model, 
            OrderInfoForm orderInfoForm, BindingResult result) throws ServiceException {
        Order cart = CartState.getCart();

        orderInfoFormValidator.validate(orderInfoForm, result);
        if (result.hasErrors()) {
            // We need to clear the email on error in case they are trying to edit it
            try {
                cart.setEmailAddress(null);
                orderService.save(cart, false);
            } catch (PricingException pe) {
                LOG.error("Error when saving the email address for order confirmation to the cart", pe);
            }
            
            populateModelWithReferenceData(request, model);
            return getCheckoutView();
        }
        
        try {
            cart.setEmailAddress(orderInfoForm.getEmailAddress());
            orderService.save(cart, false);
        } catch (PricingException pe) {
            LOG.error("Error when saving the email address for order confirmation to the cart", pe);
        }
        
        return getCheckoutPageRedirect();   
    }

    /**
     * Creates a pass-through payment of the PaymentType passed in with
     * an amount equal to the order total after any non-final applied payments.
     * (for example gift cards, customer credit, or third party accounts)
     *
     * This intended to be used in cases like COD and other Payment Types where implementations wish
     * to just checkout without having to do any payment processing.
     *
     * This default implementations assumes that the pass-through payment is the only
     * "final" payment, as this will remove any payments that are not PaymentTransactionType.UNCONFIRMED
     * That means that it will look at all transactions on the order payment and see if it has unconfirmed transactions.
     * If it does, it will not remove it.
     *
     * Make sure not to expose this method in your extended Controller if you do not wish to
     * have this feature enabled.
     *
     * @param redirectAttributes
     * @param paymentType
     * @return
     * @throws PaymentException
     * @throws PricingException
     */
    public String processPassthroughCheckout(final RedirectAttributes redirectAttributes,
                                             PaymentType paymentType) throws PaymentException, PricingException {
        Order cart = CartState.getCart();

        //Invalidate any payments already on the order that do not have transactions on them that are UNCONFIRMED
        List<OrderPayment> paymentsToInvalidate = new ArrayList<OrderPayment>();
        for (OrderPayment payment : cart.getPayments()) {
            if (payment.isActive()) {
                if (payment.getTransactions() == null || payment.getTransactions().isEmpty()) {
                    paymentsToInvalidate.add(payment);
                } else {
                    for (PaymentTransaction transaction : payment.getTransactions()) {
                        if (!PaymentTransactionType.UNCONFIRMED.equals(transaction.getType())) {
                             paymentsToInvalidate.add(payment);
                        }
                    }
                }
            }
        }

        for (OrderPayment payment : paymentsToInvalidate) {
            cart.getPayments().remove(payment);
            if (paymentGatewayCheckoutService != null) {
                paymentGatewayCheckoutService.markPaymentAsInvalid(payment.getId());
            }
        }

        //Create a new Order Payment of the passed in type
        OrderPayment passthroughPayment = orderPaymentService.create();
        passthroughPayment.setType(paymentType);
        passthroughPayment.setPaymentGatewayType(PaymentGatewayType.PASSTHROUGH);
        passthroughPayment.setAmount(cart.getTotalAfterAppliedPayments());
        passthroughPayment.setOrder(cart);

        // Create the transaction for the payment
        PaymentTransaction transaction = orderPaymentService.createTransaction();
        transaction.setAmount(cart.getTotalAfterAppliedPayments());
        transaction.setRawResponse("Passthrough Payment");
        transaction.setSuccess(true);
        transaction.setType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
        transaction.getAdditionalFields().put(PassthroughPaymentConstants.PASSTHROUGH_PAYMENT_TYPE, paymentType.getType());

        transaction.setOrderPayment(passthroughPayment);
        passthroughPayment.addTransaction(transaction);
        orderService.addPaymentToOrder(cart, passthroughPayment, null);

        orderService.save(cart, true);

        return processCompleteCheckoutOrderFinalized(redirectAttributes);
    }

    /**
     * If the order has been finalized. i.e. all the payments have been applied to the order,
     * then you can go ahead and call checkout using the passed in order id.
     * This is usually called from a Review Page or if enough Payments have been applied to the Order to complete checkout.
     * (e.g. Gift Cards cover the entire amount and there is no need to call an external Payment Gateway, or
     * a Payment from a Hosted Gateway has already been applied to the order like Paypal Express Checkout)
     *
     * @return
     * @throws Exception
     */
    public String processCompleteCheckoutOrderFinalized(final RedirectAttributes redirectAttributes) throws PaymentException {
        Order cart = CartState.getCart();
        String param = "";
        if (cart != null && !(cart instanceof NullOrderImpl)) {
            try {
                Object o = BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().get(OfferActivity.OFFERS_EXPIRED);
                if(o!=null && (Boolean) o){
                    throw new OfferExpiredException("Offer or offer code was expired and removed, order price was re-calculated, check order total and try again");
                }
                String orderNumber = initiateCheckout(cart.getId());
                return getConfirmationViewRedirect(orderNumber);
            } catch (Exception e) {
                handleProcessingException(e, redirectAttributes);
                if(CustomerState.getCustomer().isAnonymous()){
                    param="?guest-checkout=true";
                }
            }
        }

        return getCheckoutPageRedirect()+param;
    }

    public String initiateCheckout(Long orderId) throws Exception{
        if (paymentGatewayCheckoutService != null && orderId != null) {
            return paymentGatewayCheckoutService.initiateCheckout(orderId);
        }
        return null;
    }

    public void handleProcessingException(Exception e, RedirectAttributes redirectAttributes) throws PaymentException {
        LOG.error("A Processing Exception Occurred finalizing the order. Adding Error to Redirect Attributes.", e);

        Throwable cause = e.getCause();

        if (cause!= null && cause.getCause() instanceof RequiredAttributeNotProvidedException) {
            redirectAttributes.addAttribute(PaymentGatewayAbstractController.PAYMENT_PROCESSING_ERROR,
                    PaymentGatewayAbstractController.getCartReqAttributeNotProvidedMessage());
        }else if(cause !=null && cause.getCause() instanceof OfferExpiredException || e instanceof OfferExpiredException) {
            String message = (cause != null && cause.getCause() != null) ? cause.getCause().getMessage() : e.getMessage();
            redirectAttributes.addAttribute(PaymentGatewayAbstractController.PAYMENT_PROCESSING_ERROR,
                    message);
        } else if(cause !=null && cause.getCause() instanceof OfferMaxUseExceededException) {
            redirectAttributes.addAttribute(PaymentGatewayAbstractController.PAYMENT_PROCESSING_ERROR,
                    "There was an error during checkout:"+cause.getCause().getMessage());
        } else {
            redirectAttributes.addAttribute(PaymentGatewayAbstractController.PAYMENT_PROCESSING_ERROR,
                    PaymentGatewayAbstractController.getProcessingErrorMessage());
        }
    }

    public String getBaseConfirmationRedirect() {
        return baseConfirmationRedirect;
    }

    protected String getConfirmationViewRedirect(String orderNumber) {
        return getBaseConfirmationRedirect() + "/" + orderNumber;
    }

}
