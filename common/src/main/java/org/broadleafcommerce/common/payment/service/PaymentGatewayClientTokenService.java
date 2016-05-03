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

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;

/**
 * <p>The intention of this API is to generate the necessary parameters and fields
 * needed by the payment gateway's JavaScript library in order to create a tokenization request.
 * These parameters will be placed on the ResponseDTO for consumption by a client application or template.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayClientTokenService {

    public PaymentResponseDTO generateClientToken(PaymentRequestDTO requestDTO);

}
