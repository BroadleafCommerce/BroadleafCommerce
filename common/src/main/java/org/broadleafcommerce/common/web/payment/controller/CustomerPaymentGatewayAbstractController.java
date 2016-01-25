/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
import org.broadleafcommerce.common.payment.service.CustomerPaymentGatewayService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.broadleafcommerce.common.payment.service.PaymentGatewayWebResponsePrintService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayWebResponseService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Abstract controller that provides convenience methods and resource declarations to facilitate payment gateway
 * communication between the implementing module and the Spring injected customer profile engine. This class provides
 * flows to enable Credit Card tokenization in a PCI-Compliant manner (e.g. through a mechanism like Transparent Redirect)
 * with the ability to save it to a customer's profile.
 * </p>
 *
 * <p>If used in conjunction with the core framework, Broadleaf provides all the necessary spring resources, such as
 * "blCustomerPaymentGatewayService" that are needed for this class. If you are using the common jars without the framework
 * dependency, you will either have to implement the blCustomerPaymentGatewayService yourself in order to
 * save the token to your implementing customer profile system.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class CustomerPaymentGatewayAbstractController extends BroadleafAbstractController {

    protected static final Log LOG = LogFactory.getLog(CustomerPaymentGatewayAbstractController.class);

    @Resource(name = "blPaymentGatewayWebResponsePrintService")
    protected PaymentGatewayWebResponsePrintService webResponsePrintService;

    @Autowired(required=false)
    @Qualifier("blCustomerPaymentGatewayService")
    protected CustomerPaymentGatewayService customerPaymentGatewayService;

    public Long applyCustomerTokenToProfile(PaymentResponseDTO responseDTO) throws IllegalArgumentException {
        if (LOG.isErrorEnabled()) {
            if (customerPaymentGatewayService == null) {
                LOG.trace("applyCustomerTokenToProfile: CustomerPaymentGatewayService is null. Please check your configuration.");
            }
        }

        if (customerPaymentGatewayService != null) {
            return customerPaymentGatewayService.createCustomerPaymentFromResponseDTO(responseDTO, getConfiguration());
        }

        return null;
    }

    // ***********************************************
    // Customer Payment Result Processing
    // ***********************************************
    /**
     * <p>This method is intended to initiate the creation of a saved payment token.</p>
     *
     * <p>This assumes that the implementing gateway's {@link org.broadleafcommerce.common.payment.service.PaymentGatewayWebResponseService}
     * knows how to parse an incoming {@link javax.servlet.http.HttpServletRequest} into a
     * {@link org.broadleafcommerce.common.payment.dto.PaymentResponseDTO} which will then be used by the
     * customer profile engine to save a token to the user's account (e.g. wallet).</p>
     *
     * @param model - Spring MVC model
     * @param request - the HTTPServletRequest (originating either from a Payment Gateway or from the implementing checkout engine)
     * @param redirectAttributes - Spring MVC redirect attributes
     * @return the resulting view
     * @throws org.broadleafcommerce.common.vendor.service.exception.PaymentException
     */
    public String createCustomerPayment(Model model, HttpServletRequest request,
                                        final RedirectAttributes redirectAttributes) throws PaymentException {

        try {
            PaymentResponseDTO responseDTO = getWebResponseService().translateWebResponse(request);
            if (LOG.isTraceEnabled()) {
                LOG.trace("HTTPRequest translated to Raw Response: " +  responseDTO.getRawResponse());
            }

            Long customerPaymentId = applyCustomerTokenToProfile(responseDTO);

            if (customerPaymentId != null) {
                return getCustomerPaymentViewRedirect(String.valueOf(customerPaymentId));
            }

        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("HTTPRequest - " + webResponsePrintService.printRequest(request));

                LOG.error("An exception was caught either from processing the response or saving the resulting " +
                        "payment token to the customer's profile - delegating to the payment module to handle any other " +
                        "exception processing. The error caught was: " + e);
            }
            handleProcessingException(e, redirectAttributes);
        }

        return getCustomerPaymentErrorRedirect();
    }

    public abstract PaymentGatewayWebResponseService getWebResponseService();

    public abstract PaymentGatewayConfiguration getConfiguration();

    public abstract String getCustomerPaymentViewRedirect(String customerPaymentId);

    public abstract String getCustomerPaymentErrorRedirect();

    public abstract void handleProcessingException(Exception e, final RedirectAttributes redirectAttributes)
            throws PaymentException;

}
