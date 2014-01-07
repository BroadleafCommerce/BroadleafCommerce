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
