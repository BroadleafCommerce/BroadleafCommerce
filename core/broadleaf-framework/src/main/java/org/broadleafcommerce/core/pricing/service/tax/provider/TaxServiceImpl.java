package org.broadleafcommerce.core.pricing.service.tax.provider;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service("blTaxService")
public class TaxServiceImpl implements TaxService {

    protected boolean mustCalculate = false;

    @Resource(name = "blTaxProviders")
    protected List<TaxProvider> providers;

    @Resource(name = "blModuleConfigurationService")
    protected ModuleConfigurationService moduleConfigService;

    @Override
    public Order calculateTaxForOrder(Order order) throws TaxException {

        List<ModuleConfiguration> configurations =
                moduleConfigService.findActiveConfigurationsByType(ModuleConfigurationType.TAX_CALCULATION);

        if (configurations != null && !configurations.isEmpty()) {

            //Try to find a default configuration
            ModuleConfiguration config = null;
            for (ModuleConfiguration configuration : configurations) {
                if (configuration.getIsDefault()) {
                    config = configuration;
                    break;
                }
            }

            if (config == null) {
                //if there wasn't a default one, use the first active one...
                config = configurations.get(0);
            }

            if (providers != null && !providers.isEmpty()) {
                for (TaxProvider provider : providers) {
                    if (provider.canRespond(config)) {
                        return provider.calculateTaxForOrder(order, config);
                    }
                }
            }
        }
        if (!mustCalculate) {
            return order;
        }
        throw new TaxException("No eligible tax providers were configured.");
    }

    /**
     * Sets a list of <code>TaxProvider</code> implementations.
     * 
     * @param providers
     */
    public void setTaxProviders(List<TaxProvider> providers) {
        this.providers = providers;
    }

    /**
     * Sets whether or not this service is required to delegate to a tax provider. 
     * Setting this value to true will cause an exception if no tax providers are configured, 
     * or if none are eligible. 
     * @param mustCalculate
     */
    public void setMustCalculate(boolean mustCalculate) {
        this.mustCalculate = mustCalculate;
    }
}
