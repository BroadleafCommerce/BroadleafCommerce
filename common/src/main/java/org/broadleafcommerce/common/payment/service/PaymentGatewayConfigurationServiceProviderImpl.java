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

import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blPaymentGatewayConfigurationServiceProvider")
public class PaymentGatewayConfigurationServiceProviderImpl implements PaymentGatewayConfigurationServiceProvider {

    @Resource(name = "blPaymentGatewayConfigurationServices")
    protected List<PaymentGatewayConfigurationService> gatewayConfigurationServices;
    
    @Override
    public PaymentGatewayConfigurationService getGatewayConfigurationService(PaymentGatewayType gatewayType) {
        if (gatewayType == null) {
            throw new IllegalArgumentException("Gateway type cannot be null");
        }
        for (PaymentGatewayConfigurationService config : getGatewayConfigurationServices()) {
            if (config.getConfiguration().getGatewayType().equals(gatewayType)) {
                return config;
            }
        }
        
        throw new IllegalArgumentException("There is no gateway configured for " + gatewayType.getFriendlyType());
    }
    
    public List<PaymentGatewayConfigurationService> getGatewayConfigurationServices() {
        return gatewayConfigurationServices;
    }
    
    public void setGatewayConfigurationServices(List<PaymentGatewayConfigurationService> gatewayConfigurationServices) {
        this.gatewayConfigurationServices = gatewayConfigurationServices;
    }


}
