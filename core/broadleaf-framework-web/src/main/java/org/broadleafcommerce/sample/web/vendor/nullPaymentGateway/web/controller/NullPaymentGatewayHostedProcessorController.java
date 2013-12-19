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
package org.broadleafcommerce.sample.web.vendor.nullPaymentGateway.web.controller;

import org.broadleafcommerce.sample.web.payment.service.gateway.NullPaymentGatewayConfigurationService;
import org.broadleafcommerce.sample.web.vendor.nullPaymentGateway.service.payment.NullPaymentGatewayConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * This is a sample implementation of a Hosted Payment Gateway Processor.
 * This mimics the flow of a real hosted service like PayPal Express Checkout.
 *
 * In order to use this sample controller, you will need to component scan
 * the package "org.broadleafcommerce.sample.web".
 *
 * This should NOT be used in production, and is meant solely for demonstration
 * purposes only.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Controller("blNullPaymentGatewayHostedProcessorController")
public class NullPaymentGatewayHostedProcessorController {

    @Resource(name = "blNullPaymentGatewayConfigurationService")
    protected NullPaymentGatewayConfigurationService paymentGatewayConfigurationService;

    @RequestMapping(value = "/hosted/null-checkout", method = RequestMethod.POST)
    public @ResponseBody String retrieveHostedEndpoint(HttpServletRequest request){

        Map<String,String[]> paramMap = request.getParameterMap();

        String transactionAmount = "";
        String orderId="";
        String completeCheckoutOnCallback = "true";
        String resultMessage = "Hosted Call Successful";

        if (paramMap.get(NullPaymentGatewayConstants.TRANSACTION_AMT) != null
                && paramMap.get(NullPaymentGatewayConstants.TRANSACTION_AMT).length > 0) {
            transactionAmount = paramMap.get(NullPaymentGatewayConstants.TRANSACTION_AMT)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.ORDER_ID) != null
                && paramMap.get(NullPaymentGatewayConstants.ORDER_ID).length > 0) {
            orderId = paramMap.get(NullPaymentGatewayConstants.ORDER_ID)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.COMPLETE_CHECKOUT_ON_CALLBACK) != null
                && paramMap.get(NullPaymentGatewayConstants.COMPLETE_CHECKOUT_ON_CALLBACK).length > 0) {
            completeCheckoutOnCallback = paramMap.get(NullPaymentGatewayConstants.COMPLETE_CHECKOUT_ON_CALLBACK)[0];
        }

        StringBuffer response = new StringBuffer();
        response.append("<!DOCTYPE HTML>");
        response.append("<!--[if lt IE 7]> <html class=\"no-js lt-ie9 lt-ie8 lt-ie7\" lang=\"en\"> <![endif]-->");
        response.append("<!--[if IE 7]> <html class=\"no-js lt-ie9 lt-ie8\" lang=\"en\"> <![endif]-->");
        response.append("<!--[if IE 8]> <html class=\"no-js lt-ie9\" lang=\"en\"> <![endif]-->");
        response.append("<!--[if gt IE 8]><!--> <html class=\"no-js\" lang=\"en\"> <!--<![endif]-->");
        response.append("<body>");
        response.append("<h1>Mock Hosted Checkout</h1>");
        response.append("<p>This is an example that demonstrates the flow of a Hosted Third Party Checkout Integration (e.g. PayPal Express Checkout)</p>");
        response.append("<p>This customer will be prompted to either enter their credentials or fill out their payment information. Once complete, " +
                "they will be redirected back to either a confirmation page or a review page to complete checkout.</p>");
        response.append("<form action=\"" +
                paymentGatewayConfigurationService.getHostedRedirectReturnUrl() +
                "\" method=\"GET\" id=\"NullPaymentGatewayRedirectForm\" name=\"NullPaymentGatewayRedirectForm\">");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.TRANSACTION_AMT
                +"\" value=\"" + transactionAmount + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.ORDER_ID
                +"\" value=\"" + orderId + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.COMPLETE_CHECKOUT_ON_CALLBACK
                +"\" value=\"" + completeCheckoutOnCallback + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.RESULT_MESSAGE
                +"\" value=\"" + resultMessage + "\"/>");

        response.append("<input type=\"submit\" value=\"Please Click Here To Complete Checkout\"/>");
        response.append("</form>");
        response.append("</body>");
        response.append("</html>");

        return response.toString();
    }
}
