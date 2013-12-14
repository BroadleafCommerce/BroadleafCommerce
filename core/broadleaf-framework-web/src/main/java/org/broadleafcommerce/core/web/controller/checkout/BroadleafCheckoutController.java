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
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
     * Processes the request to complete checkout.
     * 
     * An extension manager is used to collect payment methods.  A default
     * Collect On Delivery(COD) extension handler and a default credit card extension
     * handler are included in the framework.
     * 
     * The paymentInfoTypeList stores the types of payments that should be added to the
     * payments map.  An extension handler will need to match a type in the paymentInfoTypeList
     * in order for that extension handler to execute its addAdditionalPaymentInfos method.
     * An exception to this pattern is the customer credit extension handler.  The customer
     * credit extension handler will always be run regardless of whether or not CUSTOMER_CREDIT is
     * in the paymentInfoTypeList.
     *
     * This method assumes that a credit card payment info
     * will be either sent to a third party gateway or saved in a secure schema.
     * If the transaction is successful, the order will be assigned an order number,
     * its status change to SUBMITTED, and given a submit date. The method then
     * returns the default confirmation path "/confirmation/{orderNumber}"
     *
     * If the transaction is unsuccessful, (e.g. the gateway declines payment)
     * processFailedOrderCheckout() is called and reverses the state of the order.
     *
     * Note: this method removes any existing PaymentInfos not of type CUSTOMER_CREDIT or
     * GIFT_CARD before running the extension manager to add new PaymentInfos.
     *
     * @param request
     * @param response
     * @param model
     * @param billingForm
     * @return the return path
     * @throws ServiceException 
     */
/*    public String completeCheckout(HttpServletRequest request, HttpServletResponse response, Model model, BillingInfoForm billingForm, BindingResult result) throws CheckoutException, PricingException, ServiceException {
        Order cart = CartState.getCart();
        if (cart != null && !(cart instanceof NullOrderImpl)) {
            Map<OrderPayment, Referenced> payments = new HashMap<OrderPayment, Referenced>();
            
            Iterator<OrderPayment> paymentInfoItr = cart.getPayments().iterator();
            while (paymentInfoItr.hasNext()) {
                OrderPayment paymentInfo = paymentInfoItr.next();
                if (!PaymentType.CUSTOMER_CREDIT.equals(paymentInfo.getType()) && !PaymentType.GIFT_CARD.equals(paymentInfo.getType())) {
                    paymentInfoItr.remove();
                    orderService.removePaymentFromOrder(cart, paymentInfo);
                }
            }

            //Create list of PaymentInfoTypes that will determine which extension handler will run
            List<PaymentType> paymentInfoTypes = createPaymentInfoTypeList(billingForm);

            //Extension handlers add PaymentInfos to the payments map and the order
            paymentInfoServiceExtensionManager.getProxy().addAdditionalPaymentInfos(payments, paymentInfoTypes, request, response, model, billingForm, result);
            
            //Check for validation errors
            if (result.hasErrors()) {
                return handleCheckoutError(request, model);
            }
            
            try {
                CheckoutResponse checkoutResponse = checkoutService.performCheckout(cart);
                //Map<OrderPayment, PaymentResponseItem> paymentResponseItemMap = checkoutResponse.getPaymentResponse().getResponseItems();
                //for (PaymentResponseItem paymentResponseItem : paymentResponseItemMap.values()) {
                //    if (!paymentResponseItem.getTransactionSuccess()) {
                //        return handleCheckoutError(request, model);
                //    }
                //}
            } catch (CheckoutException workflowException) {
                return handleCheckoutError(request, model);
            }

            return getConfirmationView(cart.getOrderNumber());
        }

        return getCartPageRedirect();
    }*/




}
