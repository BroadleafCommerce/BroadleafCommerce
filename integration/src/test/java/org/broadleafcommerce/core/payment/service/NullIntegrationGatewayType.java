/*-
 * #%L
 * BroadleafCommerce Integration
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

package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.payment.PaymentGatewayType;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class NullIntegrationGatewayType extends PaymentGatewayType {

    public static final PaymentGatewayType NULL_INTEGRATION_GATEWAY  = new PaymentGatewayType("NULL_INTEGRATION_GATEWAY", "Null Payment Gateway Implementation");
    public static final PaymentGatewayType NULL_INTEGRATION_HOSTED_GATEWAY  = new PaymentGatewayType("NULL_INTEGRATION_HOSTED_GATEWAY", "Null Hosted Payment Gateway Implementation");


}
