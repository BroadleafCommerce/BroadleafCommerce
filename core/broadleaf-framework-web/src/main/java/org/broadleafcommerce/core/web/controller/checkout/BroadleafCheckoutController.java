/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;
import org.broadleafcommerce.core.checkout.service.gateway.PassthroughPaymentConstants;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.exception.IllegalCartOperationException;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
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
     * @return the return path
     */
    public String checkout(HttpServletRequest request, HttpServletResponse response, Model model,
                           RedirectAttributes redirectAttributes) {
        Order cart = CartState.getCart();
        
        try {
            orderService.preValidateCartOperation(cart);
        } catch (IllegalCartOperationException ex) {
            model.addAttribute("cartRequiresLock", true);
        }

        if (!(cart instanceof NullOrderImpl)) {
            model.addAttribute("orderMultishipOptions",
                    orderMultishipOptionService.getOrGenerateOrderMultishipOptions(cart));
            model.addAttribute("paymentRequestDTO",
                    dtoTranslationService.translateOrder(cart));
        }
        populateModelWithReferenceData(request, model);
        return getCheckoutView();
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

        if (cart != null && !(cart instanceof NullOrderImpl)) {
            try {
                String orderNumber = initiateCheckout(cart.getId());
                return getConfirmationViewRedirect(orderNumber);
            } catch (Exception e) {
                handleProcessingException(e, redirectAttributes);
            }
        }

        return getCheckoutPageRedirect();
    }

    public String initiateCheckout(Long orderId) throws Exception{
        if (paymentGatewayCheckoutService != null && orderId != null) {
            return paymentGatewayCheckoutService.initiateCheckout(orderId);
        }
        return null;
    }

    public void handleProcessingException(Exception e, RedirectAttributes redirectAttributes) throws PaymentException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("A Processing Exception Occurred finalizing the order. Adding Error to Redirect Attributes.");
        }

        redirectAttributes.addAttribute(PaymentGatewayAbstractController.PAYMENT_PROCESSING_ERROR,
                PaymentGatewayAbstractController.getProcessingErrorMessage());
    }

    public String getBaseConfirmationRedirect() {
        return baseConfirmationRedirect;
    }

    protected String getConfirmationViewRedirect(String orderNumber) {
        return getBaseConfirmationRedirect() + "/" + orderNumber;
    }

}
