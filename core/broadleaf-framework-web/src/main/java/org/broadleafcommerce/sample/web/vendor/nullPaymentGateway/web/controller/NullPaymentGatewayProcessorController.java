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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.CreditCardValidator;
import org.broadleafcommerce.sample.web.payment.service.gateway.NullPaymentGatewayConfigurationService;
import org.broadleafcommerce.sample.web.vendor.nullPaymentGateway.service.payment.NullPaymentGatewayConstants;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * This is a sample implementation of a Payment Gateway Processor.
 * This mimics what a real payment gateway might do in order to process
 * credit card information. So, this controller will handle the POST
 * from your credit card form on your checkout page and do some
 * minimal Credit Card Validation (luhn check and expiration date is after today).
 * In production, that form should securely POST to your third party payment gateway and not this controller.
 *
 * In order to use this sample controller, you will need to component scan
 * the package "org.broadleafcommerce.sample.web".
 *
 * This should NOT be used in production, and is meant solely for demonstration
 * purposes only.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Controller("blNullPaymentGatewayProcessorController")
public class NullPaymentGatewayProcessorController {

    @Resource(name = "blNullPaymentGatewayConfigurationService")
    protected NullPaymentGatewayConfigurationService paymentGatewayConfigurationService;

    @RequestMapping(value = "/null-checkout/process", method = RequestMethod.POST)
    public @ResponseBody String processTransparentRedirectForm(HttpServletRequest request){
        Map<String,String[]> paramMap = request.getParameterMap();

        String transactionAmount = "";
        String orderId="";
        String billingFirstName = "";
        String billingLastName = "";
        String billingAddressLine1 = "";
        String billingAddressLine2 = "";
        String billingCity = "";
        String billingState = "";
        String billingZip = "";
        String shippingFirstName = "";
        String shippingLastName = "";
        String shippingAddressLine1 = "";
        String shippingAddressLine2 = "";
        String shippingCity = "";
        String shippingState = "";
        String shippingZip = "";
        String creditCardName = "";
        String creditCardNumber = "";
        String creditCardExpDate = "";
        String creditCardCVV = "";
        String cardType = "UNKNOWN";

        String resultMessage = "";
        String resultSuccess = "";

        if (paramMap.get(NullPaymentGatewayConstants.TRANSACTION_AMT) != null
                && paramMap.get(NullPaymentGatewayConstants.TRANSACTION_AMT).length > 0) {
            transactionAmount = paramMap.get(NullPaymentGatewayConstants.TRANSACTION_AMT)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.ORDER_ID) != null
                && paramMap.get(NullPaymentGatewayConstants.ORDER_ID).length > 0) {
            orderId = paramMap.get(NullPaymentGatewayConstants.ORDER_ID)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.BILLING_FIRST_NAME) != null
                && paramMap.get(NullPaymentGatewayConstants.BILLING_FIRST_NAME).length > 0) {
            billingFirstName = paramMap.get(NullPaymentGatewayConstants.BILLING_FIRST_NAME)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.BILLING_LAST_NAME) != null
                && paramMap.get(NullPaymentGatewayConstants.BILLING_LAST_NAME).length > 0) {
            billingLastName = paramMap.get(NullPaymentGatewayConstants.BILLING_LAST_NAME)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE1) != null
                && paramMap.get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE1).length > 0) {
            billingAddressLine1 = paramMap.get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE1)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE2) != null
                && paramMap.get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE2).length > 0) {
            billingAddressLine2 = paramMap.get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE2)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.BILLING_CITY) != null
                && paramMap.get(NullPaymentGatewayConstants.BILLING_CITY).length > 0) {
            billingCity = paramMap.get(NullPaymentGatewayConstants.BILLING_CITY)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.BILLING_STATE) != null
                && paramMap.get(NullPaymentGatewayConstants.BILLING_STATE).length > 0) {
            billingState = paramMap.get(NullPaymentGatewayConstants.BILLING_STATE)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.BILLING_ZIP) != null
                && paramMap.get(NullPaymentGatewayConstants.BILLING_ZIP).length > 0) {
            billingZip = paramMap.get(NullPaymentGatewayConstants.BILLING_ZIP)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.SHIPPING_FIRST_NAME) != null
                && paramMap.get(NullPaymentGatewayConstants.SHIPPING_FIRST_NAME).length > 0) {
            shippingFirstName = paramMap.get(NullPaymentGatewayConstants.SHIPPING_FIRST_NAME)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.SHIPPING_LAST_NAME) != null
                && paramMap.get(NullPaymentGatewayConstants.SHIPPING_LAST_NAME).length > 0) {
            shippingLastName = paramMap.get(NullPaymentGatewayConstants.SHIPPING_LAST_NAME)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE1) != null
                && paramMap.get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE1).length > 0) {
            shippingAddressLine1 = paramMap.get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE1)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE2) != null
                && paramMap.get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE2).length > 0) {
            shippingAddressLine2 = paramMap.get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE2)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.SHIPPING_CITY) != null
                && paramMap.get(NullPaymentGatewayConstants.SHIPPING_CITY).length > 0) {
            shippingCity = paramMap.get(NullPaymentGatewayConstants.SHIPPING_CITY)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.SHIPPING_STATE) != null
                && paramMap.get(NullPaymentGatewayConstants.SHIPPING_STATE).length > 0) {
            shippingState = paramMap.get(NullPaymentGatewayConstants.SHIPPING_STATE)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.SHIPPING_ZIP) != null
                && paramMap.get(NullPaymentGatewayConstants.SHIPPING_ZIP).length > 0) {
            shippingZip = paramMap.get(NullPaymentGatewayConstants.SHIPPING_ZIP)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_NAME) != null
                && paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_NAME).length > 0) {
            creditCardName = paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_NAME)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_NUMBER) != null
                && paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_NUMBER).length > 0) {
            creditCardNumber = paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_NUMBER)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_EXP_DATE) != null
                && paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_EXP_DATE).length > 0) {
            creditCardExpDate = paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_EXP_DATE)[0];
        }

        if (paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_CVV) != null
                && paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_CVV).length > 0) {
            creditCardCVV = paramMap.get(NullPaymentGatewayConstants.CREDIT_CARD_CVV)[0];
        }

        CreditCardValidator visaValidator = new CreditCardValidator(CreditCardValidator.VISA);
        CreditCardValidator amexValidator = new CreditCardValidator(CreditCardValidator.AMEX);
        CreditCardValidator mcValidator = new CreditCardValidator(CreditCardValidator.MASTERCARD);
        CreditCardValidator discoverValidator = new CreditCardValidator(CreditCardValidator.DISCOVER);

        if (StringUtils.isNotBlank(transactionAmount) &&
                StringUtils.isNotBlank(creditCardNumber) &&
                StringUtils.isNotBlank(creditCardExpDate)) {

            boolean validCard = false;
            if (visaValidator.isValid(creditCardNumber)){
                validCard = true;
                cardType = "VISA";
            } else if (amexValidator.isValid(creditCardNumber)) {
                validCard = true;
                cardType = "AMEX";
            } else if (mcValidator.isValid(creditCardNumber)) {
                validCard = true;
                cardType = "MASTERCARD";
            } else if (discoverValidator.isValid(creditCardNumber)) {
                validCard = true;
                cardType = "DISCOVER";
            }

            boolean validDateFormat = false;
            boolean validDate = false;
            String[] parsedDate = creditCardExpDate.split("/");
            if (parsedDate.length == 2) {
                String expMonth = parsedDate[0];
                String expYear = parsedDate[1];
                try {
                    DateTime expirationDate = new DateTime(Integer.parseInt("20"+expYear), Integer.parseInt(expMonth), 1, 0, 0);
                    validDate = expirationDate.isAfterNow();
                    validDateFormat = true;
                } catch (Exception e) {
                    //invalid date format
                }
            }

            if (!validDate || !validDateFormat) {
                transactionAmount = "0";
                resultMessage = "The expiration date is invalid. Please use (MM/YY)";
                resultSuccess = "false";
            } else if (!validCard) {
                transactionAmount = "0";
                resultMessage = "The card number you entered is invalid.";
                resultSuccess = "false";
            } else {
                resultMessage = "Success!";
                resultSuccess = "true";
            }

        } else {
            transactionAmount = "0";
            resultMessage = "There was a problem processing your request. Invalid Arguments.";
            resultSuccess = "false";
        }

        StringBuffer response = new StringBuffer();
        response.append("<!DOCTYPE HTML>");
        response.append("<!--[if lt IE 7]> <html class=\"no-js lt-ie9 lt-ie8 lt-ie7\" lang=\"en\"> <![endif]-->");
        response.append("<!--[if IE 7]> <html class=\"no-js lt-ie9 lt-ie8\" lang=\"en\"> <![endif]-->");
        response.append("<!--[if IE 8]> <html class=\"no-js lt-ie9\" lang=\"en\"> <![endif]-->");
        response.append("<!--[if gt IE 8]><!--> <html class=\"no-js\" lang=\"en\"> <!--<![endif]-->");
        response.append("<body>");
        response.append("<form action=\"" +
                paymentGatewayConfigurationService.getTransparentRedirectReturnUrl() +
                "\" method=\"POST\" id=\"NullPaymentGatewayRedirectForm\" name=\"NullPaymentGatewayRedirectForm\">");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.TRANSACTION_AMT
                +"\" value=\"" + transactionAmount + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.ORDER_ID
                +"\" value=\"" + orderId + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.RESULT_MESSAGE
                +"\" value=\"" + resultMessage + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.RESULT_SUCCESS
                +"\" value=\"" + resultSuccess + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.BILLING_FIRST_NAME
                +"\" value=\"" + billingFirstName + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.BILLING_LAST_NAME
                +"\" value=\"" + billingLastName + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.BILLING_ADDRESS_LINE1
                +"\" value=\"" + billingAddressLine1 + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.BILLING_ADDRESS_LINE2
                +"\" value=\"" + billingAddressLine2 + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.BILLING_CITY
                +"\" value=\"" + billingCity + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.BILLING_STATE
                +"\" value=\"" + billingState + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.BILLING_ZIP
                +"\" value=\"" + billingZip + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.SHIPPING_FIRST_NAME
                +"\" value=\"" + shippingFirstName + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.SHIPPING_LAST_NAME
                +"\" value=\"" + shippingLastName + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE1
                +"\" value=\"" + shippingAddressLine1 + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE2
                +"\" value=\"" + shippingAddressLine2 + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.SHIPPING_CITY
                +"\" value=\"" + shippingCity + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.SHIPPING_STATE
                +"\" value=\"" + shippingState + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.SHIPPING_ZIP
                +"\" value=\"" + shippingZip + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.CREDIT_CARD_NAME
                +"\" value=\"" + creditCardName + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.CREDIT_CARD_LAST_FOUR
                +"\" value=\"" + StringUtils.right(creditCardNumber, 4) + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.CREDIT_CARD_TYPE
                +"\" value=\"" + cardType + "\"/>");
        response.append("<input type=\"hidden\" name=\"" + NullPaymentGatewayConstants.CREDIT_CARD_EXP_DATE
                +"\" value=\"" + creditCardExpDate + "\"/>");

        response.append("<input type=\"submit\" value=\"Please Click Here To Complete Checkout\"/>");
        response.append("</form>");
        response.append("<script type=\"text/javascript\">");
        response.append("document.getElementById('NullPaymentGatewayRedirectForm').submit();");
        response.append("</script>");
        response.append("</body>");
        response.append("</html>");

        return response.toString();
    }

}
