/*
 * #%L
 * BroadleafCommerce Common Libraries
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
 * <p>Abstract controller that provides convenience methods and resource declarations for Payment Gateway
 * Operations that are shared between all gateway controllers belong here.</p>
 *
 * <p>The Core Framework should have an implementation of a "blPaymentGatewayCheckoutService" bean defined.
 * If you are using the common jars without the framework dependency, you will either have to
 * implement the blPaymentGatewayCheckoutService yourself, or override the applyPaymentToOrder and
 * the markPaymentAsInvalid methods accordingly.</p>
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

    @Autowired(required=false)
    @Qualifier("blPaymentGatewayCheckoutService")
    protected PaymentGatewayCheckoutService paymentGatewayCheckoutService;

    @Resource(name = "blPaymentGatewayWebResponsePrintService")
    protected PaymentGatewayWebResponsePrintService webResponsePrintService;

    public Long applyPaymentToOrder(PaymentResponseDTO responseDTO) throws IllegalArgumentException {
        if (paymentGatewayCheckoutService != null) {
            return paymentGatewayCheckoutService.applyPaymentToOrder(responseDTO, getConfiguration());
        }
        return null;
    }

    public String initiateCheckout(Long orderId) throws Exception {
        if (paymentGatewayCheckoutService != null && orderId != null) {
            return paymentGatewayCheckoutService.initiateCheckout(orderId);
        }
        return null;
    }

    public String lookupOrderNumberFromOrderId(PaymentResponseDTO responseDTO) {
        if (paymentGatewayCheckoutService != null) {
            return paymentGatewayCheckoutService.lookupOrderNumberFromOrderId(responseDTO);
        }
        return null;
    }

    // ***********************************************
    // Common Result Processing
    // ***********************************************
    /**
     *
     * try {
     *   translate http request to DTO
     *   apply payment to order
     *   check success and validity of response
     *   if (complete checkout on callback == true)
     *     initiateCheckout(order id);
     *   else
     *     show review page;
     * } catch (Exception e) {
     *     notify admin user of failure
     *     handle processing exception
     * }
     *
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
                LOG.trace("HTTPRequest - " + webResponsePrintService.printRequest(request)) ;

                LOG.trace("An exception was caught either from processing the response and applying the payment to " +
                        "the order, or an activity in the checkout workflow threw an exception. Attempting to " +
                        "mark the payment as invalid and delegating to the payment module to handle any other " +
                        "exception processing. The error caught was: " + e.getMessage() + " : " + e.toString());
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
}
