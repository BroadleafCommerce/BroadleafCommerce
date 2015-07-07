/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.pricing.service;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;
import org.broadleafcommerce.core.pricing.service.tax.provider.TaxProvider;
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

        //Try to find a default configuration
        ModuleConfiguration config = null;
        if (configurations != null) {
            for (ModuleConfiguration configuration : configurations) {
                if (configuration.getIsDefault()) {
                    config = configuration;
                    break;
                }
            }

            if (config == null && CollectionUtils.isNotEmpty(configurations)) {
                //if there wasn't a default one, use the first active one...
                config = configurations.get(0);
            }
        }

        if (CollectionUtils.isNotEmpty(providers)) {
            for (TaxProvider provider : providers) {
                if (provider.canRespond(config)) {
                    return provider.calculateTaxForOrder(order, config);
                }
            }
        }
        
        // haven't returned anything, nothing must have run
        if (!mustCalculate) {
            return order;
        }
        throw new TaxException("No eligible tax providers were configured.");
    }

    @Override
    public Order commitTaxForOrder(Order order) throws TaxException {

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
                        return provider.commitTaxForOrder(order, config);
                    }
                }
            }
        }
        if (!mustCalculate) {
            return order;
        }
        throw new TaxException("No eligible tax providers were configured.");
    }

    @Override
    public void cancelTax(Order order) throws TaxException {
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
                        provider.cancelTax(order, config);
                        return;
                    }
                }
            }
        }
        if (mustCalculate) {
            throw new TaxException("No eligible tax providers were configured.");
        }
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
