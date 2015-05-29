/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.controller.account;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.web.controller.account.validator.CustomerAddressValidator;
import org.broadleafcommerce.profile.core.domain.*;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.util.List;

/**
 * An abstract controller that provides convenience methods and resource declarations for its children.
 *
 * Operations that are shared between controllers that deal with Customer Addresses belong here.
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class AbstractCustomerAddressController extends BroadleafAbstractController {

    private static final Log LOG = LogFactory.getLog(AbstractCustomerAddressController.class);

    protected static String customerAddressesView = "account/manageCustomerAddresses";
    protected static String customerAddressesRedirect = "redirect:/account/addresses";

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blAddressService")
    protected AddressService addressService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Resource(name = "blCustomerAddressValidator")
    protected CustomerAddressValidator customerAddressValidator;

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blISOService")
    protected ISOService isoService;

    /**
     * Initializes some custom binding operations for the managing an address.
     * More specifically, this method will attempt to bind state and country
     * abbreviations to actual State and Country objects when the String
     * representation of the abbreviation is submitted.
     *
     * @param request
     * @param binder
     * @throws Exception
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {

        /**
         * @deprecated - address.setState() is deprecated in favor of ISO standardization
         * This is here for legacy compatibility
         */
        binder.registerCustomEditor(State.class, "address.state", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (StringUtils.isNotEmpty(text)) {
                    State state = stateService.findStateByAbbreviation(text);
                    setValue(state);
                } else {
                    setValue(null);
                }
            }
        });

        /**
         * @deprecated - address.setCountry() is deprecated in favor of ISO standardization
         * This is here for legacy compatibility
         */
        binder.registerCustomEditor(Country.class, "address.country", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (StringUtils.isNotEmpty(text)) {
                    Country country = countryService.findCountryByAbbreviation(text);
                    setValue(country);
                } else {
                    setValue(null);
                }
            }
        });

        binder.registerCustomEditor(ISOCountry.class, "address.isoCountryAlpha2", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (StringUtils.isNotEmpty(text)) {
                    ISOCountry isoCountry = isoService.findISOCountryByAlpha2Code(text);
                    setValue(isoCountry);
                }else {
                    setValue(null);
                }
            }
        });

        binder.registerCustomEditor(Phone.class, "address.phonePrimary", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                Phone phone = new PhoneImpl();
                phone.setPhoneNumber(text);
                setValue(phone);
            }

        });

        binder.registerCustomEditor(Phone.class, "address.phoneSecondary", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                Phone phone = new PhoneImpl();
                phone.setPhoneNumber(text);
                setValue(phone);
            }

        });

        binder.registerCustomEditor(Phone.class, "address.phoneFax", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                Phone phone = new PhoneImpl();
                phone.setPhoneNumber(text);
                setValue(phone);
            }

        });
    }

    protected List<State> populateStates() {
        return stateService.findStates();
    }

    protected List<Country> populateCountries() {
        return countryService.findCountries();
    }

    protected List<CustomerAddress> populateCustomerAddresses() {
        return customerAddressService.readActiveCustomerAddressesByCustomerId(CustomerState.getCustomer().getId());
    }

    public String getCustomerAddressesView() {
        return customerAddressesView;
    }

    public String getCustomerAddressesRedirect() {
        return customerAddressesRedirect;
    }

}
