/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.profile.core.dao.AddressDao;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.service.exception.AddressVerificationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Service("blAddressService")
public class AddressServiceImpl implements AddressService {

    protected boolean mustValidateAddresses = false;

    @Resource(name="blAddressDao")
    protected AddressDao addressDao;

    @Resource(name = "blModuleConfigurationService")
    protected ModuleConfigurationService moduleConfigService;

    @Resource(name = "blAddressVerificationProviders")
    protected List<AddressVerificationProvider> providers;

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Address saveAddress(Address address) {
        return addressDao.save(address);
    }

    @Override
    public Address readAddressById(Long addressId) {
        return addressDao.readAddressById(addressId);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Address create() {
        return addressDao.create();
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void delete(Address address) {
        addressDao.delete(address);
    }

    @Override
    public List<Address> verifyAddress(Address address) throws AddressVerificationException {
        if (address.getStandardized() != null && Boolean.TRUE.equals(address.getStandardized())) {
            //If this address is already standardized, don't waste a call.
            ArrayList<Address> out = new ArrayList<Address>();
            out.add(address);
            return out;
        }

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

                for (AddressVerificationProvider provider : providers) {
                    if (provider.canRespond(config)) {
                        return provider.validateAddress(address, config);
                    }
                }
            }
        }
        if (mustValidateAddresses) {
            throw new AddressVerificationException("No providers were configured to handle address validation");
        }
        ArrayList<Address> out = new ArrayList<Address>();
        out.add(address);
        return out;
    }

    /**
     * Default is false. If set to true, the verifyAddress method will throw an exception if there are no providers to handle the request.
     * @param mustValidateAddresses
     */
    public void setMustValidateAddresses(boolean mustValidateAddresses) {
        this.mustValidateAddresses = mustValidateAddresses;
    }
}
