/*-
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.web.payment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCheckoutService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.broadleafcommerce.common.payment.service.PaymentGatewayWebResponsePrintService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayWebResponseService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Abstract controller that provides convenience methods and resource declarations to facilitate payment gateway
 * communication between the implementing module and the Spring injected checkout engine. This class provides
 * generic flows and operations that are common across payment gateway integration methods.
 * You may notice that this intentionally resides in "common" as this supports the use case where an implementing module
 * can be used outside the scope of Broadleaf's "core" commerce engine.</p>
 *
 * <p>If used in conjunction with the core framework, Broadleaf provides all the necessary spring resources, such as
 * "blPaymentGatewayCheckoutService" that are needed for this class. If you are using the common jars without the framework
 * dependency, you will either have to implement the blPaymentGatewayCheckoutService yourself, or override the
 * "applyPaymentToOrder" and the "markPaymentAsInvalid" methods accordingly.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class PaymentGatewayAbstractController extends BroadleafAbstractController {

    protected static final Log LOG = LogFactory.getLog(PaymentGatewayAbstractController.class);
    public static final String PAYMENT_PROCESSING_ERROR = "PAYMENT_PROCESSING_ERROR";

    protected static String baseRedirect = "redirect:/";
    protected static String baseErrorView = "/error";
    protected static String baseOrderReviewRedirect = "redirect:/checkout";
    protected static String baseConfirmationRedirect = "redirect:/confirmation";
    protected static String baseCartRedirect = "redirect:/cart";

    //Externalized Generic Payment Error Message
    protected static String processingErrorMessage = "cart.paymentProcessingError";
    protected static String cartReqAttributeNotProvidedMessage = "cart.requiredAttributeNotProvided";

    @Autowired(required=false)
    @Qualifier("blPaymentGatewayCheckoutService")
    protected PaymentGatewayCheckoutService paymentGatewayCheckoutService;

    @Resource(name = "blPaymentGatewayWebResponsePrintService")
    protected PaymentGatewayWebResponsePrintService webResponsePrintService;

    public Long applyPaymentToOrder(PaymentResponseDTO responseDTO) throws IllegalArgumentException {
        if (LOG.isErrorEnabled()) {
            if (paymentGatewayCheckoutService == null) {
                LOG.error("applyPaymentToOrder: PaymentCheckoutService is null. Please check your configuration.");
            }
        }

        if (paymentGatewayCheckoutService != null) {
            return paymentGatewayCheckoutService.applyPaymentToOrder(responseDTO, getConfiguration());
        }
        return null;
    }

    public String initiateCheckout(Long orderId) throws Exception {
        String orderNumber = null;
        if (LOG.isErrorEnabled()) {
            if (paymentGatewayCheckoutService == null) {
                LOG.error("initiateCheckout: PaymentCheckoutService is null. Please check your configuration.");
            }
        }

        if (paymentGatewayCheckoutService != null && orderId != null) {
            orderNumber = paymentGatewayCheckoutService.initiateCheckout(orderId);
        }

        if (orderNumber == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("The result from calling initiateCheckout with paymentCheckoutService and orderId: " + orderId + " is null");
            }
        }

        return orderNumber;
    }

    public String lookupOrderNumberFromOrderId(PaymentResponseDTO responseDTO) {
        String orderNumber = null;
        if (LOG.isErrorEnabled()) {
            if (paymentGatewayCheckoutService == null) {
                LOG.error("lookupOrderNumberFromOrderId: PaymentCheckoutService is null. Please check your configuration.");
            }
        }

        if (paymentGatewayCheckoutService != null) {
            orderNumber = paymentGatewayCheckoutService.lookupOrderNumberFromOrderId(responseDTO);
        }

        if (orderNumber == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("The result from calling lookupOrderNumberFromOrderId is null");
            }
        }

        return orderNumber;
    }

    // ***********************************************
    // Common Checkout Result Processing
    // ***********************************************
    /**
     * This method is intended to initiate the final steps in checkout either
     * via a request coming directly from a Payment Gateway (i.e. a Transparent Redirect) or from
     * some sort of tokenization mechanism client-side.
     *
     * The assumption is that the implementing gateway's controller that extends this class
     * will have implemented a {@link org.broadleafcommerce.common.payment.service.PaymentGatewayWebResponseService}
     * with the ability to translate an {@link javax.servlet.http.HttpServletRequest} into a
     * {@link org.broadleafcommerce.common.payment.dto.PaymentResponseDTO} which will then be used by the framework
     * to create the appropriate order payments and transactions as well as invoke the checkout workflow
     * if configured to do so.
     *
     * The general flow is as follows:
     *
     * try {
     *   translate http request to DTO
     *   apply payment to order (if unsuccessful, payment will be archived)
     *   if (not successful or not valid)
     *     redirect to error view
     *   if (complete checkout on callback == true)
     *     initiateCheckout(order id);
     *   else
     *     show review page;
     * } catch (Exception e) {
     *     log error
     *     handle processing exception
     * }
     *
     * @param model - Spring MVC model
     * @param request - the HTTPServletRequest (originating either from a Payment Gateway or from the implementing checkout engine)
     * @param redirectAttributes - Spring MVC redirect attributes
     * @return the resulting view
     * @throws PaymentException
     */
    public String process(Model model, HttpServletRequest request,
                          final RedirectAttributes redirectAttributes) throws PaymentException {
        Long orderPaymentId = null;

        try {
            PaymentResponseDTO responseDTO = getWebResponseService().translateWebResponse(request);
            if (LOG.isTraceEnabled()) {
                LOG.trace("HTTPRequest translated to Raw Response: " +  responseDTO.getRawResponse());
            }

            orderPaymentId = applyPaymentToOrder(responseDTO);

            if (!responseDTO.isSuccessful()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("The Response DTO is marked as unsuccessful. Delegating to the " +
                            "payment module to handle an unsuccessful transaction");
                }

                handleUnsuccessfulTransaction(model, redirectAttributes, responseDTO);
                return getErrorViewRedirect();
            }

            if (!responseDTO.isValid()) {
                throw new PaymentException("The validity of the response cannot be confirmed." +
                        "Check the Tamper Proof Seal for more details.");
            }

            String orderId = responseDTO.getOrderId();
            if (orderId == null) {
                throw new RuntimeException("Order ID must be set on the Payment Response DTO");
            }

            if (responseDTO.isCompleteCheckoutOnCallback()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("The Response DTO for this Gateway is configured to complete checkout on callback. " +
                            "Initiating Checkout with Order ID: " + orderId);
                }

                String orderNumber = initiateCheckout(Long.parseLong(orderId));
                return getConfirmationViewRedirect(orderNumber);
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("The Gateway is configured to not complete checkout. " +
                            "Redirecting to the Order Review Page for Order ID: " + orderId);
                }

                return getOrderReviewRedirect();
            }

        } catch (Exception e) {

            if (LOG.isTraceEnabled()) {
                LOG.trace("HTTPRequest - " + webResponsePrintService.printRequest(request));
            }

            if (LOG.isErrorEnabled()) {
                LOG.error("An exception was caught either from processing the response and applying the payment to " +
                        "the order, or an activity in the checkout workflow threw an exception. Attempting to " +
                        "mark the payment as invalid and delegating to the payment module to handle any other " +
                        "exception processing", e);
            }
            
            if (paymentGatewayCheckoutService != null && orderPaymentId != null) {
                paymentGatewayCheckoutService.markPaymentAsInvalid(orderPaymentId);
            }

            handleProcessingException(e, redirectAttributes);
        }

        return getErrorViewRedirect();
    }

    public abstract void handleProcessingException(Exception e, final RedirectAttributes redirectAttributes)
            throws PaymentException;

    public abstract void handleUnsuccessfulTransaction(Model model, final RedirectAttributes redirectAttributes,
                                                       PaymentResponseDTO responseDTO) throws PaymentException;

    public abstract String getGatewayContextKey();

    public abstract PaymentGatewayWebResponseService getWebResponseService();

    public abstract PaymentGatewayConfiguration getConfiguration();

    public abstract String returnEndpoint(Model model, HttpServletRequest request,
                                          final RedirectAttributes redirectAttributes,
                                          Map<String, String> pathVars) throws PaymentException;

    public abstract String errorEndpoint(Model model, HttpServletRequest request,
                                         final RedirectAttributes redirectAttributes,
                                         Map<String, String> pathVars) throws PaymentException;


    protected String getErrorViewRedirect() {
        //delegate to the modules endpoint as there may be additional processing that is involved
        return baseRedirect + getGatewayContextKey() + baseErrorView;
    }

    protected String getCartViewRedirect() {
        return baseCartRedirect;
    }

    public String getOrderReviewRedirect()  {
        return baseOrderReviewRedirect;
    }

    public String getBaseConfirmationRedirect() {
        return baseConfirmationRedirect;
    }

    protected String getConfirmationViewRedirect(String orderNumber) {
        return getBaseConfirmationRedirect() + "/" + orderNumber;
    }

    public static String getProcessingErrorMessage() {
        return processingErrorMessage;
    }

    public static String getCartReqAttributeNotProvidedMessage() {
        return cartReqAttributeNotProvidedMessage;
    }
}
