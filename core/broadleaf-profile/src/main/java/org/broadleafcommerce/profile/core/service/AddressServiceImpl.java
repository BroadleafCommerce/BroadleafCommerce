/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.profile.core.dao.AddressDao;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.service.exception.AddressValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service("blAddressService")
public class AddressServiceImpl implements AddressService {

    @Resource(name="blAddressDao")
    protected AddressDao addressDao;

    @Resource(name = "blModuleConfigurationService")
    protected ModuleConfigurationService moduleConfigService;

    @Resource(name = "blAddressValidationProviders")
    protected List<AddressValidationProvider> providers;

    @Override
    public Address saveAddress(Address address) {
        return addressDao.save(address);
    }

    @Override
    public Address readAddressById(Long addressId) {
        return addressDao.readAddressById(addressId);
    }

    @Override
    public Address create() {
        return addressDao.create();
    }

    @Override
    public void delete(Address address) {
        addressDao.delete(address);
    }

    @Override
    public List<Address> validateAddress(Address address) throws AddressValidationException {

        if (providers != null && !providers.isEmpty()) {

            List<ModuleConfiguration> moduleConfigs = moduleConfigService.findActiveConfigurationsByType(ModuleConfigurationType.ADDRESS_VERIFICATION);

            if (moduleConfigs != null && !moduleConfigs.isEmpty()) {
                //Try to find a default configuration
                ModuleConfiguration config = null;
                for (ModuleConfiguration configuration : moduleConfigs) {
                    if (configuration.getIsDefault()) {
                        config = configuration;
                        break;
                    }
                }

                if (config == null) {
                    //if there wasn't a default one, use the first active one...
                    config = moduleConfigs.get(0);
                }

                for (AddressValidationProvider provider : providers) {
                    if (provider.canRespond(config)) {
                        return provider.validateAddress(address, config);
                    }
                }
            }
        }
        throw new AddressValidationException("No providers were configured to handle address validation");
    }

}