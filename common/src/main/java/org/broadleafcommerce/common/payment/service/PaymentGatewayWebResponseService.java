/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>The purpose of this class, is to provide an API that will translate a web response
 * returned from a Payment Gateway into a PaymentResponseDTO</p>
 *
 * <p>Some payment gateways provide the ability that ensures that the transaction data
 * is passed back to your application when a transaction is completed.
 * Most of the gateways issue an HTML Post to return data to your server for both
 * approved and declined transactions. This occurs even if a customer closes the browser
 * before returning to your site, or if the payment response is somehow severed.</p>
 *
 * <p>Many gateways will continue calling your exposed API Webhook for a certain period until
 * a 200 Response is received. Others will forward to an error page configured through the gateway.</p>
 *
 * <p>This is usually invoked by a gateway endpoint controller that extends PaymentGatewayAbstractController</p>
 *
 * @see {@link org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayWebResponseService {

    public PaymentResponseDTO translateWebResponse(HttpServletRequest request) throws PaymentException;

}
